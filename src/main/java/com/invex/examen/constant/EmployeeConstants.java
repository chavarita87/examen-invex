package com.invex.examen.constant;

/**
 * Constants shared by the employee domain: table, column sizes and formats.
 */
public final class EmployeeConstants {

    public static final String TABLE_NAME = "employees";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static final int NAME_MAX_LENGTH = 50;
    public static final int POSITION_MAX_LENGTH = 100;
    public static final int GENDER_MAX_LENGTH = 10;

    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 100;
    public static final int MAX_BATCH_SIZE = 100;

    private EmployeeConstants() {
    }
}
