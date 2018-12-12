package com.cl;

import com.cl.simples.ABean;
import com.cl.simples.ABeanFactory;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author cl
 * @create 2018-12-12 11:22
 **/
public class IOCTest {

     DefaultBeanFactory dbf = new DefaultBeanFactory();

    @Test
    public void testConstractorType() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        GenericBeanDefinition bd = new GenericBeanDefinition() ;
        bd.setBeanClass(ABean.class);
        bd.setInitMethodName("init");
        bd.setDestoryMethodName("destory");
        dbf.registryBeanDefinition("abean",bd);
        ABean abean = (ABean)dbf.getBean("abean");
        abean.doSomm();
        dbf.close();
    }


    @Test
    public void testStaticFactory() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        GenericBeanDefinition bd = new GenericBeanDefinition() ;
        bd.setBeanClass(ABeanFactory.class);
        bd.setFactoryMethodName("getABean");
        dbf.registryBeanDefinition("staticBean",bd);
        ABean abean = (ABean)dbf.getBean("staticBean");
        abean.doSomm();
    }

    @Test
    public void testFactoryMethodName() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        GenericBeanDefinition bd = new GenericBeanDefinition() ;
        bd.setBeanClass(ABeanFactory.class);
        dbf.registryBeanDefinition("abeanFactory",bd);
        bd = new GenericBeanDefinition() ;
        bd.setFactoryBeanName("abeanFactory");
        bd.setFactoryMethodName("getABean2");
        bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        dbf.registryBeanDefinition("aaa",bd);
        for(int i = 0;i<3;i++){

            ABean abean = (ABean)dbf.getBean("aaa");
            abean.doSomm();
        }
    }




}
