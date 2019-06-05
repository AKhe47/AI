package v5.brain.memory.data;

import v5.unit.*;
import v5.unit.type.Element;

import java.util.*;

/**
 * 用于存放最近使用的body
 * */
public class RecentData {

    /**
     * 所有的body
     * */
    private List<Body> bodies;

    private Body last;

    /**
     * 最多存放body的个数
     * */
    private static int MAX_LENGTH = 1024;

    public RecentData(){
        this.bodies = new ArrayList<>(MAX_LENGTH);
    }

    /**
     * 添入最新输入的body，并放在开头
     * */
    public void addData(Body body){
        int index = bodies.indexOf(body);
        if(index == 0){
            return;
        }else if(index > 0){
            bodies.remove(index);
            bodies.add(0, body);
            return;
        }
        if(bodies.size() < MAX_LENGTH){
            bodies.add(0, body);
        }else{
            bodies.remove(bodies.size() - 1);
            bodies.add(0, body);
        }
    }

    public void setLast(Body body){
        this.last = body;
    }

    public Body getLast(){
        return this.last;
    }

    public Body getLatest(){
        return bodies.get(0);
    }

    public Soul search(Element element){
        for(Body b : bodies){
            Soul temp = b.search(element);
            if(temp != null){
                return temp;
            }
        }
        return null;
    }

    public int size(){
        return bodies.size();
    }

    public Body get(int index){
        return bodies.get(index);
    }

}
