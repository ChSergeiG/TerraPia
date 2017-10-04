package ru.chsergeyg.terrapia.server.runnable;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;
import ru.chsergeyg.terrapia.server.Init;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialRunnable implements Runnable {
    private static String state;

    @Override
    public void run() {
        state = "GFWGKKI uhgeiglh iurgherh KGKJG";
        Init.getLogger(getClass().getName()).info("SerialRunnable started");
//        try {
//            initSerial();
//        } catch (Exception e) {
//            Init.getLogger(getClass().getName()).warning(e.toString());
//        }
    }

    private void initSerial() throws Exception {
        final Serial serial = SerialFactory.createInstance();
        final SerialConfig config = new SerialConfig();
        config.device(Serial.FIRST_USB_COM_PORT);
        config.baud(Baud._9600);
        config.dataBits(DataBits._8);
        config.flowControl(FlowControl.NONE);
        config.parity(Parity.NONE);
        config.stopBits(StopBits._1);
        serial.open(config);
        BufferedReader brd = new BufferedReader(new InputStreamReader(serial.getInputStream()));
        while (!Thread.currentThread().isInterrupted()) {
            state = brd.readLine();
        }
    }

  public   static String getState() {
        Init.getLogger(SerialRunnable.class.getName()).info(state);
        return state;
    }
}
