package com.cl;

/**
 * @author cl
 * @create 2018-12-11 13:34
 **/
public interface BeanDefinitionRegistry {


    void registryBeanDefinition(String beanName,BeanDefinition beandefinition);

    BeanDefinition getBeanDefinition(String beanName);

    boolean containBeanDefinition(String beanName);

}
