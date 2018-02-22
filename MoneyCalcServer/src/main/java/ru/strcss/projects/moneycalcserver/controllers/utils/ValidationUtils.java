package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

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

//    public static ValidationResult validateAbstractTransactionContainer(AbstractContainer container) {
//        switch (container instanceof )
//            case
//
//    }

//    public static ValidationResult validateAbstractTransactionContainer(AbstractTransactionContainer container) {
//        // FIXME: 05.02.2018 WTF IS HAPPENING HERE
//
////        System.err.println("container = " + container);
//
//        if (container == null)
//            return new ValidationResult(false, Collections.singletonList("Empty request"));
//
//        if (container instanceof TransactionContainer) {
//            if (((TransactionContainer) container).getTransaction() == null) {
//                return new ValidationResult(false, Collections.singletonList("Transaction is empty"));
//            }
//        } else if (container instanceof TransactionUpdateContainer) {
//            if (((TransactionUpdateContainer) container).getTransaction() == null) {
//                return new ValidationResult(false, Collections.singletonList("Transaction is empty"));
//            }
//        } else if (container instanceof TransactionDeleteContainer) {
//            if (((TransactionDeleteContainer) container).getId() == null) {
//                return new ValidationResult(false, Collections.singletonList("Transaction id is empty"));
//            }
//        }
//        return new ValidationResult(true, Collections.emptyList());
//    }

//    public static ValidationResult validateTransactionContainer(TransactionContainer container) {
//        return validateAbstractTransactionContainer(container);
//    }
//
//    public static ValidationResult validateTransactionUpdateContainer(TransactionUpdateContainer container) {
//        return validateAbstractTransactionContainer(container);
//    }
//
//    public static ValidationResult validateTransactionDeleteContainer(TransactionDeleteContainer container) {
//        return validateAbstractTransactionContainer(container);
//    }
//    public static ValidationResult validateTransactionContainer(TransactionContainer container) {
//
//        if (container == null)
//            return new ValidationResult(false, Collections.singletonList("Empty request"));
//
//        if (container.getTransaction() == null)
//            return new ValidationResult(false, Collections.singletonList("Transaction is empty"));
//
//        return container.getTransaction().isValid();
//    }
//
//    public static ValidationResult validateTransactionUpdateContainer(TransactionUpdateContainer container) {
//
//        if (container == null)
//            return new ValidationResult(false, Collections.singletonList("Empty request"));
//
//        if (container.getTransaction() == null)
//            return new ValidationResult(false, Collections.singletonList("Transaction is empty"));
//
//        return container.isValid();
//    }
//
//    public static ValidationResult validateTransactionDeleteContainer(TransactionDeleteContainer container) {
//
//        if (container == null)
//            return new ValidationResult(false, Collections.singletonList("Empty request"));
//
//        if (container.getLogin() == null)
//            return new ValidationResult(false, Collections.singletonList("Transaction id is empty"));
//
//        return container.isValid();
//    }
}
