package edu.lftc;

import edu.lftc.controller.Controller;
import edu.lftc.ui.UI;

public class Main {

    public static final String SEQUENCE_TO_CHECK = "a b b c";

    public static void main(String[] args) {
        // write your code here
        UI ui = new UI(new Controller("C:\\Users\\ysyh\\IdeaProjects\\lftc_lab4\\src\\main\\resources\\grammat.txt"));
        ui.run();
    }
}
