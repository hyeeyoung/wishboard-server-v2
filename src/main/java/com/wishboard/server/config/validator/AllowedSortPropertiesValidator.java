package com.wishboard.server.config.validator;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedSortPropertiesValidator implements ConstraintValidator<AllowedSortProperties, Pageable> {

    private Set<String> allowedSortProperties;

    @Override
    public void initialize(AllowedSortProperties constraintAnnotation) {
        this.allowedSortProperties = new HashSet<>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {
        if (value == null || value.getSort() == null) {
            return true;
        }
        if (allowedSortProperties.isEmpty()) {
            return true;
        }
        for (Sort.Order order : value.getSort()) {
            if (!allowedSortProperties.contains(order.getProperty())) {
                return false;
            }
        }
        return true;
    }
}
