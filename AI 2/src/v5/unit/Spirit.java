package v5.unit;


import java.util.*;

/**
 * 也由Soul组成
 * 用于表达理解、寻找Body中的内在规律
 * */

public class Spirit {

    /**
     * 组成理解的Soul
     * */
    private List<Soul> souls;

    /**
     * 使用矩阵表示原句中的关系
     * souls是通过矩阵判断，处理之后得到的一个新的soul组合
     * */
    private int[] relation;

    /**
     * 所属的body
     * */
    private Body master;

    /**
     * 相关含义
     * */
    private List<Spirit> meaning;

    /**
     * 与此Spirit所属body相邻的body
     * */
    private Body nextBody = null;
    private Body preBody = null;

    public Spirit(List<Soul> souls, Body master){
        this.souls = souls;
        this.relation = new int[souls.size()];
        this.master = master;
        for(int i = 0; i < relation.length; i++){
            relation[i] = 0;
        }
        makeRelation();

    }

    public Spirit(Body body){
        this(body.getSouls(), body);
    }

    public List<Soul> getSouls(){return souls;}

    public int[] getRelation(){return relation;}

    public void setNext(Body body){
        this.nextBody = body;
    }

    public Body getNext(){
        return nextBody;
    }

    public Body getPre(){
        return preBody;
    }

    public List<Spirit> getMeaning(){
        return meaning;
    }

    public void addMeaning(Spirit spirit){
        if(meaning.indexOf(spirit) < 0){
            meaning.add(spirit);
        }
    }

    /**
     * 通过上一个Body，修改自身关系
     * */
    public void approveByPre(Body pre){
        boolean isRelate = false;
        List<Soul> preSouls = pre.getSouls();
        for(int i = 0; i < preSouls.size(); i++){
            for(int j = 0; j < souls.size(); j++){
                if(souls.get(j).getElement().isSpecial()){
                    continue;
                }
                if(preSouls.get(i).match(souls.get(j)) < 2){
                    if(!isRelate){
                        isRelate = true;
                    }
                    relation[j]++;
                }
            }
        }
        if(isRelate){
            preBody = pre;
            pre.getSubject().setNext(master);
        }
    }

    /**
     * 得到此句话中的重点
     * */
    public List<Soul> getPoint(){
        List<Soul> points = new ArrayList<>();
        for(int i = 0; i < souls.size(); i++){
            if(relation[i] > 0){
                points.add(souls.get(i));
            }
        }
        return points;
    }

    /**
     * 查看是否包含某项含义
     * */
    public List<Soul> isContain(Soul target){
        List<Soul> wanted = new ArrayList<>();
        for(int i = 0; i < meaning.size(); i++){
            System.out.println("isContain" + i);
            for(Soul soul : meaning.get(i).getPoint()){
                System.out.print(soul.getElement() + " , ");

                if(soul.match_3(target) && wanted.indexOf(soul) < 0){
                    wanted.add(soul);
                }
            }
            System.out.println();
        }
        return wanted;
    }


    /**
     * 将自己添加入组成自己的soul中
     * */
    public void addSelf(){
        for(Soul s : souls){
            s.addSpirit(this);
        }
    }

    /**
     * 匹配策略：
     * 通过主题之间的关系判断
     * 此处为语义上的判断
     *
     * 返回值：
     * 0：相同
     * 1：有点相同
     * 2：有点不同
     * 3：不同
     * 4：无关
     * */
    public int match(Spirit spirit){
        List<Soul> targetSoul = spirit.getSouls();
        int[] targetRel = spirit.getRelation();
        for(int i = 0; i < targetSoul.size(); i++){
            for(int j = 0; j < souls.size(); j++) {
                if (targetSoul.get(i).match(souls.get(j)) < 2){
                    return 0;
                }
            }
        }
        return 4;
    }

    /**
     * 是否相等
     * soul的组合是否一致
     * 并且关系是否也相同
     * */
    public boolean equal(Spirit spirit){
        List<Soul> temp;
        synchronized (this){
            temp = new ArrayList<>(souls);
        }
        if(spirit.getSouls().size() != temp.size()){
            return false;
        }
        for(int i = 0; i < temp.size(); i++){
            if(!spirit.getSouls().get(i).equals(temp.get(i))){
                return false;
            }
        }

        for(int i = 0; i < temp.size(); i++){
            if((relation[i] == 0 && spirit.getRelation()[i] != 0) || (relation[i] != 0 && spirit.getRelation()[i] == 0)){
                return false;
            }
        }
        return true;
    }

    /**
     * 当从原body中得到较长soul后，用于合并原本分开、现在合并的soul的部分
     * */
    public void merge(List<Soul> alter, int begin, int size){

        this.souls = alter;
        int max = relation[begin];
        for(int i = 1; i < size; i++){
            max = relation[begin + i] > max ? relation[begin + i] : max;
        }
        relation[begin] = max;
        int[] temp = new int[relation.length - size + 1];
        for(int i = 0, j = 0; i < relation.length; i++){
            if(i <= begin || i >= begin + size){
                temp[j] = relation[i];
                j++;
            }
        }
        relation = temp;
    }


    /**
     * 匹配1：
     * 这两个spirit是否有联系，查看meaning之中是否有一样的spirit
     * */
    public boolean match_1(Spirit target) {
        List<Spirit> ts = target.getMeaning();
        for(Spirit t : ts){
            for(Spirit s : meaning){
                if(s.equal(t)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 匹配2：
     * 这两个spirit是否有相似，查看meaning之中是否一致
     * */
    public boolean match_2(Spirit target) {
        List<Spirit> ts = target.getMeaning();
        List<Spirit> longer, shorter;
        if(ts.size() > meaning.size()){
            longer = ts;
            shorter = meaning;
        }else{
            longer = meaning;
            shorter = ts;
        }
        int count = 0;
        for(Spirit t : shorter){
            for(Spirit s : longer){
                if(s.equal(t)){
                    count++;
                    break;
                }
            }
        }
        return count == shorter.size() ? true : false;
    }

    /**
     * list中包含自身
     * */
    private void makeRelation(){
        List<Spirit> list;
        if(meaning == null){
            list = new ArrayList<>();
        }else{
            list = meaning;
        }

        list.add(this);

        for(int i = 0; i < souls.size(); i++){
            for(int j = i + 1; j < souls.size(); j++){
                Body[] bodies = souls.get(i).match_2(souls.get(j));
                if(bodies == null){
                    continue;
                }

                relation[i]++;
                relation[j]++;
                List<Spirit> s0s = bodies[0].getSubject().getMeaning();
                List<Spirit> s1s = bodies[1].getSubject().getMeaning();

                for(Spirit s0 : s0s){
                    for(Spirit s1 : s1s){
                        if(s0.equal(s1) && list.indexOf(s0) < 0){
                            list.add(s0);
                            break;
                        }
                    }
                }
                bodies[0].getSubject().addMeaning(this);
                bodies[1].getSubject().addMeaning(this);
            }
        }
        meaning = list;

    }

}
