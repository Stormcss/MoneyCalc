package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

public abstract class AbstractController {
    @Autowired
    public PersonRepository repository;

    @Autowired
    MongoOperations mongoOperations;

    final String REGISTER_SUCCESSFUL = "Person successfully registered";
    final String NO_PERSON_EXIST = "Person with login {} does not exist!";

    final String SETTINGS_UPDATED = "Settings successfully updated";
    final String SETTINGS_RETURNED = "Settings successfully returned";

    final String SPENDING_SECTIONS_RETURNED = "Spending Sections successfully returned";
    final String SPENDING_SECTION_ADDED = "Spending Section successfully added";
    final String SPENDING_SECTION_DELETED = "Spending Section successfully deleted";
    final String SPENDING_SECTION_UPDATED = "Spending Section successfully updated";
    final String SPENDING_SECTION_NAME_EXISTS = "Spending Section with name {} already exists";

    final String IDENTIFICATIONS_SAVED = "Identifications successfully saved";
    final String IDENTIFICATIONS_RETURNED = "Identifications successfully returned";
    final String IDENTIFICATIONS_SAVING_ERROR = "Identifications were not updated!";

    final String TRANSACTION_SAVING_ERROR = "ERROR has occurred while saving Transaction";
    final String TRANSACTION_SAVED = "TRANSACTION successfully saved";
    final String TRANSACTION_DELETED = "TRANSACTION successfully deleted";
    final String TRANSACTION_UPDATED = "TRANSACTION successfully updated";
    final String TRANSACTIONS_RETURNED = "TRANSACTIONS successfully returned";

    final String STATISTICS_RETURNED = "Statistics successfully returned";
}
