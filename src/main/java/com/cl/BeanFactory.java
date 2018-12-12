package com.cl;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by cl on 2018/12/11.
 */
public interface BeanFactory {

    /**
     * 获取bean信息
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
