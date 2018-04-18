#include "TroykaDHT11.h"
#include "Terr.h"

#define HUM A0
#define LUX A1

#define SYSTEM 13

bool day = false;
bool bgM = false;
bool hum = false;
bool smM = false;
bool lmp = false;
int counter = 30;
int hourCounter = 1800;
int hourLux = 0;

TPin bg_heater(5, bgM);
TPin humid(6, hum);
TPin s_heater(7, smM);
TPin lamp(8, lmp);
TPin system(13, false);

DHT11 dht(HUM);

void setup() {
  dht.begin();
  pinMode(LUX, INPUT);
  Serial.begin(9600);
}

void loop() {
  // get sensor data
  int check;
  int humidity;
  int temperature;
  int lux = analogRead(LUX);
  switch (dht.read()) {
    case DHT_OK:
      temperature = dht.getTemperatureC();
      humidity = dht.getHumidity();
      break;
    case DHT_ERROR_CHECKSUM:
      Serial.print("Checksum error; ");
      temperature = -1;
      humidity = -1;
      break;
    case DHT_ERROR_TIMEOUT:
      Serial.print("Timeout error; ");
      temperature = -1;
      humidity = -1;
      break;
    default:
      Serial.print("Unknown error; ");
      temperature = -1;
      humidity = -1;
      break;
  }

  // every 1800 cycles ~ 1 hour
  if (hourCounter >= 1800) {
    lamp.setState(false);
    delay(1000);
    hourLux = analogRead(LUX);
    lmp = hourLux < 477;
    day = lmp;
    // large heater
    bgM = !lmp;
    lamp.setState(lmp);
    bg_heater.setState(bgM);
    hourCounter = 0;
  }

  // every 30 cycles ~ 1 minute
  if (counter >= 30) {
    // humidifier
    if (humidity >= 0) {
      if (humidity < 50) {
        hum = true;
      } else {
        hum = false;
      }
    } else if (humidity == -1 && (hourCounter < 60 || (hourCounter >= 900 && hourCounter < 960))) {
      hum = true;
    } else {
      hum = false;
    }
    // small heater
    smM = temperature <= 27;
    // write pin states
    humid.setState(hum);
    s_heater.setState(smM);
    counter = 0;
  }

  // write values into serial console
  char buff [98];
  int i = sprintf(buff, "[%2d/30 %4d/1800] temperature:%-2d humidity:%-3d lux:%-4d hourlux:%-4d [bgM]%d [hum]%d [smM]%d [lux]%d\n\0", counter, hourCounter, temperature, humidity, lux, hourLux, bg_heater.getState(), humid.getState(), s_heater.getState(), lamp.getState());
  for (int j = 0; j < i; j++) {
    Serial.print(buff[j]);
  }

  // increase counters and delay
  counter++;
  hourCounter++;
  delay(2000);
}
