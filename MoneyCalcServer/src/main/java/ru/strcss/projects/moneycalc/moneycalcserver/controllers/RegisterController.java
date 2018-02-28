package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.RegisterAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.*;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ValidationUtils.isPersonExists;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ValidationUtils.validateRegisterPerson;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
public class RegisterController extends AbstractController implements RegisterAPIService {

    private SettingsDBConnection settingsDBConnection;

    @Autowired
    public RegisterController(SettingsDBConnection settingsDBConnection) {
        this.settingsDBConnection = settingsDBConnection;
    }

    /**
     * 1) Checking if person has required fields filled
     * 2) Checking if the same login and email exists
     *
     * @param credentials - wrapper with Access and Identifications objects inside
     * @return AjaxRs
     */
    @PostMapping(value = "/registerPerson")
    public AjaxRs<Person> registerPerson(@RequestBody Credentials credentials) {

        ValidationResult validationResult = validateRegisterPerson(credentials.getAccess(), credentials.getIdentifications());

        // TODO: 15.01.2018 add email verification

        if (!validationResult.isValidated()) {
            log.error("Person registration has failed - required fields are incorrect: {}", validationResult.getReasons());
            return ControllerUtils.responseError("Required fields are incorrect: " + validationResult.getReasons());
        }

        ValidationResult personExists = isPersonExists(credentials.getAccess(), repository);

        if (!personExists.isValidated()) {
            log.error("Person registration has failed - required fields are incorrect: {}", personExists.getReasons());
            return ControllerUtils.responseError("Required fields are incorrect: " + personExists.getReasons());
        }

        String login = credentials.getAccess().getLogin();

        log.info("Registering new Person with Login: {} and Name: {}", login, credentials.getIdentifications().getName());

        Person person = Person.builder()
                .ID(login)
                .access(credentials.getAccess())
                .identifications(credentials.getIdentifications())
                .settings(Settings.builder()
                        .login(login)
                        .periodFrom(ControllerUtils.formatDateToString(GenerationUtils.currentDate()))
                        .periodTo(ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.MONTHS, 1)))
                        .sections(Arrays.asList(SpendingSection.builder()
                                        .id(0)
                                        .isAdded(true)
                                        .budget(5000)
                                        .name("Еда")
                                        .build(),
                                SpendingSection.builder()
                                        .id(1)
                                        .budget(5000)
                                        .isAdded(true)
                                        .name("Прочее")
                                        .build()))
                        .build())
                .finance(Finance.builder()
                        ._id(login)
                        .financeSummary(FinanceSummary.builder()
                                ._id(login)
                                .financeSections(new ArrayList<>())
                                .build())
//                        .financeStatistics(FinanceStatistics.builder()
//                                .build())
                        .build())
                .build();

        PersonTransactions personTransactions = PersonTransactions.builder()
                .login(login)
                .transactions(new ArrayList<>())
                .build();

        // TODO: 02.02.2018 TRANSACTIONS REQUIRED!

//        Transactional.startTransaction()
//                .then(() -> mongoOperations.save(personTransactions, "Transactions"))
//                .then(() -> mongoOperations.save(person, "Person"))
//                .endTransaction();

//        SpendingSection section1 = SpendingSection.builder()
//                .id(0)
//                .isAdded(true)
//                .budget(5000)
//                .name("Еда")
//                .build();
//        SpendingSection section2 = SpendingSection.builder()
//                .id(1)
//                .budget(5000)
//                .isAdded(true)
//                .name("Прочее")
//                .build();

        mongoOperations.save(personTransactions, "Transactions");
        mongoOperations.save(person, "Person");

//        settingsDBConnection.addSpendingSection(new SpendingSectionAddContainer(credentials.getAccess().getLogin(), section1));
//        settingsDBConnection.addSpendingSection(new SpendingSectionAddContainer(credentials.getAccess().getLogin(), section2));

        // TODO: 02.02.2018 validate if save is successful

        return responseSuccess(REGISTER_SUCCESSFUL, null);

    }

}
