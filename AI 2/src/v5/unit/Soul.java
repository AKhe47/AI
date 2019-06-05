package v5.unit;

import v5.unit.type.Element;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于表示纯粹的概念
 * 只是一个短语而已，或者是其他的一个不涉及到具体语境的元素
 * 相对抽象，只有理解
 * */

public class Soul{

    /**
     * 概念本身
     * */
    private Element element;

    /**
     * 组成此概念的部分，也由概念组成
     * */
    private List<Soul> parts;

    /**
     * 或者是包含此soul的更大、更长的soul
     * */
    private List<Soul> wholes;

    /**
     * 涉及到其的实例
     * */
    private List<Body> bodyList;

    /**
     * 涉及到其的理解
     * */
    private List<Spirit> spiritList;

    /**
     * 通过match2判断此soul可否省略
     * */
    private boolean isNecessary = true;

    private List<Soul> passMatch2;


    public Soul(Element element){
        this.element = element;
        this.bodyList = new ArrayList<>();
        this.parts = new ArrayList<>();
        this.spiritList = new ArrayList<>();
        this.wholes = new ArrayList<>();
        this.passMatch2 = new ArrayList<>();
    }

    public void setNecessary(){
        this.isNecessary = false;
    }

    public boolean getIsNecessary(){
        return isNecessary;
    }

    public Element getElement() {
        return element;
    }

    public List<Body> getBodyList() {
        List<Body> temp;
        synchronized (this){
            temp = new ArrayList<>(bodyList);
        }
        return temp;
    }

    public List<Soul> getParts(){
        return parts;
    }

    public boolean addSpirit(Spirit spirit){
        List<Spirit> spirits;
        synchronized (this){
            spirits = new ArrayList<>(spiritList);
        }
        for(Spirit s : spirits){
            if(spirit.equals(s)){
                return false;
            }
        }
        spirits.add(spirit);
        spiritList = spirits;
        return true;
    }

    /**
     * 返回此soul在body中的长度
     * 由于可能将原body中的soul组合成为长soul，此处返回的是此soul在原body中对应的soul个数
     * */
    public void addBody(Body body){
        List<Body> bodies;
        synchronized (this){
            bodies = new ArrayList<>(bodyList);
        }
        for(Body b : bodies){
            if(body.equals(b)){
                return;
            }
        }

        bodies.add(body);
        bodyList = bodies;
    }

    public boolean removeBody(Body body){
        List<Body> bodies;
        synchronized (this){
            bodies = new ArrayList<>(bodyList);
        }
        boolean temp = bodies.remove(body);
        if(temp){
            bodyList = bodies;
        }
        return temp;
    }

    public boolean removeWhole(Soul whole){
        List<Soul> souls;
        synchronized (this){
            souls = new ArrayList<>(wholes);
        }
        boolean temp = souls.remove(whole);
        if(temp){
            wholes = souls;
        }
        return temp;
    }


    public List<Soul> getWholes(){
        List<Soul> temp;
        synchronized (this){
            temp = new ArrayList<>(wholes);
        }
        return temp;
    }

    public boolean addWholes(Soul soul){
        List<Soul> temp;
        synchronized (this){
            temp = new ArrayList<>(wholes);
        }
        for(Soul s : temp){
            if(s.equals(soul)){
                return false;
            }
        }
        temp.add(soul);
        wholes = temp;
        return true;
    }

    public void setParts(List<Soul> souls){
        this.parts = souls;
    }

    /**
     * 匹配策略：
     * 概念本身是否一致
     * */
    public boolean equals(Element element){
        if(element.equals(this.element)){
            return true;
        }
        return false;
    }

    public boolean equals(Soul soul){
        if(soul.getElement().equals(this.element)){
            return true;
        }
        return false;
    }

    /**
     * 得到与此soul同时出现的soul的所对应的body
     * */
    public Body getRelativeBody(Soul soul){
        for(Body body : bodyList){
            if(body.getIndex(soul) >= 0){
                return body;
            }
        }
        return null;
    }

    /**
     * 搜索
     * 查询类似的soul，匹配开头是否相似
     * 首先，查看本身是否一样
     * 然后，查看是否part中有一样的内容
     * 若都没有查询到，就返回null
     * */
    public Soul search(Element element){
        if(equals(element)){
            return this;
        }
        List<Soul> temp;
        synchronized (this){
            temp = new ArrayList<>(parts);
        }
        for(Soul s : temp){
            Soul result = s.search(element);
            if(result != null){
                return s;
            }
        }
        return null;
    }


