package com.myjo.ordercat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ReadStdin{

    private static final Logger Logger = LogManager.getLogger(ReadStdin.class);


    public static void main(String[] args) {
        DataInputStream in = new DataInputStream(new BufferedInputStream(System.in));
        String s;
        try {
            while((s = in.readLine()).length() != 0)
                Logger.error(s);
            //An empty line terminates the program
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}