package ru.strcss.projects.moneycalcserver.controllers.Utils;

import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalcserver.enitities.dto.Access;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Identifications;
import ru.strcss.projects.moneycalcserver.enitities.dto.Status;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ControllerUtils {

    public static ValidationResult validateRegisterPerson(Access access, Identifications identifications){
        return processValidationResults(access.isValid(), identifications.isValid());
    }

//    public static ValidationResult validateSettings(Settings settings){
//        return proccessValidaionResults(settings.getAccess().isValid(), person.getIdentifications().isValid());
//    }

    private static ValidationResult processValidationResults(ValidationResult... validationResults){
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

    public static ValidationResult isPersonExists(Access access, PersonRepository repository){

        ArrayList<String> reasons = new ArrayList<>();

        boolean loginExists = repository.findPersonByAccess_Login(access.getLogin()) != null;
        boolean emailExists = repository.findPersonByAccess_Email(access.getEmail()) != null;

        if (loginExists) reasons.add("Login is already registered");
        if (emailExists) reasons.add("Email is already registered");

        return new ValidationResult(!loginExists && !emailExists, reasons);
    }

//    public static boolean isPersonLoginExists(String login, PersonRepository repository) {
//        // FIXME: 13.01.2018 Returning the whole Person is not the best practice
//        return repository.findPersonByAccess_Login(login) != null;
//    }
//
//    public static boolean isPersonEmailExists(String email, PersonRepository repository) {
//        // FIXME: 13.01.2018 Returning the whole Person is not the best practice
//        return repository.findPersonByAccess_Email(email) != null;
//    }

    public static String generateDatePlus(TemporalUnit unit, int count) {
        LocalDate now = LocalDate.now().plus(count, unit);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }

    public static String createDate() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
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
