package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

public abstract class AbstractController {

    final String REGISTER_SUCCESSFUL = "Person successfully registered";
    final String NO_PERSON_EXIST = "Person does not exist!";
    final String NO_PERSON_LOGIN_EXISTS = "Person with login \"%s\" does not exist!";
    final String PERSON_LOGIN_ALREADY_EXISTS = "Person with login \"%s\" is already registered!";
    final String PERSON_EMAIL_ALREADY_EXISTS = "Person with email \"%s\" is already registered!";
    final String REGISTER_ERROR = "Can not perform registration: %s";
    final String PERSON_EMAIL_INCORRECT = "Email \"%s\" is incorrect!";

    final String SETTINGS_UPDATED = "Settings successfully updated";
    final String SETTINGS_RETURNED = "Settings successfully returned";
    final String SETTINGS_INCORRECT = "Settings has incorrect fields: %s";

    final String ACCESS_RETURNED = "Access object successfully returned";

    final String SPENDING_SECTIONS_RETURNED = "Spending Sections successfully returned";
    final String SPENDING_SECTION_ADDED = "Spending Section successfully added";
    final String SPENDING_SECTION_DELETED = "Spending Section successfully deleted";
    final String SPENDING_SECTION_UPDATED = "Spending Section successfully updated";
    final String SPENDING_SECTION_NAME_EXISTS = "Spending Section with name \"%s\" already exists";
    final String SPENDING_SECTION_ID_NOT_EXISTS = "Spending Section with id \"%s\" does not exist";
    final String SPENDING_SECTION_INCORRECT = "Spending Section has incorrect fields: \"%s\"";
    final String SPENDING_SECTION_NOT_FOUND = "Spending Section was not found!";
    final String SPENDING_SECTION_SAVING_ERROR = "ERROR has occurred while saving Spending Section!";

    final String IDENTIFICATIONS_SAVED = "Identifications successfully saved";
    final String IDENTIFICATIONS_RETURNED = "Identifications successfully returned";
    final String IDENTIFICATIONS_SAVING_ERROR = "Identifications were not updated!";
    final String IDENTIFICATIONS_INCORRECT = "Can not perform updating Identifications: %s";

    final String TRANSACTION_SAVING_ERROR = "ERROR has occurred while saving Transaction";
    final String TRANSACTION_SAVED = "TRANSACTION successfully saved";
    final String TRANSACTION_DELETED = "TRANSACTION successfully deleted";
    final String TRANSACTION_UPDATED = "TRANSACTION successfully updated";
    final String TRANSACTION_NOT_UPDATED = "Updating transaction has failed!";
    final String TRANSACTIONS_RETURNED = "TRANSACTIONS successfully returned";
    final String TRANSACTION_INCORRECT = "TRANSACTION has incorrect fields: %s";
    final String TRANSACTION_NOT_FOUND = "TRANSACTION was not found!";

    final String STATISTICS_RETURNED = "Statistics successfully returned";
    final String DATE_INCORRECT = "Date [%s] is incorrect: must be [yyyy-MM-dd]";
    final String DATE_SEQUENCE_INCORRECT = "Incorrect dates: dateTo must be after dateFrom";
}
