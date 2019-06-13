package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerMessages {

    //Other Errors
    public static final String DATE_INCORRECT = "Date [%s] is incorrect: must be [yyyy-MM-dd]";
    public static final String DATE_SEQUENCE_INCORRECT = "Incorrect date sequence: dateFrom must be before dateTo";

    //Register
    public static final String NO_PERSON_EXIST = "Person does not exist!";
    public static final String NO_PERSON_LOGIN_EXISTS = "Person with login \'%s\' does not exist!";
    public static final String PERSON_LOGIN_ALREADY_EXISTS = "Person with login \'%s\' is already registered!";
    public static final String PERSON_EMAIL_ALREADY_EXISTS = "Person with email \'%s\' is already registered!";
    public static final String REGISTER_ERROR = "Can not perform registration: %s";
    public static final String PERSON_EMAIL_INCORRECT = "Email \'%s\' is incorrect!";

    //Settings
    public static final String SETTINGS_UPDATING_ERROR = "Settings were not updated!";
    public static final String SETTINGS_NOT_FOUND = "Settings are not found!";
    public static final String SETTINGS_INCORRECT = "Settings has incorrect fields: %s";

    //Access
    public static final String ACCESS_RETURNED = "Access object successfully returned";

    //SpendingSections
    public static final String SPENDING_SECTION_NOT_DELETED = "Spending Section was not deleted!";
    public static final String SPENDING_SECTION_NAME_EXISTS = "Spending Section with name \'%s\' already exists";
    public static final String SPENDING_SECTION_ID_NOT_EXISTS = "Spending Section with id \'%s\' does not exist";
    public static final String SPENDING_SECTION_INCORRECT = "Spending Section has incorrect fields: \'%s\'";
    public static final String SPENDING_SECTION_EMPTY = "Spending Section is empty!";
    public static final String SPENDING_SECTION_NOT_FOUND = "Spending Section was not found!";
    public static final String SPENDING_SECTION_SAVING_ERROR = "ERROR has occurred while saving Spending Section!";

    //Identifications
    public static final String IDENTIFICATIONS_NOT_RETURNED = "Identifications were not returned!";
    public static final String IDENTIFICATIONS_SAVING_ERROR = "Identifications were not updated!";
    public static final String IDENTIFICATIONS_INCORRECT = "Can not perform updating Identifications: %s";

    //Transactions
    public static final String TRANSACTION_SAVING_ERROR = "ERROR has occurred while saving Transaction";
    public static final String TRANSACTION_NOT_DELETED = "Transaction was not deleted!";
    public static final String TRANSACTION_NOT_UPDATED = "Updating transaction has failed!";
    public static final String TRANSACTION_INCORRECT = "TRANSACTION has incorrect fields: %s";
    public static final String TRANSACTION_NOT_FOUND = "TRANSACTION was not found!";

}
