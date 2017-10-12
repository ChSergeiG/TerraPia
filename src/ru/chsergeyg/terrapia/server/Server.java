package ru.chsergeyg.terrapia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {
    public static void main(String[] args) {
        Core core = new Core();
        String input = null;
        BufferedReader brd = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("SERVER> ");
            try {
                input = brd.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            switch (input.toLowerCase()) {
                case "start":
                    System.out.print("SERVER/PORT> ");
                    try {
                        core.start(Integer.parseInt(brd.readLine()));
                    } catch (NumberFormatException | IOException e) {
                        System.out.println("Wrong port number.\nSERVER> ");
                    }
                    break;
                case "stop":
                    core.stop();
                    break;
                case "help":
                case "info":
                    printHelp();
                    break;
                case "status":
                    System.out.println(core.status());
                    break;
                case "exit":
                case "quit":
                    System.exit(0);
                default:
                    System.out.println("Wrong input. Type \"help\" to get allowed command list");
                    break;
            }
        }
    }

    private static void printHelp() {
        System.out.println("start|stop|exit|quit|help|info|status");
    }
}
