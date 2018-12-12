package com.cl.simples;

/**
 * @author cl
 * @create 2018-12-12 11:36
 **/
public class ABeanFactory {


    public static ABean getABean(){
        return new ABean();
    }

    public  ABean getABean2(){
        return new ABean();
    }
}
