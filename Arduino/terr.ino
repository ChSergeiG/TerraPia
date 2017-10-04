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

DHT11 dht(A0);

boolean day;
boolean smM;
boolean bgM;
boolean hum;
int counter;

void setup() {
  dht.begin();
  pinMode(VCC_1, OUTPUT);
  pinMode(VCC_2, OUTPUT);
  pinMode(VCC_3, OUTPUT);
  pinMode(LUX, INPUT);
  pinMode(SYSTEM,OUTPUT);
  digitalWrite(SYSTEM,LOW);
  //digitalWrite(VCC_1, LOW);
  //digitalWrite(VCC_2, HIGH);
  //digitalWrite(VCC_3, LOW);
  day = true;
  smM = false;
  bgM = true;
  hum = false;
  counter = 1000;
  Serial.begin(9600);
}

void loop() {
  // get sensor data
  int check;
  int humidity;
  int temperature;
  int lux = analogRead(LUX);
  check = dht.read();
  Serial.print(counter);
  Serial.print(" ");
  switch (check) {
    case DHT_OK:
      temperature = dht.getTemperatureC();
      humidity = dht.getHumidity();
      Serial.print(temperature);
      Serial.print(" ");
      Serial.print(humidity);
      Serial.print(" ");
      break;
    default:
      Serial.print("Error Error");
      break;
  }
  Serial.println(analogRead(LUX));

  if (counter > 600) {
  day = lux > 600;
    // set season
    // small heater
    if (temperature <= 25 && !smM) {
      smM = true;
      digitalWrite(VCC_3, smM?LOW:HIGH);
    } else {
      smM = false;
      digitalWrite(VCC_3, smM?LOW:HIGH);
    }
    // large heater
    if (day && temperature <= 25) {
      bgM = true;
      digitalWrite(VCC_1, bgM?LOW:HIGH);
    } else if (temperature <= 20) {
      bgM = true;
      digitalWrite(VCC_1, bgM?LOW:HIGH);
    } else if (day && temperature > 20) {
      bgM = false;
      digitalWrite(VCC_1, bgM?LOW:HIGH);
    } else {
      bgM = false;
      digitalWrite(VCC_1, bgM?LOW:HIGH);
    }
    // humidifier
    if (humidity < 20 && !hum) {
      hum = true;
      digitalWrite(VCC_2, hum?LOW:HIGH);
    } else if (humidity > 40 && hum) {
      hum = false;
      digitalWrite(VCC_2, hum?LOW:HIGH);
    }
    counter = 0;
  }  
  counter++;
  delay(1000);
}
