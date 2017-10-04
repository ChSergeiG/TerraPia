package ru.chsergeyg.terrapia.client;

import java.io.IOException;
import java.net.Socket;

public class ClientCMD{
    private static  Socket sock;
    public static void main(String[] args) {
        System.out.println("Starting console client");
        try {
            go();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void go() throws IOException {
         sock = new Socket("194.87.147.172",60033);
    }
}
