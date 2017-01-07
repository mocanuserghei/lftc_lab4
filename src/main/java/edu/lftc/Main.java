package edu.lftc;

import edu.lftc.controller.Controller;
import edu.lftc.ui.UI;

public class Main {

    public static void main(String[] args) {
        // write your code here
        UI ui = new UI(new Controller("C:\\LFTC\\LR0parser\\LR0parser\\src\\main\\resources\\grammat.txt"));
        ui.run();
    }
}
