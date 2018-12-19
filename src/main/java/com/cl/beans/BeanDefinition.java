package com.cl.beans;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by cl on 2018/12/11.
 */
public interface BeanDefinition {

    final String SCOPE_SINGLETON = "singleton";

    final String SCOPE_PROTOTYPE = "prototype";

    Class<?> getBeanClass();

    String getScope();

    boolean isSingleton();

    boolean isPrototype();

    String getFactoryBeanName();

    String getFactoryMethodName();

    String getInitMethodName();

    String getDestoryMethodName();

    default boolean validate(){
        //进行校验
        if(getBeanClass() != null && StringUtils.isNotBlank(getFactoryBeanName())){
            return false;
        }else{
            if(getBeanClass() == null){

                if(StringUtils.isBlank(getFactoryMethodName()) || StringUtils.isBlank(getFactoryBeanName())){
                    return false;
                }
            }
        }
        return true;
    }

}
