package org.example;

import java.net.*;

import net.wimpi.modbus.*;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.io.*;
import net.wimpi.modbus.net.*;

public class DITest {
    public static void main(String[] args) throws Exception {
        TCPMasterConnection con = null; //the connection
        ModbusTCPTransaction trans = null; //the transaction
        ReadInputDiscretesRequest req = null; //the request
        ReadInputDiscretesResponse res = null; //the response

        InetAddress addr = null; //the slave's address
        int port = Modbus.DEFAULT_PORT;
        int ref = 0; //the reference; offset where to start reading from
        int count = 0; //the number of DI's to read
        int repeat = 5; //a loop for repeating the transaction

        //1. Setup the parameters
        if (args.length < 3) {
            System.exit(1);
        } else {
            try {
                String astr = args[0];
                int idx = astr.indexOf(':');
                if (idx > 0) {
                    port = Integer.parseInt(astr.substring(idx + 1));
                    astr = astr.substring(0, idx);
                }
                addr = InetAddress.getByName(astr);
                ref = Integer.decode(args[1]);
                count = Integer.decode(args[2]);
                if (args.length == 4) {
                    repeat = Integer.parseInt(args[3]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        //2. Open the connection
        con = new TCPMasterConnection(addr);
        con.setPort(port);
        con.connect();

        //3. Prepare the request
        req = new ReadInputDiscretesRequest(ref, count);
        req.setUnitID(50);

        //4. Prepare the transaction
        trans = new ModbusTCPTransaction(con);
        trans.setRequest(req);

        //5. Execute the transaction repeat times
        int k = 0;
        do {
            trans.execute();
            res = (ReadInputDiscretesResponse) trans.getResponse();
            System.out.println("Digital Inputs Status=" + res.getDiscretes().toString());
            k++;
        } while (k < repeat);

        //6. Close the connection
        con.close();
    }//main
}//class DITest