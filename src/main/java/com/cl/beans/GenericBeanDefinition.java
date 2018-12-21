package com.cl.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author cl
 * @create 2018-12-12 9:29
 **/
public class GenericBeanDefinition implements BeanDefinition {

    private Class<?> clazz;

    private String scope = SCOPE_SINGLETON;

    private String factoryBeanName;

    private String factoryMethodName;

    private String initMethodName;

    private String destoryMethodName;

    private List<?> constructorArgumentValues;

    private Constructor<?> constructor;

    private Method factoryMethod;

    private List<PropertyValue> propertyValues;

    public void setBeanClass(Class<?> clazz){
        this.clazz = clazz;
    }

    public void setScope(String scope){
        this.scope = scope;
    }

    public void setFactoryBeanName(String factoryBeanName){
        this.factoryBeanName = factoryBeanName;
    }

    public void setFactoryMethodName(String factoryMethodName){
        this.factoryMethodName = factoryMethodName;
    }

    public void setInitMethodName(String initMethodName){
        this.initMethodName = initMethodName;
    }

    public void setDestoryMethodName(String destoryMethodName){
        this.destoryMethodName = destoryMethodName;
    }

    public void setConstructorArgumentValues(List<?> constructorArgumentValues){
        this.constructorArgumentValues = constructorArgumentValues;
    }

    public void setPropertyValues(List<PropertyValue> propertyValues){
        this.propertyValues = propertyValues;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.clazz;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    @Override
    public String getInitMethodName() {
        return this.initMethodName;
    }

    @Override
    public String getDestoryMethodName() {
        return this.destoryMethodName;
    }

    @Override
    public List<?> getConstructorAgrumentValues() {
        return this.constructorArgumentValues;
    }

    @Override
    public Constructor<?> getConstructor() {
        return this.constructor;
    }

    @Override
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Method getFactoryMethod() {
        return this.factoryMethod;
    }

    @Override
    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    @Override
    public List<PropertyValue> getPropertyValues() {
        return this.propertyValues;
    }


}
