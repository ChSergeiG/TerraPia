#ifndef __TERR_H_
#define __TERR_H_

#include "troyka.h"
#include <Arduino.h>

#define SERIAL_SPEED 9600
#define TEMP_FAIL -1
#define HUM_FAIL -1
#define DELAY_MS 2000

#define LONG_PERIOD_LENGTH 1800
#define SHORT_PERIOD_LENGTH 30

#define HUM A0
#define LUX A1

#define SYSTEM 13

class TPin {
  public:
    TPin(int pin, bool state);
    void invert();
    void setState(bool state);
    bool getState();
  private:
    int _pin;
    bool _state;
};

TPin::TPin (int pin, bool state) {
  pinMode(pin, OUTPUT);
  _pin = pin;
  _state = state;
  digitalWrite(_pin, _state ? LOW : HIGH);
}

void TPin::invert() {
  _state = !_state;
}

void TPin::setState(bool state) {
  _state = state;
  digitalWrite(_pin, state ? LOW : HIGH);
}

bool TPin::getState() {
  return _state;
}

bool isCounterValid(int c) {
  return c < 60 || (c >= 900 && c < 960);
}

#endif
