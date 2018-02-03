package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.api.RegisterAPIService;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.controllers.dto.Credentials;
import ru.strcss.projects.moneycalcserver.controllers.dto.ValidationResult;
import ru.strcss.projects.moneycalcserver.enitities.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ValidationUtils.isPersonExists;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ValidationUtils.validateRegisterPerson;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
public class RegisterController extends AbstractController implements RegisterAPIService {
    /**
     * 1) Checking if person has required fields filled
     * 2) Checking if the same login and email exists
     *
     * @param credentials - wrapper with Access and Identifications objects inside
     * @return AjaxRs
     */

    public AjaxRs registerPerson(@RequestBody Credentials credentials) {

        ValidationResult validationResult = validateRegisterPerson(credentials.getAccess(), credentials.getIdentifications());

        // TODO: 15.01.2018 add email verification

        if (!validationResult.isValidated()) {
            log.error("Person registration failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        ValidationResult personExists = isPersonExists(credentials.getAccess(), repository);

        if (!personExists.isValidated()) {
            log.error("Person registration failed - required fields are empty: {}", personExists.getReasons());
            return responseError("Required fields are empty: " + personExists.getReasons());
        }

        String login = credentials.getAccess().getLogin();

        log.debug("Registering new Person with Login: {} and Name: {}", login, credentials.getIdentifications().getName());


        Person person = Person.builder()
                .ID(login)
                .access(credentials.getAccess())
                .identifications(credentials.getIdentifications())
                .settings(Settings.builder()
                        ._id(login)
                        .periodFrom(formatDateToString(currentDate()))
                        .periodTo(formatDateToString(generateDatePlus(ChronoUnit.MONTHS, 1)))
                        .sections(new ArrayList<>())
                        .build())
                .finance(Finance.builder()
                        ._id(login)
                        .financeSummary(FinanceSummary.builder()
                                ._id(login)
                                .financeSections(new ArrayList<>())
                                .build())
                        .financeStatistics(FinanceStatistics.builder()
                                .build())
                        .build())
                .build();

        PersonTransactions personTransactions = PersonTransactions.builder()
                .login(login)
                .transactions(new ArrayList<>())
                .build();

        // TODO: 02.02.2018 TRANSACTIONS REQUIRED!

        mongoOperations.save(personTransactions, "Transactions");
        mongoOperations.save(person, "Person");

        // TODO: 02.02.2018 validate if save is successful

        return responseSuccess(REGISTER_SUCCESSFUL, null);

    }

}
