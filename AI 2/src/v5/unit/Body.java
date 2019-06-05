package v5.unit;

import v5.unit.type.Element;
import v5.util.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示具体的语境，由多个Soul组成，用于表达Soul之间的关系
 * Body排列为时间顺序
 * */

public class Body {

    /**
     * 某一个具体的语境
     * */
    private List<Soul> souls;

    /**
     * 此语境中的主题、重点
     * 可根据主体，进行之后的操作
     * 其中包含的soul可源自于其他
     * 通过上下文语境进行制作
     * */
    private Spirit subject;

    /**
     * 具体语境中，接在此body之后的body
     * 以及此body与所有nextBody的关系
     * */
    private List<Body> nextBody;

    /**
     * 具体语境中，在此body之前的body
     * 此body与所有preBody的关系
     * */
    private List<Body> preBody;

    private static int ADD = Util.ADD - 1;
    private static int MINUS = Util.MINUS;
    private static int EDGE = Util.EDGE;
    private static int TIMES = Util.TIMES;
    private static int BOTTOM = Util.BOTTOM;
    private static int INIT = Util.INIT;

    public Body(List<Soul> souls){
        this.souls = new ArrayList<>(souls);
        this.subject = null;
        this.nextBody = new ArrayList<>();
        this.preBody = new ArrayList<>();
        this.subject = null;
    }

    public List<Soul> getSouls() {
        return souls;
    }

    public void setSouls(List<Soul> souls) {
        this.souls = souls;
    }

    public Spirit getSubject() {
        return subject;
    }

    public void setSubject(Spirit subject) {
        this.subject = subject;
    }

    public int getIndex(Soul soul){
        return souls.indexOf(soul);
    }


    /**
     * 比较body的组成是否和此body相同
     * */
    public boolean equals(Body body){
        if(body.getSouls().size() != souls.size()){
            return false;
        }
        List<Soul> temp = body.getSouls();
        for(int i = 0; i < souls.size(); i++){
            if(!souls.get(i).equals(temp.get(i))){
                return false;
            }
        }
        return true;
    }


    /**
     * 匹配策略：
     *
     * 返回值：
     * 0：完全一致
     * 1：这两个body除了在某个位置的soul不一样，其他完全一致
     * 2：这两个body至少存在分别存在一个soul，这两个soul的匹配值小于等于2
     * 3：不同
     * 4：无关
     * */
    public int match(Body body){
        if(equals(body)){
            return 0;
        }

        int diffCount = 0;
        List<Soul> bSoul = body.getSouls();
        if(souls.size() == bSoul.size()){
            for(int i = 0; i < souls.size(); i++){
                if(!souls.get(i).equals(bSoul.get(i))){
                    diffCount++;
                }
            }
        }
        if(diffCount == 1){
            return 1;
        }

        for(int i = 0; i < souls.size(); i++){
            for(int j = 0; j < bSoul.size(); j++){
                if(souls.get(i).match(bSoul.get(j)) < 4){
                    return 2;
                }
            }
        }

        return 4;
    }

    /**
     * 搜索
     * 查询语句中的元素对应的soul
     * */
    public Soul search(Element element){
        for(Soul s : souls){
            Soul result = s.search(element);
            if(result != null){
                return result;
            }
        }
        return null;
    }

    public void addNext(Body body){
        addBodyRel(body, nextBody);
    }

    public void addPre(Body body){
        addBodyRel(body, preBody);
    }

    /**
     * 将自己添加入组成自己的soul中
     * */
    public void addSelf(){
        for(Soul s : souls){
            s.addBody(this);
        }
    }

    public void findWholes(){
        int index = 0;
        int length = souls.size();
        while(index < length){
            Soul soul = souls.get(index);
            int size = soul.findWhole(this);
            index += 1;
            length = souls.size();
        }
    }

    /**
     * 此Body中是否包含target
     * */
    public Soul isContain(Soul target){
        for(Soul soul : souls){
            if(soul.match(target) <= 1){
                return soul;
            }
        }
        return null;
    }

    /**
     * 得到此body的回应
     * 先寻找nextBody最相关的body
     * 若没有找到
     * */
    public Body response(){
        List<Spirit> result = new ArrayList<>();
        result.add(subject);
        List<Spirit> thisMeaning = subject.getMeaning();
        Body lastBody = preBody.get(preBody.size() - 1);
        List<Spirit> lastMeaning = lastBody.getSubject().getMeaning();
        for(Spirit st : thisMeaning){
            for(Spirit sl : lastMeaning){
                if(st.equal(sl) && result.indexOf(st) < 0){
                    result.add(st);
                }
            }
        }
        int maxRel = 0;
        Body bodyRel = null;
        for(Body b : nextBody){
            List<Spirit> nextSpirit = b.getSubject().getMeaning();
            int count = 0;
            for(Spirit sn : nextSpirit){
                for(Spirit sr : result){
                    if(sn.equal(sr)){
                        count++;
                    }
                }
            }
            if(maxRel < count){
                maxRel = count;
                bodyRel = b;
            }
        }
        if(bodyRel == null){
            return new Body(subject.getPoint());
        }
        return bodyRel;
    }

    /**
     * 通过对比此body和预测body的区别
     * 修正此body的subject
     * 增加此body的含义
     * */
    public void enhance(Body predictResponse, Body realResponse){
        List<Spirit> pSs = predictResponse.getSubject().getMeaning();
        List<Spirit> rSs = realResponse.getSubject().getMeaning();
        List<Spirit> same = new ArrayList<>();
        for(Spirit sp : pSs){
            for(Spirit sr : rSs){
                if(sp.equal(sr) && same.indexOf(sr) < 0){
                    same.add(sr);
                    break;
                }
            }
        }
        for(Spirit ss : rSs){
            if(same.indexOf(ss) < 0){
                subject.addMeaning(ss);
            }
        }
    }

    /**
     *  查看是否可以找到从target到此body含义的路径
     * */
    public List<Soul> findPathTo(Soul target){
        return subject.isContain(target);
    }

    /**
     * 产生、加工产生spirit
     * */
    public void processSpirit(Body pre){
        if(subject == null){
            subject = new Spirit(this);
            subject.addSelf();
        }
        if(pre != null){
            subject.approveByPre(pre);
        }
    }

    /**
     * 用于向两个list中添加数据的方法
     * */
    private void addBodyRel(Body body, List<Body> list){
        int index = list.indexOf(body);
        if(index < 0){
            list.add(body);
        }else if(index < list.size() - 1){
            list.remove(index);
            list.add(body);
        }
    }



}
