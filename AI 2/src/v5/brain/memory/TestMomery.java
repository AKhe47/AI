package v5.brain.memory;

import v5.unit.Body;
import v5.unit.Soul;
import v5.unit.type.Word;

import java.util.ArrayList;
import java.util.List;

public class TestMomery {

    private List<Body> bodies;
    private List<Soul> units;

    private String[][] sentences =
                    {{"苹果","是","红色的"},
                    {"成熟的", "苹果","是","红色的"},
                    {"不成熟的","苹果","是","青色的"},
                    {"苹果","是","圆形的"},};

    public TestMomery(){
        this.bodies = new ArrayList<>(100);
        this.units = new ArrayList<>(100);
        testFunc();
    }


    private void testFunc(){
        addUnits();
        addBodies();
    }

    private void addUnits(){
        units.add(new Soul(new Word("苹果")));
        units.add(new Soul(new Word("是")));
        units.add(new Soul(new Word("红色的")));
        units.add(new Soul(new Word("成熟的")));
        units.add(new Soul(new Word("苹果")));
    }

    private void addBodies(){
    //
        //
        //
        //  bodies.add(new Body());
    }


}
