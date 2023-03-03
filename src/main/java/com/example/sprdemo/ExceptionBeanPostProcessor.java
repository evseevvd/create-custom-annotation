package com.example.sprdemo;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.reflections.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExceptionBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Class<?>> originBeans = new HashMap<>();

    private final ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (ReflectionUtils.getMethods(bean.getClass())
            .stream().anyMatch(method -> method.isAnnotationPresent(MyExceptionHandler.class))) {
            originBeans.put(beanName, bean.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class originBean = originBeans.get(beanName);

        if (Objects.isNull(originBean)) {
            return bean;
        } else {

            final Set<Method> annotatedMethods = ReflectionUtils.getMethods(originBean).stream()
                .filter(method -> method.isAnnotationPresent(MyExceptionHandler.class))
                .collect(Collectors.toSet());

            return Proxy.newProxyInstance(originBean.getClassLoader(), originBean.getInterfaces(), (proxy, method, args) -> {
                Object result = null;

                if (annotatedMethods.stream().map(Method::getName).anyMatch(name -> method.getName().equals(name))) {
                    final ExceptionHandler handler = annotatedMethods
                        .stream()
                        .filter(m -> m.getName().equals(method.getName()))
                        .filter(method1 -> method1.isAnnotationPresent(MyExceptionHandler.class))
                        .findFirst()
                        .map(parameter -> {
                            final MyExceptionHandler annotation = parameter.getAnnotation(MyExceptionHandler.class);
                            final String exceptionHandlerBeanName = annotation.handler();
                            return applicationContext.getBean(ExceptionHandler.class, exceptionHandlerBeanName);
                        })
                        .get();
                    try {
                        result = method.invoke(args);
                    } catch (Exception e) {
                        handler.handle(e);
                    }
                } else {
                    result = method.invoke(args);
                }

                return result;
            });
        }
    }
}
