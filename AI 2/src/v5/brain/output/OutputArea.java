package v5.brain.output;

import v5.unit.Body;
import v5.unit.Soul;

import java.util.ArrayList;
import java.util.List;

public class OutputArea {

    public OutputArea(){}

    public void outputBody(Body body){
        System.out.println("body:   " + getString(body.getSouls()));
    }

    public void outputPath(List<Body> result){
        System.out.println("Path:");
        for(Body body : result){
            for(Soul soul : body.getSouls()){
                System.out.print(soul.getElement() + " , ");
            }
            System.out.println();
        }
    }

    public void outputResponse(Body response){
        System.out.println("Response:");
        for(Soul soul : response.getSouls()){
            System.out.print(soul.getElement() + " , ");
        }
        System.out.println();
    }

    public void outputPathTo(List<Soul> result){
        System.out.println("Wanted:");
        for(Soul soul : result){
            System.out.print(soul.getElement() + " , ");
        }
        System.out.println();
    }

    private String getString(List<Soul> list){
        String r = "";
        for(Soul s : list){
            if(s == null){
                r = r + "   ";
            }
            r = r + s.getElement().toString();
            r += " , ";
        }
        return r;
    }

}
