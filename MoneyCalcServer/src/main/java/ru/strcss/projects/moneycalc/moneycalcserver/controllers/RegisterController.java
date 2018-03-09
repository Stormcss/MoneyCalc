package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.RegisterAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.RegistrationDBConnection;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.getRegisteringPerson;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.getRegisteringPersonTransactions;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
public class RegisterController extends AbstractController implements RegisterAPIService {

    private RegistrationDBConnection registrationDBConnection;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private MongoTemplate mongoTemplate;

    public RegisterController(RegistrationDBConnection registrationDBConnection, BCryptPasswordEncoder bCryptPasswordEncoder, MongoTemplate mongoTemplate) {
        this.registrationDBConnection = registrationDBConnection;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 1) Checking if person has required fields filled
     * 2) Checking if the same login and email exists
     *
     * @param credentials - wrapper with Access and Identifications objects inside
     * @return AjaxRs
     */
    @PostMapping(value = "/register")
    public AjaxRs<Person> registerPerson(@RequestBody Credentials credentials) {

        RequestValidation<Person> requestValidation = new Validator(credentials, "Registering Person")
                .addValidation(() -> credentials.getAccess().isValid().isValidated(),
                        () -> fillLog(REGISTER_ERROR, credentials.getAccess().isValid().getReasons().toString()), "Access")
                .addValidation(() -> credentials.getIdentifications().isValid().isValidated(),
                        () -> fillLog(REGISTER_ERROR, credentials.getIdentifications().isValid().getReasons().toString()), "Identifications")
                .addValidation(() -> !registrationDBConnection.isPersonExistsByLogin(credentials.getAccess().getLogin()),
                        () -> fillLog(PERSON_LOGIN_ALREADY_EXISTS, credentials.getAccess().getLogin()))
                .addValidation(() -> !registrationDBConnection.isPersonExistsByEmail(credentials.getAccess().getEmail()),
                        () -> fillLog(PERSON_EMAIL_ALREADY_EXISTS, credentials.getAccess().getEmail()))
                .addValidation(() -> ValidationUtils.isEmailValid(credentials.getAccess().getEmail()),
                        () -> fillLog(PERSON_EMAIL_INCORRECT, credentials.getAccess().getEmail()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        credentials.getAccess().setPassword(bCryptPasswordEncoder.encode(credentials.getAccess().getPassword()));

        String login = credentials.getAccess().getLogin();

        log.info("Registering new Person with Login: {} and Name: {}", login, credentials.getIdentifications().getName());

        // TODO: 02.02.2018 TRANSACTIONS REQUIRED!

//        Transactional.startTransaction()
//                .then(() -> mongoOperations.save(personTransactions, "Transactions"))
//                .then(() -> mongoOperations.save(person, "Person"))
//                .endTransaction();

        mongoTemplate.save(getRegisteringPersonTransactions(login), "Transactions");
        mongoTemplate.save(getRegisteringPerson(login, credentials), "Person");


        // TODO: 02.02.2018 validate if save is successful

        return responseSuccess(REGISTER_SUCCESSFUL, null);
    }

}
