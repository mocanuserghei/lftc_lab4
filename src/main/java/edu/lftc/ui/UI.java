package edu.lftc.ui;

import edu.lftc.controller.Controller;
import edu.lftc.controller.LRParseTable;

import java.util.List;
import java.util.Queue;
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
        System.out.println("3) Build LR parse table ");
    }

    public void run() {
        try {
            controller.readGrammarFromFile();
            System.out.println("---------------------------");

            LRParseTable lrParseTable = controller.buildLRParseTable(controller.getGrammar());
            System.out.println(lrParseTable);
            System.out.println("---------------------------");

            Queue<Integer> outputStack = controller.checkGrammar(controller.getGrammar());
            System.out.println(outputStack);
            System.out.println("---------------------------");

            List<String> representationString = controller.getRepresentation(outputStack);
            System.out.println(representationString);
        } catch (IllegalArgumentException e) {
            System.out.println("SOMETHING WENT WRONG");
            System.out.println(e.getMessage());
        }
    }

}

