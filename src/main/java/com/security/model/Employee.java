package com.security.model;

import com.security.validation.NameValidation;
import lombok.Data;

@Data
public class Employee {

    private String id;

    @NameValidation()
    private String name;
}
