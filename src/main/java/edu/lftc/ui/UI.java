package edu.lftc.ui;

import edu.lftc.controller.Controller;

import java.util.Scanner;

/**
 * Created by Melisa AM on 28.12.2016.
 */
public class UI {
    private Controller controller;
    private Scanner scanner;

    public UI(Controller controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in).useDelimiter("\\n");
    }

    private void menu() {
        System.out.println("Choose your options: \n");
        System.out.println("1) Read grammar from file ");
        System.out.println("2) Get states test ");
    }

    public void run() {
        String opt;
        while (true) {
            menu();
            opt = scanner.next();
            switch (opt) {
                case "1":
                    controller.readGrammarFromFile();
                    break;
                case "2":
                    controller.getStates(controller.getGrammar());
                default:
                    System.exit(0);
            }
        }
    }

}

