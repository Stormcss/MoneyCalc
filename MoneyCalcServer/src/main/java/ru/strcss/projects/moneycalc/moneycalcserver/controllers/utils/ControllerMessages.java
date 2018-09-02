package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

public class ControllerMessages {

    //Other Errors
    public static final String DATE_INCORRECT = "Date [%s] is incorrect: must be [yyyy-MM-dd]";
    public static final String DATE_SEQUENCE_INCORRECT = "Incorrect dates: dateTo must be after dateFrom";
    public static final String DEFAULT_ERROR = "Error";

    //Register
    public static final String REGISTER_SUCCESSFUL = "Person successfully registered";
    public static final String NO_PERSON_EXIST = "Person does not exist!";
    public static final String NO_PERSON_LOGIN_EXISTS = "Person with login \'%s\' does not exist!";
    public static final String PERSON_LOGIN_ALREADY_EXISTS = "Person with login \'%s\' is already registered!";
    public static final String PERSON_EMAIL_ALREADY_EXISTS = "Person with email \'%s\' is already registered!";
    public static final String REGISTER_ERROR = "Can not perform registration: %s";
    public static final String PERSON_EMAIL_INCORRECT = "Email \'%s\' is incorrect!";

    //Settings
    public static final String SETTINGS_UPDATED = "Settings successfully updated";
    public static final String SETTINGS_RETURNED = "Settings successfully returned";
    public static final String SETTINGS_INCORRECT = "Settings has incorrect fields: %s";

    //Access
    public static final String ACCESS_RETURNED = "Access object successfully returned";

    //SpendingSections
    public static final String SPENDING_SECTIONS_RETURNED = "Spending Sections successfully returned";
    public static final String SPENDING_SECTION_ADDED = "Spending Section successfully added";
    public static final String SPENDING_SECTION_DELETED = "Spending Section successfully deleted";
    public static final String SPENDING_SECTION_ALREADY_DELETED = "Spending Section is already deleted!";
    public static final String SPENDING_SECTION_NOT_DELETED = "Spending Section was not deleted!";
    public static final String SPENDING_SECTION_UPDATED = "Spending Section successfully updated";
    public static final String SPENDING_SECTION_NAME_EXISTS = "Spending Section with name \'%s\' already exists";
    public static final String SPENDING_SECTION_ID_NOT_EXISTS = "Spending Section with id \'%s\' does not exist";
    public static final String SPENDING_SECTION_INCORRECT = "Spending Section has incorrect fields: \'%s\'";
    public static final String SPENDING_SECTION_NOT_FOUND = "Spending Section was not found!";
    public static final String SPENDING_SECTION_SAVING_ERROR = "ERROR has occurred while saving Spending Section!";

    //Identifications
    public static final String IDENTIFICATIONS_SAVED = "Identifications successfully saved";
    public static final String IDENTIFICATIONS_RETURNED = "Identifications successfully returned";
    public static final String IDENTIFICATIONS_SAVING_ERROR = "Identifications were not updated!";
    public static final String IDENTIFICATIONS_INCORRECT = "Can not perform updating Identifications: %s";

    //Transactions
    public static final String TRANSACTION_SAVING_ERROR = "ERROR has occurred while saving Transaction";
    public static final String TRANSACTION_SAVED = "TRANSACTION successfully saved";
    public static final String TRANSACTION_DELETED = "TRANSACTION successfully deleted";
    public static final String TRANSACTION_UPDATED = "TRANSACTION successfully updated";
    public static final String TRANSACTION_NOT_UPDATED = "Updating transaction has failed!";
    public static final String TRANSACTIONS_RETURNED = "TRANSACTIONS successfully returned";
    public static final String TRANSACTION_INCORRECT = "TRANSACTION has incorrect fields: %s";
    public static final String TRANSACTION_NOT_FOUND = "TRANSACTION was not found!";

    //Statistics
    public static final String STATISTICS_RETURNED = "Statistics successfully returned";
}
