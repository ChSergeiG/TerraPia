#include "terr.h"

bool day = false;
bool bgM = false;
bool hum = false;
bool smM = false;
bool lmp = false;
int counter = SHORT_PERIOD_LENGTH;
int hourCounter = LONG_PERIOD_LENGTH;
int dayCounter = 0;
int hourLux = 0;

TPin bg_heater		(5, bgM);
TPin humid		(6, hum);
TPin s_heater		(7, smM);
TPin lamp		(8, lmp);
TPin system		(SYSTEM, false);

DHT11 dht(HUM);

void setup() {
  dht.begin();
  pinMode(LUX, INPUT);
  Serial.begin(SERIAL_SPEED);
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
      Serial.print("CHSUM_ERR; ");
      temperature = TEMP_FAIL;
      humidity = HUM_FAIL;
      break;
    case DHT_ERROR_TIMEOUT:
      Serial.print("TMOUT_ERR; ");
      temperature = TEMP_FAIL;
      humidity = HUM_FAIL;
      break;
    default:
      Serial.print("UNKWN_ERR; ");
      temperature = TEMP_FAIL;
      humidity = HUM_FAIL;
      break;
  }

  // every 1800 cycles ~ 1 hour
  if (hourCounter >= LONG_PERIOD_LENGTH) {
    dayCounter = dayCounter == 24 ? 1 : dayCounter + 1;
    //lamp.setState(false);
    //delay(1000);
    //hourLux = analogRead(LUX);
    //lmp = hourLux < 800;
    //day = lmp;
    // large heater
    lmp = dayCounter > DAY  && dayCounter <= NIGHT;
    bgM = !lmp;
    lamp.setState(lmp);
    bg_heater.setState(bgM);
    hourCounter = 0;
  }

  // every 30 cycles ~ 1 minute
  if (counter >= SHORT_PERIOD_LENGTH) {
    // humidifier
    if (humidity >= 0) {
      if (humidity < 50) {
        hum = true;
      } else {
        hum = false;
      }
    } else if (humidity == HUM_FAIL && isCounterValid(hourCounter)) {
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
  char buff [114];
  int i = sprintf(
    buff,
    "[%2d/%2d %4d/%4d %5d] temperature:%-2d humidity:%-3d lux:%-4d hourlux:%-4d [bgM]%d [hum]%d [smM]%d [lux]%d\0",
    counter,
    SHORT_PERIOD_LENGTH,
    hourCounter,
    LONG_PERIOD_LENGTH,
    dayCounter,
    temperature,
    humidity,
    lux,
    hourLux,
    bg_heater.getState(),
    humid.getState(),
    s_heater.getState(),
    lamp.getState()
  );
  for (int j = 0; j < i; j++) {
    Serial.print(buff[j]);
  }
  Serial.println();

  // increase counters and delay
  counter++;
  hourCounter++;
  delay(DELAY_MS);
}
