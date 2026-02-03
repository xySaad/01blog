package com.z01.blog.infrastructure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import com.z01.blog.annotation.EntityAccess;

@Component
public class EntityAccessResolver<T> implements HandlerMethodArgumentResolver {
    @Autowired
    EntityRegistry entityRegistry;
    @Autowired
    @Lazy
    ConversionService conversionService;
    @Autowired
    PrincipalProvider<T> principalProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(EntityAccess.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        @SuppressWarnings("unchecked")
        var pathVariables = (Map<String, String>) webRequest
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        String entityId = pathVariables.get(parameter.getParameterName());
        if (entityId == null)
            throw new MissingPathVariableException(parameter.getParameterName(), parameter);

        var accessInfo = entityRegistry.getAccessInfo(parameter.getParameter());

        Class<?> requiredIdType = accessInfo.method().getParameterTypes()[0];

        Object convertedId = entityId;
        if (conversionService.canConvert(String.class, requiredIdType)) {
            convertedId = conversionService.convert(entityId, requiredIdType);
        }

        Object entity = accessInfo.method().invoke(accessInfo.repoBean(), convertedId);
        if (entity == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        @SuppressWarnings("unchecked")
        RestrictedEntity<T> restricted = (RestrictedEntity<T>) entity;
        restricted.ensureAccess(
                principalProvider.getCurrentPrincipal(),
                parameter.getParameterAnnotation(EntityAccess.class).mode());

        return entity;
    }
}