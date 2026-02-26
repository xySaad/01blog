package com.z01.blog.infrastructure;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.z01.blog.annotation.AccessMethod;
import com.z01.blog.annotation.EntityAccess;

class RepoMethod {
    public Object repo;
    public Method method;

    RepoMethod(Object repo) {
        this.repo = repo;
        this.method = getAccessMethod(repo);
    }

    private Method getAccessMethod(Object repo) {
        for (Method m : repo.getClass().getMethods()) {
            AccessMethod accessMethod = AnnotationUtils.findAnnotation(m, AccessMethod.class);
            if (accessMethod != null)
                return m;
        }

        throw new RuntimeException("No @AccessMethod found for: " + repo.getClass());
    }
}

@Component
public class EntityRegistry implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private ApplicationContext context;

    private HashMap<Class<?>, RepoMethod> registry = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        for (String beanName : context.getBeanDefinitionNames()) {
            Object bean = context.getBean(beanName);
            for (Method method : bean.getClass().getDeclaredMethods()) {
                for (Parameter param : method.getParameters()) {
                    this.registerEntityAccessParam(param);
                }
            }
        }
    }

    public RepoMethod getMethodForParam(Parameter param) {
        Class<?> paramType = param.getType();
        return registry.get(paramType);
    }

    private void registerEntityAccessParam(Parameter param) {
        EntityAccess entityAccess = param.getAnnotation(EntityAccess.class);
        if (entityAccess == null)
            return;

        Class<?> paramType = param.getType();
        if (registry.containsKey(paramType))
            return;

        if (entityAccess.repo() == Repository.class) {
            var repoMethod = new RepoMethod(findRepo(paramType));
            registry.put(paramType, repoMethod);
            return;
        }

        Class<?> repoClass = entityAccess.repo();
        Object repoBean = context.getBean(repoClass);
        var repoMethod = new RepoMethod(repoBean);
        registry.put(paramType, repoMethod);
    }

    private Object findRepo(Class<?> paramType) {
        Repositories repositories = new Repositories(context);
        Optional<Object> repoOpt = repositories.getRepositoryFor(paramType);
        if (repoOpt.isEmpty())
            throw new RuntimeException("No repository found for entity: " + paramType);

        Object repo = repoOpt.get();
        return repo;
    }

}