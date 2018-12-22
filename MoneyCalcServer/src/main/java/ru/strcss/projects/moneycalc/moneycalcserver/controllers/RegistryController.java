package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.RegisterService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.PERSON_EMAIL_ALREADY_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.PERSON_EMAIL_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.PERSON_LOGIN_ALREADY_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.REGISTER_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.REGISTER_SUCCESSFUL;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isEmailValid;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
@AllArgsConstructor
public class RegistryController extends AbstractController {

    private RegisterService registerService;

    /**
     * 1) Checking if person has required fields filled
     * 2) Checking if the same login and email exists
     *
     * @param credentials - wrapper with Access and Identifications objects inside
     * @return AjaxRs
     */
    @PostMapping(value = "/register")
    public ResponseEntity<MoneyCalcRs<Person>> registerPerson(@RequestBody Credentials credentials) {

        RequestValidation<Person> requestValidation = new Validator(credentials, "Registering Person")
                .addValidation(() -> credentials.getAccess().isValid().isValidated(),
                        () -> fillLog(REGISTER_ERROR, credentials.getAccess().isValid().getReasons().toString()), "Access")
                .addValidation(() -> credentials.getIdentifications().isValid().isValidated(),
                        () -> fillLog(REGISTER_ERROR, credentials.getIdentifications().isValid().getReasons().toString()), "Identifications")
                .addValidation(() -> !registerService.isUserExistsByLogin(credentials.getAccess().getLogin()),
                        () -> fillLog(PERSON_LOGIN_ALREADY_EXISTS, credentials.getAccess().getLogin()))
                .addValidation(() -> !registerService.isUserExistsByEmail(credentials.getAccess().getEmail()),
                        () -> fillLog(PERSON_EMAIL_ALREADY_EXISTS, credentials.getAccess().getEmail()))
                .addValidation(() -> isEmailValid(credentials.getAccess().getEmail()),
                        () -> fillLog(PERSON_EMAIL_INCORRECT, credentials.getAccess().getEmail()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Person registeredUser = registerService.registerUser(credentials);

        return responseSuccess(REGISTER_SUCCESSFUL, registeredUser);
    }
}