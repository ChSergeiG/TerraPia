#include "TroykaDHT11.h"

#define RPI_0 9
#define RPI_2 10
#define RPI_3 11
#define RPI_8 2
#define RPI_9 3

#define VCC_1 5  // LARGE HEATER
#define VCC_2 6  // HUMIDIFIER
#define VCC_3 7  // SMALL HEATER
#define VCC_4 8  // LAMP

#define HIM A0
#define LUX A1

#define SYSTEM 13

DHT11 dht(HIM);

boolean day = false;
boolean bgM = false;
boolean hum = false;
boolean smM = false;
boolean lmp = false;
int counter = 30;
int hourCounter = 1800;
int hourLux = 0;

void setup() {
  dht.begin();
  pinMode(VCC_1, OUTPUT);
  pinMode(VCC_2, OUTPUT);
  pinMode(VCC_3, OUTPUT);
  pinMode(VCC_4, OUTPUT);
  pinMode(LUX, INPUT);
  pinMode(SYSTEM,OUTPUT);

  digitalWrite(VCC_1,bgM?LOW:HIGH);
  digitalWrite(VCC_2,hum?LOW:HIGH);
  digitalWrite(VCC_3,smM?LOW:HIGH);
  digitalWrite(VCC_4,lmp?LOW:HIGH);
  digitalWrite(SYSTEM,LOW);

  Serial.begin(9600);
}

void loop() {
  // get sensor data
  int check;
  int humidity;
  int temperature;
  int lux = analogRead(LUX);
  check = dht.read();
  switch (check) {
    case DHT_OK:
      temperature = dht.getTemperatureC();
      humidity = dht.getHumidity();
      break;
    default:
      temperature = -1;
      humidity = -1;
      break;
  }

  if (hourCounter >= 1800) {
    digitalWrite(VCC_4, HIGH);
    delay(1000);
    hourLux = analogRead(LUX);
    lmp = hourLux < 850;
    day = lmp;
    hourCounter = 0;
  }

  if (counter >= 30) {
    // large heater
    bgM = temperature <= 25;
    // humidifier
    if (humidity < 40) {
      hum = true;
    } else if (humidity > 45) {
      hum = false;
    }
    // small heater
    smM = temperature <= 27;
    digitalWrite(VCC_1, bgM?LOW:HIGH);
    digitalWrite(VCC_2, hum?LOW:HIGH);
    digitalWrite(VCC_3, smM?LOW:HIGH);
    digitalWrite(VCC_4, lmp?LOW:HIGH);
    counter = 0;
  }
  char buff [96];
  int i = sprintf(buff, "[%2d/30 %4d/1800] temperature:%-2d humidity:%-3d lux:%-4d hourlux:%-4d [bgM]%d [hum]%d [smM]%d [lux]%d", counter, hourCounter, temperature, humidity, lux, hourLux, bgM, hum, smM, lmp);
  for (int j = 0; j < i; j++) {
    Serial.print(buff[j]);
  }
  Serial.println();
  counter++;
  hourCounter++;
  delay(2000);
}
