package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;
import ru.strcss.projects.moneycalcserver.enitities.dto.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
public class RegisterController extends AbstractController {
    /**
     * 1) Checking if person has required fields filled
     * 2) Checking if the same login and email exists
     *
     * @param credentials - wrapper with Access and Identifications objects inside
     * @return AjaxRs
     */

    @PostMapping(value = "/registerPerson")
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
                        .periodFrom(createDate())
                        .periodTo(generateDatePlus(ChronoUnit.MONTHS, 1))
                        .sections(new ArrayList<>())
                        .build())
                .finance(Finance.builder()
                        ._id(login)
                        .financeSummary(FinanceSummary.builder()
                                ._id(login)
                                .financeSections(new ArrayList<>())
                                .build())
                        .financeStatistics(null)
                        .build())
                .build();

        return responseSuccess(REGISTER_SUCCESSFUL, repository.save(person));

//            if (!ControllerUtils.isPersonLoginExists(personIncome.getAccess().getLogin(), repository)) {
//                if (!ControllerUtils.isPersonEmailExists(personIncome.getAccess().getEmail(), repository)) {
//                    log.debug("Registering new Person: {}", personIncome);
//                    return ControllerUtils.responseSuccess(REGISTER_SUCCESSFULL, repository.save(personIncome));
//                } else {
//                    log.error("Person with email {} already exists", personIncome.getAccess().getEmail());
//                    return ControllerUtils.responseError("Person with email " + personIncome.getAccess().getEmail() + " already exists!");
//                }
//            } else {
//                log.error("Person with login {} already exists", personIncome.getAccess().getLogin());
//                return ControllerUtils.responseError("Person with login " + personIncome.getAccess().getLogin() + " already exists!");
//            }
//        } else {
//            log.error("Person registration failed - required fields are empty: {}", validationResult.getReasons());
//            return ControllerUtils.responseError("Required fields are empty: " + validationResult.getReasons());
//        }
    }

}
