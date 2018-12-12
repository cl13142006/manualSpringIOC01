package com.cl;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cl
 * @create 2018-12-12 9:39
 **/
public class DefaultBeanFactory implements BeanFactory,BeanDefinitionRegistry,Closeable {

    //存储bean定义的
    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String,Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beandefinition) {
        Objects.requireNonNull(beanName,"beanName is not null");
        Objects.requireNonNull(beandefinition,"beanDefinition is not null");
        synchronized(beanDefinitionMap){
            if(!beandefinition.validate()){
                throw new RuntimeException("定义不合法");
            }
            if(containBeanDefinition(beanName)){
                throw new RuntimeException("beanName repetition");
            }
            beanDefinitionMap.put(beanName,beandefinition);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String beanName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return this.doGetBean(beanName);
    }

    protected Object doGetBean(String beanName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Objects.requireNonNull(beanName,"beanName is not null");
        Object object = beanMap.get(beanName);
        if(object != null){
            return object;
        }
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if(bd == null){
            throw new RuntimeException("bd is not null");
        }
        Class<?> clazz = bd.getBeanClass();
        if(clazz != null){
            if(StringUtils.isBlank(bd.getFactoryMethodName())){
                object = this.createInstanceByConstructor(bd);
            }else{
                //静态工厂
                object = this.createInstanceByStaticFactoryMethod(bd);
            }
        }else{
            //普通工厂方法
            object = this.createInstanceByFactoryBean(bd);
        }
        this.doInit(object,bd);
        if(bd.isSingleton()){
            beanMap.put(beanName,object);
        }
        return object;
    }

    private Object createInstanceByConstructor(BeanDefinition bd) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = bd.getBeanClass();
        return clazz.newInstance();
    }

    private Object createInstanceByStaticFactoryMethod(BeanDefinition bd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = bd.getBeanClass();
        Method method = clazz.getMethod(bd.getFactoryMethodName(),null);
        return method.invoke(clazz,null);
    }

    private Object createInstanceByFactoryBean(BeanDefinition bd) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String factoryBeanName = bd.getFactoryBeanName();
        Object obj = this.doGetBean(factoryBeanName);
        String factoryMethodName = bd.getFactoryMethodName();
        Method method  = obj.getClass().getMethod(factoryMethodName,null);
        return method.invoke(obj,null);
    }

    private void doInit(Object obj,BeanDefinition bd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String initMethodName = bd.getInitMethodName();
        if(StringUtils.isNotBlank(initMethodName)){
            Method method = obj.getClass().getMethod(initMethodName,null);
            method.invoke(obj,null);
        }
    }

    @Override
    public void close() throws IOException {
        //关闭处理
        Set<Map.Entry<String, BeanDefinition>> entries = beanDefinitionMap.entrySet();
        for(Map.Entry<String, BeanDefinition> entry : entries){
            BeanDefinition bd = entry.getValue();
            if(bd.isSingleton() && StringUtils.isNotBlank(bd.getDestoryMethodName())){
                try {
                    Object obj = beanMap.get(entry.getKey());
                    Method method = obj.getClass().getMethod(bd.getDestoryMethodName(),null);
                    method.invoke(obj,null);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
