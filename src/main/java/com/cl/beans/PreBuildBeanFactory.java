package com.cl.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 * @create 2018-12-12 13:40
 **/
public class PreBuildBeanFactory extends DefaultBeanFactory{

    List<String> beanNameList = new ArrayList<>();

    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beandefinition) {
        super.registryBeanDefinition(beanName,beandefinition);
        synchronized (beanNameList){
            beanNameList.add(beanName);
        }
    }

    public void preInstantiateSingletons() throws Exception {
        synchronized (beanNameList){
            for (String name : beanNameList) {
                BeanDefinition bd = this.getBeanDefinition(name);
                if (bd.isSingleton()) {
                    this.doGetBean(name);
                }
            }
        }
    }





}
