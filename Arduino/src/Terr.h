#ifndef __TERR__
#define __TERR__

#include "TroykaDHT11.h"
#include <Arduino.h>

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
  digitalWrite(_pin, _state?LOW:HIGH);
}

void TPin::invert() {
  _state = !_state;
}

void TPin::setState(bool state) {
  _state = state;
  digitalWrite(_pin, state?LOW:HIGH);
}

bool TPin::getState() {
  return _state;
}

#endif
