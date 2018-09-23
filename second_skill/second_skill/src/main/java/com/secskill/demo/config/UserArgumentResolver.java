package com.secskill.demo.config;

import com.secskill.demo.access.UserContext;
import com.secskill.demo.domain.SecondSkillUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class UserArgumentResolver  implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == SecondSkillUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }
}
