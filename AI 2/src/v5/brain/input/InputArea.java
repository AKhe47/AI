package v5.brain.input;

import v5.brain.comprehension.*;
import v5.unit.type.Element;
import v5.unit.type.Word;

import java.io.InputStream;
import java.util.*;

public class InputArea extends Thread{

    private Scanner scanner;
    private ReThink reThink;
    private Think think;
    private Scanner insideScanner;


    public InputArea(InputStream is,
 //                    ReThink reThink,
                     Think think){
        this.scanner = new Scanner(is);
 //       this.reThink = reThink;
        this.think = think;
    }


    public void run(){
        while(scanner.hasNext()){
            String temp = scanner.nextLine();
            String[] strings = temp.split("\n");
            for(String s : strings){
                s = s.trim();
                Element[] elements = wordProcess(s);
                try {
                    if(elements != null){
                        think.addInput(elements);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将一句字串分割成有单个字符组成的element数组
     * */
    private Element[] wordProcess(String s){

        List<Element> elementList = new ArrayList<>();
        for(int i = 0; i < s.length(); i++){
            elementList.add(new Word(s.charAt(i) + ""));
        }
        if(elementList.size() <= 0){
            return null;
        }
        Element[] elements = new Element[elementList.size()];
        for(int i = 0; i < elements.length; i++){
            elements[i] = elementList.get(i);
        }
        return elements;
    }

    private char[] ban = {' ', '\t',':','：','!','！','）','(','（','）','\t','、','/','\\','《','》','@'};

    private boolean isBan(char target){
        for(char c : ban){
            if(c == target){
                return true;
            }
        }
        return false;
    }




}