    /**
     * 得到由此soul开头的、与body内容一致的body
     *
     * 若没找到，返回null
     * */
    public Body getSame(Body body){
        List<Body> temp;
        synchronized (this){
            temp = new ArrayList<>(bodyList);
        }
        for(Body b : temp){
            if(b.getIndex(this) != 0){
                continue;
            }
            if(b.equals(body)){
                return b;
            }
        }
        return null;
    }

    /**
     * 得到此soul之后的soul之后的soul
    */
    public List<Soul> getNNextSoul(Soul next){
        List<Soul> nnexts = new ArrayList<>();
        for(Body b : bodyList){
            int pos = b.getIndex(this);
            if(pos + 2 < bodyList.size() && b.getSouls().get(pos + 1).equals(next)){
                nnexts.add(b.getSouls().get(pos + 2));
            }
        }
        return nnexts;
    }

    /**
     * 得到此soul所对应的soul
     * */
    public List<Soul> getNextSoul(){
        List<Soul> nexts = new ArrayList<>();
        for(Body b : bodyList){
            int pos = b.getIndex(this);
            if(pos + 1 < bodyList.size() && (pos + 1) < b.getSouls().size()){
                nexts.add(b.getSouls().get(pos + 1));
            }
        }
        return nexts;
    }

    /**
     * 匹配策略：
     *
     * 此处的判断更多的是基于语法上的判断，而非语义
     *
     * 返回值：
     * 0：完全一致
     * 1：存在这样的关系：这两个soul中都存在这样的body，这两个body的除了soul的位置外完全一样
     * 2：一个soul为另一个soul的whole或者是part
     * 3：此soul和匹配soul出现在同一个body中
     * 4：不同
     * 5：无关
     * */
    public int match(Soul soul){
        if(soul.equals(this)){
            return 0;
        }
        for(int i = 0; i < bodyList.size(); i++){
            for(int j = 0; j < soul.getBodyList().size(); j++){
                Body thisBody = bodyList.get(i);
                Body soulBody = soul.getBodyList().get(j);
                if(thisBody.equals(soulBody)){
                    continue;
                }
                List<Soul> thisList = thisBody.getSouls();
                List<Soul> soulList = soulBody.getSouls();
                int thisPos = thisList.indexOf(this);
                int soulPos = soulList.indexOf(soul);
                if(thisList.size() == soulList.size() && thisPos == soulPos){
                    boolean isNeed = true;
                    for(int m = 0; m < thisList.size(); m++){
                        if(!thisList.get(m).equals(soulList.get(m))){
                            if(m != thisPos){
                                isNeed = false;
                                break;
                            }
                        }
                    }
                    if(isNeed){
                        return 1;
                    }
                }

            }
        }

        for(int i = 0; i < parts.size(); i++){
            if(parts.get(i).equals(soul)){
                return 2;
            }
        }
        for(int i = 0; i < wholes.size(); i++){
            if(wholes.get(i).equals(soul)){
                return 2;
            }
        }
/*
        for(Body body : bodyList){
            for(Soul bs : body.getSouls()){
                if(bs.match(soul) == 0){
                    return 3;
                }
            }
        }

*/
        return 4;
    }

    /**
     * 匹配0：
     * 当此soul和target完全相同时，返回true
     * */
    public boolean match_0(Soul target){
        if(this.equals(target)){
            return true;
        }
        return false;
    }

    /**
     * 匹配1：
     * 当此soul和target存在包含关系，即一个是另一个的whole或者是part
     * 若target为part，返回1
     * 若target为whole，返回2
     * 若都不是。返回0
     * */
    public int match_1(Soul target){
        for(int i = 0; i < parts.size(); i++){
            if(parts.get(i).equals(target)){
                return 1;
            }
        }
        for(int i = 0; i < wholes.size(); i++){
            if(wholes.get(i).equals(target)){
                return 2;
            }
        }
        return 0;
    }

    /**
     * 匹配2：
     * 当存在这样的关系：这两个soul中都存在这样的body，这两个body的除了soul的位置外完全一样，此时这两个soul处于可比状态
     * */
    public Body[] match_2(Soul target){
        if(getElement().isSpecial() || target.getElement().isSpecial()){
            return null;
        }

        Body[] bodies = new Body[2];
        for(Body bs : bodyList){
            for(Body bt : target.getBodyList()){
                boolean isMatch = true;
                if(bs.equals(bt)){
                    continue;
                }
                List<Soul> thisList = bs.getSouls();
                List<Soul> targetList = bt.getSouls();

                int thisPos = thisList.indexOf(this);
                int targetPos = targetList.indexOf(target);
                if(thisList.size() != targetList.size() || thisPos != targetPos){
                    continue;
                }
                for(int i = 0; i < thisPos; i++){
                    if(!targetList.get(i).equals(thisList.get(i)) && i != thisPos){
                        isMatch = false;
                        break;
                    }
                }
                if(isMatch){
                    bodies[0] = bt;
                    bodies[1] = bs;
                    return bodies;
                }
            }
        }
        return null;
    }

