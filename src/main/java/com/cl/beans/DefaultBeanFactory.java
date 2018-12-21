package com.cl.beans;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cl
 * @create 2018-12-12 9:39
 **/
public class DefaultBeanFactory implements BeanFactory,BeanDefinitionRegistry,Closeable {

    //存储bean定义的
    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String,Object> beanMap = new ConcurrentHashMap<>();

    private ThreadLocal<Set<String>> buildingBeans = new ThreadLocal<>();

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
    public Object getBean(String beanName) throws Exception {
        return this.doGetBean(beanName);
    }

    protected Object doGetBean(String beanName) throws Exception {
        Objects.requireNonNull(beanName,"beanName is not null");
        Object object = beanMap.get(beanName);
        if(object != null){
            return object;
        }

        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if(bd == null){
            throw new RuntimeException("bd is not null");
        }

        Set<String> ingBeans = this.buildingBeans.get();
        if(ingBeans == null){
            ingBeans= new HashSet<>();
            this.buildingBeans.set(ingBeans);
        }

        if(!ingBeans.add(beanName)){
            throw new RuntimeException(beanName + "循环依赖" + ingBeans);
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

        ingBeans.clear();

        //属性注入
        this.setPropertyDiValue(bd,object);

        this.doInit(object,bd);

        if(bd.isSingleton()){
            beanMap.put(beanName,object);
        }

        return object;
    }

    private void setPropertyDiValue(BeanDefinition bd,Object object) throws Exception {
        List<PropertyValue> propertyValues = bd.getPropertyValues();
        if(propertyValues != null){
            for(PropertyValue propertyValue : propertyValues){
                String name = propertyValue.getName();
                Object rv = propertyValue.getValue();
                Objects.requireNonNull(name,"name is not null");
                Objects.requireNonNull(rv,"obj is not null");
                Field field = object.getClass().getDeclaredField(name);
                field.setAccessible(true);
                Object v = null;
                if (rv == null) {
                    v = null;
                } else if (rv instanceof BeanReference) {
                    v = this.doGetBean(((BeanReference) rv).getBeanName());
                } else if (rv instanceof Object[]) {
                    // TODO 处理集合中的bean引用
                } else if (rv instanceof Collection) {
                    // TODO 处理集合中的bean引用
                } else if (rv instanceof Properties) {
                    // TODO 处理properties中的bean引用
                } else if (rv instanceof Map) {
                    // TODO 处理Map中的bean引用
                } else {
                    v = rv;
                }
                field.set(object,v);
            }
        }
    }

    private Object createInstanceByConstructor(BeanDefinition bd) throws Exception {
        try{
            Object [] obj = this.getConstructorArgumentValues(bd);
            Class<?> clazz = bd.getBeanClass();
            if(obj == null){
                return clazz.newInstance();
            }else{
                return this.determineConstractor(bd,obj).newInstance(obj);
            }
        } catch (SecurityException e1) {
            e1.printStackTrace();
            throw e1;
        }
    }

    private Object createInstanceByStaticFactoryMethod(BeanDefinition bd) throws Exception {
        Class<?> clazz = bd.getBeanClass();
        Object [] args = this.getRealValues(bd.getConstructorAgrumentValues());
//        Method method = clazz.getMethod(bd.getFactoryMethodName(),null);
        Method method = this.determineFactoryMethod(bd,args,null);
        return method.invoke(clazz,args);

    }

    private Object createInstanceByFactoryBean(BeanDefinition bd) throws Exception {
        String factoryBeanName = bd.getFactoryBeanName();
        Object obj = this.doGetBean(factoryBeanName);
        Object[] realArgs = this.getRealValues(bd.getConstructorAgrumentValues());
        Method method = this.determineFactoryMethod(bd,realArgs,obj.getClass());
        return method.invoke(obj,realArgs);
    }

    private Method determineFactoryMethod(BeanDefinition bd,Object [] realValues,Class<?> type) throws Exception{
        if(type == null){
            type = bd.getBeanClass();
        }
        String methodName = bd.getFactoryMethodName();

        Method method = bd.getFactoryMethod();
        if(method != null){
            return method;
        }
        Class [] paramTypes = new Class[realValues.length];
        int j = 0;
        for(int i = 0;i<realValues.length;i++){
            paramTypes[j++] = realValues[i].getClass();
        }
        try{
            method = type.getMethod(methodName,paramTypes);
        }catch(Exception e){

        }
        if(method == null){
            outer : for(Method m0 : type.getMethods()){
               if(!m0.getName().equals(methodName)){
                   continue;
               }
                Class<?>[] paramterTypes = m0.getParameterTypes();
                if(paramterTypes.length == paramTypes.length ){
                    for (int i = 0; i < paramterTypes.length; i++) {
                        if (!paramterTypes[i].isAssignableFrom(realValues[i].getClass())) {
                            continue outer;
                        }
                    }

                    method = m0;
                    break outer;
                }
            }
        }
        if (method != null) {
            // 对于原型bean,可以缓存找到的方法，方便下次构造实例对象。在BeanDefinfition中获取设置所用方法的方法。
            // 同时在上面增加从beanDefinition中获取的逻辑。
            if (bd.isPrototype()) {
                bd.setFactoryMethod(method);
            }
            return method;
        } else {
            throw new Exception("不存在对应的构造方法！" + bd);
        }

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

    /**
     * 匹配构造器
     * @param bd
     * @param args
     * @return
     */
    private Constructor<?> determineConstractor(BeanDefinition bd, Object [] args) throws Exception {
        Constructor<?> constructor = null;
        constructor = bd.getConstructor();
        if(constructor != null){
            return constructor;
        }
        //先直接按照目标和参数
        Class<?> [] paramType = new Class[args.length];
        int j = 0;
        for(Object o : args){
            paramType[j++] = o.getClass();
        }
        try {
            constructor = bd.getBeanClass().getConstructor(paramType);
        }catch(Exception e){
            //异常不处理
        }
        if(constructor == null){
            outer: for(Constructor<?> ct0 : bd.getBeanClass().getConstructors()){
                Class<?> [] paramterTypes = ct0.getParameterTypes();
                if(paramterTypes.length == args.length){
                    for(int i = 0;i<paramterTypes.length;i++){
                        if(!paramterTypes[i].isAssignableFrom(args[i].getClass())){
                            continue outer;
                        }
                    }
                    constructor = ct0;
                    break outer;
                }

            }
        }
        if(constructor != null){
            if(bd.isPrototype()){
                bd.setConstructor(constructor);
            }
            return constructor;
        }else{
            throw new Exception("不存在对应的构造方法！" + bd);
        }

    }


    private Object [] getConstructorArgumentValues(BeanDefinition bd) throws Exception{
        return getRealValues(bd.getConstructorAgrumentValues());
    }

    private Object [] getRealValues(List<?> defs) throws Exception {
        if(CollectionUtils.isEmpty(defs)){
            return null;
        }
        Object [] values = new Object[defs.size()];
        int i = 0;
        Object v = null;
        for(Object obj : defs){
            if(obj == null){
                v = null;
            } else if(obj instanceof  BeanReference){
                v = this.doGetBean(((BeanReference)obj).getBeanName());
            }else if(obj instanceof Object []){

            }else if(obj instanceof Collection){

            }else if(obj instanceof Map){

            }else {
                v = obj;
            }
            values[i++] = v;
        }
        return values;
    }
}
