package github.ch.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午2:42
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    public CustomScanner(BeanDefinitionRegistry beanDefinitionRegistry, Class<? extends Annotation>... annotationTypes) {
        super(beanDefinitionRegistry);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        }
    }

    public CustomScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
}
