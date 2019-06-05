package v5.brain;

import v5.brain.comprehension.Think;
import v5.brain.input.InputArea;

import java.util.ArrayList;

public class Start {

    public static void main(String[] args){
        Think think = new Think();
        InputArea inputArea = new InputArea(System.in, think);
        think.start();
        inputArea.start();
    }

}
