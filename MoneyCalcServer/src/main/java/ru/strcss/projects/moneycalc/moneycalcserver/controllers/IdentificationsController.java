package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.IdentificationsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.IdentificationsService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/identifications/")
public class IdentificationsController extends AbstractController implements IdentificationsAPIService {

    private IdentificationsService identificationsService;
    private PersonService personService;

    public IdentificationsController(IdentificationsService identificationsService, PersonService personService) {
        this.identificationsService = identificationsService;
        this.personService = personService;

    }

    /**
     * Save Identifications object using user's login
     *
     * @param updateContainer - container with user's login and Identifications object
     * @return response object with Identifications payload
     */
    @PostMapping(value = "/update")
    public ResponseEntity<MoneyCalcRs<Identifications>> updateIdentifications(@RequestBody IdentificationsUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Identifications> requestValidation = new Validator(updateContainer, "Saving Identifications")
                .addValidation(() -> updateContainer.getIdentifications().isValid().isValidated(),
                        () -> fillLog(IDENTIFICATIONS_INCORRECT, updateContainer.getIdentifications().isValid().getReasons().toString()))
                .validate();

        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Integer personId = personService.getPersonIdByLogin(login);
        Integer identificationsId = personService.getIdentificationsIdByPersonId(personId);

        updateContainer.getIdentifications().setId(identificationsId);

        Identifications identifications = identificationsService.updateIdentifications(updateContainer.getIdentifications());

        if (identifications == null) {
            log.error("Updating Identifications for login \'{}\' has failed", login);
            return responseError(IDENTIFICATIONS_SAVING_ERROR);
        }
        return responseSuccess(IDENTIFICATIONS_SAVED, updateContainer.getIdentifications());
    }

    /**
     * Get Identifications object
     *
     * @return response object with Identifications payload
     */
    @GetMapping(value = "/get")
    public ResponseEntity<MoneyCalcRs<Identifications>> getIdentifications() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer personId = personService.getPersonIdByLogin(login);
        Integer identificationsId = personService.getIdentificationsIdByPersonId(personId);


        Identifications identifications = identificationsService.getIdentificationsById(identificationsId);

        if (identifications != null) {
            log.debug("returning Identifications for login \'{}\': {}", login, identifications);
            return responseSuccess(IDENTIFICATIONS_RETURNED, identifications);
        } else {
            log.error("Can not return Identifications for login \'{}\' - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }
}
