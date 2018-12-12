package com.cl;

/**
 * @author cl
 * @create 2018-12-12 9:29
 **/
public class GenericBeanDefinition implements BeanDefinition {

    private Class<?> clazz;

    private String scope = BeanDefinition.SCOPE_SINGLETON;

    private String factoryBeanName;

    private String factoryMethodName;

    private String initMethodName;

    private String destoryMethodName;


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
        return BeanDefinition.SCOPE_SINGLETON.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return BeanDefinition.SCOPE_PROTOTYPE.equals(this.scope);
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
}
