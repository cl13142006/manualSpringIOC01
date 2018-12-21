package com.cl.beans;

/**
 * bean的引用
 * @author cl
 * @create 2018-12-19 15:26
 **/
public class BeanReference {

    private String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName(){
        return this.beanName;
    }
}
