package com.company;

import com.company.controller.Controller;
import com.company.domain.ISymbol;
import com.company.domain.Item;
import com.company.domain.Nonterminal;
import com.company.domain.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Melisa AM on 28.12.2016.
 */
public class UI {
    Controller controller;
    Scanner scanner;

    public UI(Controller controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in).useDelimiter("\\n");
        ;
    }

    public void menu() {
        System.out.println("Choose your options: \n");
        System.out.println("1)Read grammar from file ");
        System.out.println("2)Get states test ");
    }

    public void run() {
        String opt = "";
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

