#include "TroykaDHT11.h"
#include "Terr.h"


// #define VCC_1 5  // LARGE HEATER
// #define VCC_2 6  // HUMIDIFIER
// #define VCC_3 7  // SMALL HEATER
// #define VCC_4 8  // LAMP

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
Tpin humid(6, hum);
Tpin s_heater(7, smM);
Tpin lamp(8, lmp);
Tpin system(13, false);

DHT11 dht(HUM);

void setup() {
  dht.begin();
  // pinMode(VCC_1, OUTPUT);
  // pinMode(VCC_2, OUTPUT);
  // pinMode(VCC_3, OUTPUT);
  // pinMode(VCC_4, OUTPUT);
  pinMode(LUX, INPUT);
  // pinMode(SYSTEM,OUTPUT);
  //
  // digitalWrite(VCC_1,bgM?LOW:HIGH);
  // digitalWrite(VCC_2,hum?LOW:HIGH);
  // digitalWrite(VCC_3,smM?LOW:HIGH);
  // digitalWrite(VCC_4,lmp?LOW:HIGH);
  // digitalWrite(SYSTEM,LOW);

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
      Serial.println("Timeout error; ");
      temperature = -1;
      humidity = -1;
      break;
    default:
      Serial.println("Unknown error; ");
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
    bg_heater.setState(bgM);
    humid.setState(hum);
    s_heater.setState(smM);
    lamp.setState(lmp);
    // digitalWrite(VCC_1, bgM?LOW:HIGH);
    // digitalWrite(VCC_2, hum?LOW:HIGH);
    // digitalWrite(VCC_3, smM?LOW:HIGH);
    // digitalWrite(VCC_4, lmp?LOW:HIGH);
    counter = 0;
  }

  // write values into serial console
  char buff [96];
  int i = sprintf(buff, "[%2d/30 %4d/1800] temperature:%-2d humidity:%-3d lux:%-4d hourlux:%-4d [bgM]%d [hum]%d [smM]%d [lux]%d", counter, hourCounter, temperature, humidity, lux, hourLux, bg_heater.getState(), humid.getState(), s_heater.getState(), lamp.getState());
  for (int j = 0; j < i; j++) {
    Serial.print(buff[j]);
  }
  Serial.println();

  // increase counters and delay
  counter++;
  hourCounter++;
  delay(2000);
}
