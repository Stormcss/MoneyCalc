package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;
import ru.strcss.projects.moneycalcserver.controllers.entities.AbstractApiController;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
public class ApiControllerRegister extends AbstractApiController {
//    @Autowired
//    PersonRepository repository;

    /**
     * 1) We check if person has required fields filled
     * 2) We check if the same login exists
     * 3) We check if the same email exists
     *
     * @param personIncome - Person object for registering
     * @return AjaxRs
     */

    @PostMapping(value = "/registerPerson")
    public AjaxRs addPerson(@RequestBody Person personIncome) {

        ValidationResult validationResult = ControllerUtils.validateRegisterPerson(personIncome);

        // TODO: 15.01.2018 add email verification

        if (validationResult.isValidated()) {
            if (!ControllerUtils.isPersonLoginExists(personIncome.getAccess().getLogin(), repository)) {
                if (!ControllerUtils.isPersonEmailExists(personIncome.getAccess().getEmail(), repository)) {
                    log.debug("Registering new Person: {}", personIncome);
                    return ControllerUtils.responseSuccess(REGISTER_SUCCESSFULL, repository.save(personIncome));
                } else {
                    log.error("Person with email {} already exists", personIncome.getAccess().getEmail());
                    return ControllerUtils.responseError("Person with email " + personIncome.getAccess().getEmail() + " already exists!");
                }
            } else {
                log.error("Person with login {} already exists", personIncome.getAccess().getLogin());
                return ControllerUtils.responseError("Person with login " + personIncome.getAccess().getLogin() + " already exists!");
            }
        } else {
            log.error("Person registration failed - required field is empty: {}", validationResult.getReasons());
            return ControllerUtils.responseError("Required fields are empty: " + validationResult.getReasons());
        }
    }

}
