package ru.chsergeyg.terrapia.server;
import com.pi4j.io.gpio.*;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("<--Pi4J--> ... started.");
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin00 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        final GpioPinDigitalOutput pin02 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
        final GpioPinDigitalOutput pin03 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
        pin00.setShutdownOptions(true, PinState.LOW);
        pin02.setShutdownOptions(true, PinState.LOW);
        pin03.setShutdownOptions(true, PinState.LOW);
        int time = Integer.parseInt(args[0]);
        int rnd1, rnd2, rnd3;
        int i = 0;
        do {
            Thread.sleep(time);
            rnd1 = (int) (Math.random() * 100);
            rnd2 = (int) (Math.random() * 100);
            rnd3 = (int) (Math.random() * 100);
            if (rnd1 < 50) pin00.low();
            else pin00.high();
            if (rnd2 < 50) pin02.low();
            else pin02.high();
            if (rnd3 < 50) pin03.low();
            else pin03.high();
            System.out.print(String.format("i = %5d, rnd1 = %2d, rnd2 = %2d, rnd3 = %2d.\n", i, rnd1, rnd2, rnd3));
            i++;
        } while (i < 10000);
        gpio.shutdown();
        System.out.println("Exiting");
    }
}