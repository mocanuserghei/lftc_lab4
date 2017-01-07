package com.company;

import com.company.controller.Controller;

public class Main {

    public static void main(String[] args) {
	// write your code here
        UI ui = new UI(new Controller("D:\\Projects\\LR0parser\\src\\grammat.txt"));
        ui.run();
    }
}
