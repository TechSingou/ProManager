package ml.malikura.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        startDateField = constraintAnnotation.startDateField();
        endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime startDate = null;
        Boolean isValid = null;
        try {
            startDate = (LocalDateTime) getValue(o, startDateField);
            LocalDateTime endDate = (LocalDateTime) getValue(o, endDateField);
            if (startDate == null || endDate == null) {
                return true; // Let @NotNull or @Future handle null values
            }
            isValid = startDate.isBefore(endDate) && startDate.isAfter(LocalDateTime.now()) && endDate.isAfter(LocalDateTime.now());

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return isValid;
    }

    private Object getValue(Object object, String fieldName) throws IllegalAccessException {
        Field field = null;

        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to access field: " + fieldName, e);
        }
        return field.get(object);
    }

}
