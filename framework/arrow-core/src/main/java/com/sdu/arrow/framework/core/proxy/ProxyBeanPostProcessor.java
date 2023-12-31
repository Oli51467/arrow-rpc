package com.sdu.arrow.framework.core.proxy;

import com.sdu.arrow.framework.common.annotation.ArrowReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@Order(1)
public class ProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 给带有特殊注解的bean生成一个Rpc代理对象
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            ArrowReference annotation = field.getAnnotation(ArrowReference.class);
            if (null != annotation) {
                // 获取一个代理
                String application = annotation.application();
                Class<?> clazz = field.getType();
                // 生成代理
                Object proxy = ProxyFactory.getProxy(clazz, application);
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
