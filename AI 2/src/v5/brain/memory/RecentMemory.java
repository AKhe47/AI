package v5.brain.memory;

import v5.brain.memory.data.*;
import v5.unit.*;
import v5.unit.type.*;
import v5.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 按输入的时间顺序记录输入
 * 后输入的在前，先输入的在后
 * */
public class RecentMemory {

    private RecentData recentData;
    private UsualMomery usualMomery;

    private static int ADD = Util.ADD;
    private static int MINUS = Util.MINUS;
    private static int EDGE = Util.EDGE;
    private static int TIMES = Util.TIMES;
    private static int BOTTOM = Util.BOTTOM;
    private static int INIT = Util.INIT;
    private static int MAX_LENGTH = 256;

    /**
     * 用于判断当前语境
     * */
    private List<Soul> souls;
    private int[] relation;

    public RecentMemory(){
        this.recentData = new RecentData();
        this.usualMomery = new UsualMomery();
        this.souls = new ArrayList<>(MAX_LENGTH);
        relation = new int[MAX_LENGTH];
        for(int i = 0; i < MAX_LENGTH; i++){
            relation[i] = -1;
        }
    }

    /**
     * 初步找到相同的开头相同的soul
     * 然后寻找可能存在的更大的soul
     *
     * 得到的body，肯定是当前情况下可以找到的最长soul集
     * */
    public Body search(Element[] elements){
        List<Soul> souls = new ArrayList<>(elements.length);
        for(int i = 0; i < elements.length; i++){
            Soul soul = search(elements[i]);
            if(soul == null){
                soul = new Soul(elements[i]);
            }else{
                Soul start = soul;
                while (start != null){
                    soul = start;
                    List<Soul> wholes = start.getWholes();
                    boolean isMatch = false;
                    for(Soul whole : wholes){
                        int size = whole.getElement().size();
                        Element temp = elements[i].getCopy();
                        for(int j = i + 1; temp.size() < size && j < elements.length; j++){
                            temp = temp.addLast(elements[j]);
                        }
                        if(temp.equals(whole.getElement())){
                            start = whole;
                            i = i + size - 1;
                            isMatch = true;
                            break;
                        }
                    }
                    if(!isMatch){
                        start = null;
                    }
                }
            }
            souls.add(soul);
        }
        Body result = souls.get(0).getSame(new Body(souls));
        if(result == null){
            result = new Body(souls);
            result.findWholes();
            result.addSelf();
        }
        if(recentData.size() > 1){
            recentData.getLatest().addNext(result);
            result.addPre(recentData.getLatest());
            recentData.setLast(recentData.getLatest());
        }
        recentData.addData(result);
        return result;
    }


    /**
     * 通过上下文
     * 计算目前的语境
     * */
    public Spirit calDirect(Body body){
        List<Soul> target = body.getSouls();

        for(int i = 0; i < target.size(); i++){
            boolean isMatch = false;
            for(int j = 0; j < souls.size(); j++){
                if(souls.get(j).equals(target.get(i))){
                    relation[j] += ADD;
                    if(relation[j] > EDGE){
                        relation[j] = relation[j] + TIMES * ADD;
                    }
                    isMatch = true;
                    break;
                }
            }
            if(isMatch){
                break;
            }
            boolean isCal = false;
            for(int j = 0; j < relation.length && j < souls.size(); j++){
                if(relation[j] == BOTTOM){
                    souls.set(j, target.get(i));
                    relation[j] = INIT;
                    isCal = true;
                    break;
                }
            }
            if(!isCal){
                souls.add(target.get(i));
                relation[souls.size() - 1] = INIT;
            }
        }
        for(int i = 0; i < relation.length; i++){
            if(target.indexOf(souls.get(i)) >= 0){
                continue;
            }
            relation[i] = relation[i] <= BOTTOM ? BOTTOM : relation[i] - MINUS;
        }

        Spirit spirit = new Spirit(body);
        return spirit;
    }

    /**
     * 通过相邻语境改进spirit
     * */
    public void approve(Body body){
        if(recentData.size() > 1){
            body.processSpirit(recentData.getLast());
        }else {
            body.processSpirit(null);
        }
    }

    /**
     * 寻找soul开头相同的位置
     * */
    private Soul search(Element element){
        Soul result =  recentData.search(element);
        if(result != null){
            return result;
        }
        return usualMomery.search(element);
    }

}
