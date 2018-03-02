package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import java.util.ArrayList;
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

        // FIXME: 22.02.2018 use proper requests to DB
        boolean loginExists = repository.findPersonByAccess_Login(access.getLogin()) != null;
        boolean emailExists = repository.findPersonByAccess_Email(access.getEmail()) != null;

        if (loginExists) reasons.add("Login is already registered");
        if (emailExists) reasons.add("Email is already registered");

        return new ValidationResult(!loginExists && !emailExists, reasons);
    }

}
