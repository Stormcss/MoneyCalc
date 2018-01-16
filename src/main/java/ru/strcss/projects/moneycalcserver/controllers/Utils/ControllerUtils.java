package ru.strcss.projects.moneycalcserver.controllers.Utils;

import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.enitities.dto.Status;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ControllerUtils {

    public static ValidationResult validateRegisterPerson(Person person){
        return proccessValidaionResults(person.getAccess().isValid(), person.getPersonalIdentifications().isValid());
    }

    private static ValidationResult proccessValidaionResults(ValidationResult... validationResults){
        boolean status = true;

        List reasons = new ArrayList();
        for (ValidationResult vr : validationResults){
            if (!vr.isValidated()) {
                status = false;
            }
            reasons.addAll(vr.getReasons());
        }
        return new ValidationResult(status, reasons);
    }

    public static boolean isPersonLoginExists(String login, PersonRepository repository) {
        // FIXME: 13.01.2018 Returning the whole Person is not the best practice
        return repository.findPersonByAccess_Login(login) != null;
    }

    public static boolean isPersonEmailExists(String email, PersonRepository repository) {
        // FIXME: 13.01.2018 Returning the whole Person is not the best practice
        return repository.findPersonByAccess_Email(email) != null;
    }

    public static AjaxRs responseError(String message) {
        return AjaxRs.builder()
                .message(message)
                .status(Status.ERROR)
                .build();
    }

    public static <E> AjaxRs<E> responseSuccess(String message, E payload) {

        return AjaxRs.<E>builder()
                .message(message)
                .status(Status.SUCCESS)
                .payload(payload)
                .build();
    }
}
