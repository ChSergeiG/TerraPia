package ru.chsergeyg.terrapia.server;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.logging.Logger;

public class SerialThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(SerialThread.class.getName());
    private static String state;

    private static SerialPort sPort;

    @Override
    public void run() {
        initSerial();
        while (!isInterrupted()) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.warning(e.toString());
            }
        }
    }

    private void initSerial() {
        sPort = com.fazecast.jSerialComm.SerialPort.getCommPort("/dev/ttyUSB0");
        sPort.openPort();
        sPort.addDataListener(new SerialPortDataListener() {
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
            }

            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
                    state = new String(serialPortEvent.getReceivedData());
                }
            }
        });
    }

    static String getStateString() {
        LOGGER.info(state);
        return state;
    }
}
