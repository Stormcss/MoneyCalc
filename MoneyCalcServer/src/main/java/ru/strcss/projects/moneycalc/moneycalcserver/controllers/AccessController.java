package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.AccessAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.AccessDBConnection;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/access/")
public class AccessController extends AbstractController implements AccessAPIService {
    // TODO: 06.03.2018 finish me

    private AccessDBConnection accessDBConnection;

    public AccessController(AccessDBConnection accessDBConnection) {
        this.accessDBConnection = accessDBConnection;
    }

    /**
     * Get Access object
     *
     * @return response object with Identifications payload
     */
    @GetMapping(value = "/getAccess")
    public AjaxRs<Access> getAccess() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Access> requestValidation = new RequestValidation.Validator(null, "Requesting Access")
                .addValidation(() -> repository.existsByAccess_Login(login),
                        () -> fillLog(NO_PERSON_EXIST, login))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Access access = accessDBConnection.getAccess(login);

        if (access != null) {
            log.debug("returning Access for login {}: {}", login, access);
            return responseSuccess(ACCESS_RETURNED, access);
        } else {
            log.error("Can not return Access for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }

    @Override
    public AjaxRs<Access> saveAccess(IdentificationsUpdateContainer updateContainer) {
        return null;
    }

}
