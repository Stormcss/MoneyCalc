package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.IdentificationsService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/identifications")
public class IdentificationsController extends AbstractController {

    private IdentificationsService identificationsService;

    public IdentificationsController(IdentificationsService identificationsService) {
        this.identificationsService = identificationsService;
    }

    /**
     * Save Identifications object using user's login
     *
     * @param identifications - Identifications object itself
     * @return response object with Identifications payload
     */
    @PutMapping
    public ResponseEntity<MoneyCalcRs<Identifications>> updateIdentifications(@RequestBody Identifications identifications) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Identifications> requestValidation = new Validator(identifications, "Saving Identifications")
                .addValidation(() -> identifications.isValid().isValidated(),
                        () -> fillLog(IDENTIFICATIONS_INCORRECT, identifications.isValid().getReasons().toString()))
                .validate();

        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        boolean isUpdated = identificationsService.updateIdentifications(login, identifications);

        if (!isUpdated) {
            log.error("Updating Identifications for login \'{}\' has failed", login);
            return responseError(IDENTIFICATIONS_SAVING_ERROR);
        }
        return responseSuccess(IDENTIFICATIONS_SAVED, identificationsService.getIdentifications(login));
    }

    /**
     * Get Identifications object
     *
     * @return response object with Identifications payload
     */
    @GetMapping
    public ResponseEntity<MoneyCalcRs<Identifications>> getIdentifications() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Identifications identifications = identificationsService.getIdentifications(login);

        if (identifications != null) {
            log.debug("returning Identifications for login \'{}\': {}", login, identifications);
            return responseSuccess(IDENTIFICATIONS_RETURNED, identifications);
        } else {
            log.error("Can not return Identifications for login \'{}\'", login);
            return responseError(IDENTIFICATIONS_NOT_RETURNED);
        }
    }
}
