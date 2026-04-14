package com.z01.blog.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.exception.AppError;

@Component
public class EntityAccessResolver<T> implements HandlerMethodArgumentResolver {
    @Autowired
    EntityRegistry entityRegistry;
    @Autowired
    PrincipalProvider<T> principalProvider;
    @Autowired
    @Lazy
    private ConversionService conversionService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(EntityAccess.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws IllegalAccessException, InvocationTargetException {
        var repoMethod = entityRegistry.getMethodForParam(parameter.getParameter());

        Map<String, String> pathVariables = (Map<String, String>) webRequest
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        // TODO: support multiple variables
        String raw = pathVariables.get(parameter.getParameterName());
        Class<?> idType = repoMethod.method.getParameterTypes()[0];
        var convertedId = conversionService.convert(raw, idType);
        var entity = this.getEntity(repoMethod, convertedId);

        if (entity instanceof RestrictedEntity re) {
            var access = parameter.getParameterAnnotation(EntityAccess.class);
            re.ensureAccess(principalProvider.getCurrentPrincipal(), access.mode());
        }

        return entity;
    }

    private Object getEntity(RepoMethod repoMethod, Object id)
            throws IllegalAccessException, InvocationTargetException {
        var entity = repoMethod.method.invoke(repoMethod.repo, id);

        if (entity == null)
            throw AppError.ENTITY_NOT_FOUND.asException();

        if (entity instanceof Optional<?> oe)
            return oe.orElseThrow(AppError.ENTITY_NOT_FOUND::asException);

        return entity;
    }
}