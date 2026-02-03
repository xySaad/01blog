package com.z01.blog.infrastructure;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.AccessMethod;
import com.z01.blog.annotation.EntityAccess;

record AccessInfo(Object repoBean, Method method) {
}

@Component
public class EntityRegistry implements InitializingBean {

    @Autowired
    private ApplicationContext context;
    private final Map<Parameter, AccessInfo> cache = new HashMap<>();

    public AccessInfo getAccessInfo(Parameter param) {
        return cache.get(param);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] beanNames = context.getBeanNamesForAnnotation(RestController.class);
        for (String beanName : beanNames) {
            Class<?> beanType = context.getType(beanName);
            for (Method method : beanType.getDeclaredMethods()) {
                if (!AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class))
                    continue;

                Parameter[] parameters = method.getParameters();

                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    var entityAccess = param.getAnnotation(EntityAccess.class);
                    if (entityAccess != null) {
                        Class<?> repoInterface = validateEntityParamter(param, entityAccess);
                        Object repoBean = context.getBean(repoInterface);
                        Method repoAccessMethod = findAccessMethod(repoInterface, entityAccess.mode());
                        validateMethodSignature(repoAccessMethod, repoInterface, param);
                        cache.put(param, new AccessInfo(repoBean, repoAccessMethod));
                    }
                }
            }
        }
    }

    private Class<?> validateEntityParamter(Parameter param, EntityAccess entityAccess) {
        var ParamType = param.getType();

        Assert.isAssignable(
                RestrictedEntity.class, ParamType,
                "The provided type does not implement RestrictedEntity");

        var repoClass = entityAccess.repo();
        if (repoClass != Repository.class)
            return repoClass;

        Repositories repositories = new Repositories(context);
        var repoInfoOpt = repositories.getRepositoryInformationFor(ParamType);
        if (repoInfoOpt.isEmpty()) {
            throw new IllegalStateException("No repository for " + ParamType.getName());
        }

        var repoInterface = repoInfoOpt.get().getRepositoryInterface();
        return repoInterface;
    }

    private Method findAccessMethod(Class<?> repoInterface, EntityAccess.Mode mode) {
        for (Method method : repoInterface.getDeclaredMethods()) {
            AccessMethod accessMethod = method.getAnnotation(AccessMethod.class);
            if (accessMethod != null) {
                return method;
            }
        }

        for (Class<?> iface : repoInterface.getInterfaces()) {
            for (Method method : iface.getDeclaredMethods()) {
                AccessMethod accessMethod = method.getAnnotation(AccessMethod.class);
                if (accessMethod != null) {
                    return method;
                }
            }
        }

        throw new IllegalStateException(
                "No method annotated with @AccessMethod(" + mode + ") found in repository: " + repoInterface.getName());

    }

    private void validateMethodSignature(Method method, Class<?> repoInterface, Parameter controllerParam) {
        Class<?> expectedEntityType = controllerParam.getType();
        Class<?> returnType = method.getReturnType();

        Assert.isAssignable(expectedEntityType, returnType,
                String.format("Method %s in %s returns %s, but controller expects %s",
                        method.getName(), repoInterface.getSimpleName(), returnType.getSimpleName(),
                        expectedEntityType.getSimpleName()));

        Assert.isTrue(method.getParameterCount() == 1, "AccessMethod must take exactly 1 argument (the lookup key).");
    }
}