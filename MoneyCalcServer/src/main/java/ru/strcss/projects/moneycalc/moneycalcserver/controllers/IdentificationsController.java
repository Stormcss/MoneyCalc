package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.RequestFailedException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.IdentificationsService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.IDENTIFICATIONS_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.IDENTIFICATIONS_NOT_RETURNED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.IDENTIFICATIONS_SAVING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;

@Slf4j
@RestController
@RequestMapping("/api/identifications")
@AllArgsConstructor
public class IdentificationsController implements AbstractController {

    private IdentificationsService identificationsService;

    /**
     * Get Identifications object
     *
     * @return response object with Identifications payload
     */
    @GetMapping
    public Identifications getIdentifications() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Identifications identifications = identificationsService.getIdentifications(login);

        if (identifications == null) {
            log.error("Can not return Identifications for login '{}'", login);
            throw new RequestFailedException(HttpStatus.NOT_FOUND, IDENTIFICATIONS_NOT_RETURNED);
        }
        log.debug("returning Identifications for login '{}': {}", login, identifications);
        return identifications;
    }

    /**
     * Save Identifications object using user's login
     *
     * @param identifications - Identifications object itself
     * @return response object with Identifications payload
     */
    @PutMapping
    public Identifications updateIdentifications(@RequestBody Identifications identifications) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation requestValidation = new Validator(identifications, "Saving Identifications")
                .addValidation(() -> identifications.isValid().isValidated(),
                        () -> fillLog(IDENTIFICATIONS_INCORRECT, identifications.isValid().getReasons().toString()))
                .validate();

        if (!requestValidation.isValid()) throw new IncorrectRequestException(requestValidation.getReason());

        boolean isUpdated = identificationsService.updateIdentifications(login, identifications);

        if (!isUpdated) {
            log.error("Updating Identifications for login '{}' has failed", login);
            throw new RequestFailedException(HttpStatus.INTERNAL_SERVER_ERROR, IDENTIFICATIONS_SAVING_ERROR);
        }
        return identificationsService.getIdentifications(login);
    }
}