    /**
     * 匹配3：
     * 此soul与target是否有联系
     * */
    public boolean match_3(Soul target){
        Body[] bodies = match_2(target);
        if(bodies == null){
            return false;
        }
        return bodies[0].getSubject().match_1(bodies[1].getSubject());
    }

    /**
     * 匹配4：
     * 此soul与target是否有相似
     * */
    public boolean match_4(Soul target){
        Body[] bodies = match_2(target);
        if(bodies == null){
            return false;
        }
        return bodies[0].getSubject().match_2(bodies[1].getSubject());
    }


    /**
     * 产生长soul的部分
     *
     * 通过查找相同子串，得到更加长、大规模的soul
     * 此处为修改target中的数据
     *
     * 还需要修改匹配的body中的spirit
     * */
    public int findWhole(Body target){
        if(this.getElement().isSpecial()){
            return 0;
        }
        List<Soul> targetSoul = target.getSouls();
        int targetPos = targetSoul.indexOf(this);
        List<Soul> newWholes = new ArrayList<>();
        for(Body b : bodyList){
            List<Soul> bSoul = b.getSouls();
            int bPos = bSoul.indexOf(this);
            int count = 1;
            while((count + bPos) < bSoul.size() && (count + targetPos) < targetSoul.size() && !targetSoul.get(targetPos + count).getElement().isSpecial() && targetSoul.get(targetPos + count).equals(bSoul.get(bPos + count))){
                count++;
            }
            if(count > 1){
                List<Soul> part = new ArrayList<>();
                part.add(this);
                Element te = this.getElement().getCopy();
                for(int i = 1; i < count; i++){
                    part.add(targetSoul.get(targetPos + i));
                    te = te.addLast(targetSoul.get(targetPos + i).getElement());
                }
                for(int i = 0; i < count; i++){
                    bSoul.remove(bPos);
                    this.removeBody(b);
                }

                Soul whole = null;
                boolean hasWhole = false;
                for(int i = 0; i < newWholes.size(); i++){
                    if(newWholes.get(i).equals(te)){
                        whole = newWholes.get(i);
                        hasWhole = true;
                        break;
                    }
                }
                if(!hasWhole){
                    whole = new Soul(te);
                    whole.setParts(part);
                    newWholes.add(whole);
                    this.addWholes(whole);
                }
                bSoul.add(bPos, whole);
                b.getSubject().merge(bSoul, bPos, count);
                whole.addBody(b);
                this.removeBody(b);
            }
        }
        if(newWholes.size() < 1){
            return 1;
        }
        sort(newWholes);
        sort(wholes);
        int longest = newWholes.get(newWholes.size() - 1).getParts().size();
        for(int i = 0; i < longest; i++){
            targetSoul.remove(targetPos);
        }
        targetSoul.add(targetPos, newWholes.get(newWholes.size() - 1));
        for(int i = 0; i < newWholes.size(); i++){
            for(int j = i + 1; j < wholes.size(); j++){
                Soul s1 = newWholes.get(i);
                Soul s2 = wholes.get(j);
                Soul longer, shorter;
                if(s1.getElement().size() == s2.getElement().size()){
                    break;
                }else if(s1.getElement().size() > s2.getElement().size()){
                    longer = s1;
                    shorter = s2;
                }else{
                    longer = s2;
                    shorter = s1;
                }
                List<Soul> lw = longer.getParts();
                int posL = lw.indexOf(this);
                Element le = lw.get(posL).getElement().getCopy();
                for(int k = 1; k < lw.size(); k++){
                    le = le.addLast(lw.get(k + posL).getElement());
                    if(shorter.equals(le)){
                        for(int m = 0; m <= k; m++){
                            lw.get(posL).removeWhole(longer);
                            lw.get(posL).addWholes(shorter);
                            lw.remove(posL);
                        }
                        lw.add(posL, shorter);
                        shorter.addWholes(longer);
                        break;
                    }
                }
            }
        }
        sort(wholes);
        return longest;
    }

    /**
     * 排序，从小到大
     * */
    private void sort(List<Soul> list){
        for(int i = 0; i < list.size(); i++){
            for(int j = i + 1; j < list.size(); j++){
                List<Soul> parts1 = list.get(i).getParts();
                List<Soul> parts2 = list.get(j).getParts();
                if(parts1.size() > parts2.size()){
                    Soul temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

}
