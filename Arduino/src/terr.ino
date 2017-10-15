#include "TroykaDHT11.h"

#define RPI_0 8
#define RPI_2 9
#define RPI_3 10
#define RPI_8 2
#define RPI_9 3

#define VCC_1 5  // LARGE HEATER
#define VCC_2 6  // HUMIDIFIER
#define VCC_3 7  // SMALL HEATER

#define HIM A0
#define LUX A1

#define SYSTEM 13

#define UPDATE_DELAY 60

DHT11 dht(HIM);

boolean day = false;
boolean bgM = false;
boolean hum = false;
boolean smM = false;
int counter = UPDATE_DELAY;

void setup() {
  dht.begin();
  pinMode(VCC_1, OUTPUT);
  pinMode(VCC_2, OUTPUT);
  pinMode(VCC_3, OUTPUT);
  pinMode(LUX, INPUT);
  pinMode(SYSTEM,OUTPUT);

  digitalWrite(VCC_1,bgM?LOW:HIGH);
  digitalWrite(VCC_2,hum?LOW:HIGH);
  digitalWrite(VCC_3,smM?LOW:HIGH);
  digitalWrite(SYSTEM,LOW);
  
  Serial.begin(9600);
}

void loop() {
  // get sensor data
  String data;
  int check;
  int humidity;
  int temperature;
  int lux = analogRead(LUX);
  check = dht.read();
  data = data + "[" + counter+"] ";
  switch (check) {
    case DHT_OK:
      temperature = dht.getTemperatureC();
      humidity = dht.getHumidity();
      data = data + temperature + " "+ humidity+ " ";
      break;
    default:
      data = data + "err err ";
      break;
  }
  data = data + lux + " ";
  if (counter > UPDATE_DELAY) {
    day = lux < 600;
    /// set season
    // large heater
    if (day) {
      bgM = temperature <= 25;
    } else {
      bgM = temperature <= 20;
    }
    digitalWrite(VCC_1, bgM?LOW:HIGH);
    
    // humidifier
    if (humidity < 20) {
      hum = true;
    } else if (humidity > 40) {
      hum = false;
    }
    digitalWrite(VCC_2, hum?LOW:HIGH);

    // small heater
    smM = temperature <= 25;
    digitalWrite(VCC_3, smM?LOW:HIGH);

    counter = 0;
  }
  data = data + bgM + " " + hum + " " + smM;
  Serial.println(data);
  counter++;
  delay(1000);
}
