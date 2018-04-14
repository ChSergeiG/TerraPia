#include "TroykaDHT11.h"
#include "Terr.h"

#define RPI_0 9
#define RPI_2 10
#define RPI_3 11
#define RPI_8 2
#define RPI_9 3

#define VCC_1 5  // LARGE HEATER
#define VCC_2 6  // HUMIDIFIER
#define VCC_3 7  // SMALL HEATER
#define VCC_4 8  // LAMP

#define HUM A0
#define LUX A1

#define SYSTEM 13

DHT11 dht(HUM);

bool day = false;
bool bgM = false;
bool hum = false;
bool smM = false;
bool lmp = false;
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
    case DHT_ERROR_CHECKSUM:
      Serial.println("Checksum error");
      temperature = -1;
      humidity = -1;
      break;
    case DHT_ERROR_TIMEOUT:
      Serial.println("Time out error");
      temperature = -1;
      humidity = -1;
      break;
    default:
      Serial.println("Unknown error");
      temperature = -1;
      humidity = -1;
      break;
  }

  // every 1800 cycles ~ 1 hour
  if (hourCounter >= 1800) {
    digitalWrite(VCC_4, HIGH);
    delay(1000);
    hourLux = analogRead(LUX);
    lmp = hourLux < 477;
    day = lmp;
    hourCounter = 0;
  }

  // every 30 cycles ~ 1 minute
  if (counter >= 30) {
    // large heater
    bgM = temperature <= 25;
    // humidifier
    if (humidity >= 0) {
      if (humidity < 50) {
        hum = true;
      } else {
        hum = false;
      }
    } else if (humidity == -1 && hourCounter < 60) {
      hum = true;
    } else {
      hum = false;
    }
    // small heater
    smM = temperature <= 27;
    // write pin states
    digitalWrite(VCC_1, bgM?LOW:HIGH);
    digitalWrite(VCC_2, hum?LOW:HIGH);
    digitalWrite(VCC_3, smM?LOW:HIGH);
    digitalWrite(VCC_4, lmp?LOW:HIGH);
    counter = 0;
  }

  // write values into serial console
  char buff [96];
  int i = sprintf(buff, "[%2d/30 %4d/1800] temperature:%-2d humidity:%-3d lux:%-4d hourlux:%-4d [bgM]%d [hum]%d [smM]%d [lux]%d", counter, hourCounter, temperature, humidity, lux, hourLux, bgM, hum, smM, lmp);
  for (int j = 0; j < i; j++) {
    Serial.print(buff[j]);
  }
  Serial.println();

  // increase counters and delay
  counter++;
  hourCounter++;
  delay(2000);
}
