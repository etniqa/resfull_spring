package com.example.mobileappws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

// necesseraly need to announce SpringApplicationContext as Bean in MobileAppWsApplication (main spring class) (which has @SpringBootApplication)
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext context;

    public ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String beanName) {
        System.out.println("getBean " + context);

        return context.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("setApplicationContext " + applicationContext);
        context = applicationContext;
    }
}
