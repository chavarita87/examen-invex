package com.invex.examen.constant;

/**
 * Validation and error messages returned by the API.
 */
public final class ValidationMessages {

    public static final String NAME_PATTERN = "^\\p{L}+([ '-]\\p{L}+)*$";
    public static final String NOT_BLANK_PATTERN = "(?s).*\\S.*";

    public static final String REQUIRED = "is required";
    public static final String INVALID_NAME = "must contain only letters, spaces, apostrophes or hyphens";
    public static final String NOT_BLANK = "must not be blank";
    public static final String MAX_LENGTH = "must have at most {max} characters";
    public static final String MIN_AGE = "must be at least {value}";
    public static final String MAX_AGE = "must be at most {value}";
    public static final String PAST_DATE = "must be a date in the past";
    public static final String POSITIVE_ID = "must be a positive number";
    public static final String EMPTY_LIST = "must contain at least one employee";
    public static final String LIST_SIZE = "must contain at most {max} employees";

    public static final String AGE_MISMATCH =
            "age %d does not match birth date %s (expected %d)";
    public static final String MALFORMED_REQUEST =
            "Malformed request body. Dates must use the format " + EmployeeConstants.DATE_FORMAT
                    + " and gender must be one of: MALE, FEMALE, OTHER";
    public static final String INVALID_PARAMETER = "Parameter '%s' has an invalid value: %s";
    public static final String MISSING_PARAMETER = "Parameter '%s' is required";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String DATA_CONFLICT = "The operation conflicts with existing data";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";

    private ValidationMessages() {
    }
}
