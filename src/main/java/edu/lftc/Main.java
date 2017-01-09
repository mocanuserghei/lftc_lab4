package edu.lftc;

import edu.lftc.controller.Controller;
import edu.lftc.ui.UI;

public class Main {

    public static void main(String[] args) {
        // write your code here
        UI ui = new UI(new Controller("D:\\Projects2016\\SergheiProjLFTC\\lftc_lab4\\src\\main\\resources\\grammat.txt"));
        ui.run();
    }
}
