package ru.chsergeyg.terrapia.server;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.concurrent.TimeUnit;

public class SerialRunnable implements Runnable {
    private static String state;

    @Override
    public void run() {
        //initSerial("/dev/ttyUSB0");
        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                Init.getLogger(SerialRunnable.class.getName()).warning(e.toString());
//            }
        }
    }

    private void initSerial(String portID) {
       SerialPort sPort = com.fazecast.jSerialComm.SerialPort.getCommPort(portID);
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
        Init.getLogger(SerialRunnable.class.getName()).info(state);
        return state;
    }
}
