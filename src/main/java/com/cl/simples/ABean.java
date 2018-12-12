package com.cl.simples;

/**
 * @author cl
 * @create 2018-12-12 11:25
 **/
public class ABean {


    public void doSomm(){
        System.out.println("method executor" + this);
    }

    public void init(){
        System.out.println("初始化");
    }

    public void destory(){
        System.out.println("销毁");
    }


}
