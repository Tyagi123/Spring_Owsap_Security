package com.security.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<NameValidation, String>
{
    public boolean isValid(String name, ConstraintValidatorContext cxt) {
        if(name != null) {
            String regex = "^[a-zA-Z0-9]+$";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(name).matches();
        }
        return true;
    }
}