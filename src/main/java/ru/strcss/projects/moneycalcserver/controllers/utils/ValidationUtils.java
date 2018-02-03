package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionContainer;
import ru.strcss.projects.moneycalcserver.controllers.dto.ValidationResult;
import ru.strcss.projects.moneycalcserver.enitities.Access;
import ru.strcss.projects.moneycalcserver.enitities.Identifications;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationUtils {

    public static ValidationResult validateRegisterPerson(Access access, Identifications identifications) {
        return processValidationResults(access.isValid(), identifications.isValid());
    }

    private static ValidationResult processValidationResults(ValidationResult... validationResults) {
        boolean status = true;

        List reasons = new ArrayList();
        for (ValidationResult vr : validationResults) {
            if (!vr.isValidated()) {
                status = false;
            }
            reasons.addAll(vr.getReasons());
        }
        return new ValidationResult(status, reasons);
    }

    public static ValidationResult isPersonExists(Access access, PersonRepository repository) {

        ArrayList<String> reasons = new ArrayList<>();

        boolean loginExists = repository.findPersonByAccess_Login(access.getLogin()) != null;
        boolean emailExists = repository.findPersonByAccess_Email(access.getEmail()) != null;

        if (loginExists) reasons.add("Login is already registered");
        if (emailExists) reasons.add("Email is already registered");

        return new ValidationResult(!loginExists && !emailExists, reasons);
    }

    public static ValidationResult validateTransactionContainer(TransactionContainer container) {

        if (container == null)
            return new ValidationResult(false, Collections.singletonList("Empty request"));

        if (container.getTransaction() == null)
            return new ValidationResult(false, Collections.singletonList("Transaction is empty"));

        return container.getTransaction().isValid();
    }
}
