package v5.brain.comprehension;

import v5.brain.memory.RecentMemory;
import v5.brain.output.OutputArea;
import v5.unit.*;
import v5.unit.type.Element;
import v5.unit.type.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Think extends Thread{

    private RecentMemory recentMemory;
    private BlockingDeque<Element[]> blockingDeque;

    private boolean Is_Learning = false;
    private OutputArea outputArea;
    private Soul target;



    private Body predictResponse_learning;
    private Body lastBody_learning;

    /**
     * 阻塞队列长度
     * */
    private static int MAX_LENGTH = 32;

    public Think(){
        this.recentMemory = new RecentMemory();
        this.blockingDeque = new LinkedBlockingDeque<>(MAX_LENGTH);
        this.outputArea = new OutputArea();
    }

    public void run(){
        while(true){
            Element[] elements = blockingDeque.poll();
            if(elements != null){
                dataProcess(elements);
            }else{
                try {
                    synchronized (this){
                        wait();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void addInput(Element[] elements) throws InterruptedException{
        blockingDeque.put(elements);
        synchronized (this){
            notify();
        }
    }

    private Body response_talking;
    private Body predictResponse_talking;

    private void dataProcess(Element[] elements){
        Body body = recentMemory.search(elements);
        recentMemory.approve(body);
        if(predictResponse_talking != null){

        }
        response_talking = body.response();
        outputArea.outputResponse(response_talking);
        predictResponse_talking = response_talking.response();
    }

    /**
     * 学习过程
     *         Body body = recentMemory.search(elements);
     *         recentMemory.approve(body);
     *         if(predictResponse_learning != null){
     *             lastBody_learning.enhance(predictResponse_learning, body);
     *         }
     *         lastBody_learning = body;
     *         predictResponse_learning = body.response();
     *         outputArea.outputBody(body);
     *         outputArea.outputResponse(predictResponse_learning);
     * */

    /**
     * 交互，输入得到输出，对话
     *         Body body = recentMemory.search(elements);
     *         recentMemory.approve(body);
     *         if(predictResponse != null){
     *
     *         }
     *         response = body.response();
     *         outputArea.outputBody(body);
     *         outputArea.outputResponse(response);
     *         predictResponse = response.response();
     * */

    /**
     * 直接识别垃圾邮件的内容
     *         if(elements[0].equals(new Word("s"))){
     *             Is_Learning = true;
     *         }
     *         Body body = recentMemory.search(elements);
     *         for(Soul soul : body.getSouls()){
     *             if(soul.getElement().toString().equals("垃圾邮件")){
     *                 target = soul;
     *             }
     *         }
     *         recentMemory.approve(body);
     *         outputArea.outputBody(body);
     *         if(target != null && Is_Learning){
     *             System.out.println("match");
     *             outputArea.outputPathTo(body.findPathTo(target));
     *         }else{
     *             System.out.println("NOT match");
     *             body.addSelf();
     *         }
     *
     *
     * */

}
