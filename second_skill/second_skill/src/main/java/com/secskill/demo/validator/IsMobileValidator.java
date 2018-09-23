package com.secskill.demo.validator;

import com.secskill.demo.util.ValidatorUitl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
       if (required) {
           return ValidatorUitl.isMobile(value);
       }
       return value == null ? true : ValidatorUitl.isMobile(value);
    }
}
