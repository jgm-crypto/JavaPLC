package org.example;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.*;
import net.wimpi.modbus.procimg.*;
import net.wimpi.modbus.ModbusCoupler;

public class TCPSlaveTest {

    //     Simulacion de un dispositivo MODBUS
    // ***************************************

    public static void main(String[] args) {
        ModbusTCPListener listener = null;
        SimpleProcessImage spi = null;
        int port = Modbus.DEFAULT_PORT;
        //1. Set port number from commandline parameter
        if (args != null && args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        //2. Prepare a process image
        spi = new SimpleProcessImage();
        spi.addDigitalOut(new SimpleDigitalOut(true));
        spi.addDigitalOut(new SimpleDigitalOut(false));
        spi.addDigitalIn(new SimpleDigitalIn(false));
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalIn(new SimpleDigitalIn(false));
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addRegister(new SimpleRegister(251));
        spi.addInputRegister(new SimpleInputRegister(45));
        //3. Set the image on the coupler
        ModbusCoupler.getReference().setProcessImage(spi);
        ModbusCoupler.getReference().setMaster(false);
        ModbusCoupler.getReference().setUnitID(15);
        //4. Create a listener with 3 threads in pool
        listener = new ModbusTCPListener(3);
        listener.setPort(port);
        listener.start();
    }
}