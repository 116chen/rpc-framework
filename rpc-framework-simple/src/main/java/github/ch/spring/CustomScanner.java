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

    public CustomScanner(BeanDefinitionRegistry beanDefinitionRegistry, Class<? extends Annotation> annotationType) {
        super(beanDefinitionRegistry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
    }
}
