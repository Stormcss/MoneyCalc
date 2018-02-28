package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.IdentificationsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.IdentificationsDBConnection;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/identifications/")
public class IdentificationsController extends AbstractController implements IdentificationsAPIService {

    private IdentificationsDBConnection identificationsDBConnection;

    @Autowired
    public IdentificationsController(IdentificationsDBConnection identificationsDBConnection) {
        this.identificationsDBConnection = identificationsDBConnection;
    }

    /**
     * Save Identifications object using user's login
     *
     * @param identifications - user's login
     * @return response object with Identifications payload
     */
    @PostMapping(value = "/saveIdentifications")
    public AjaxRs<Identifications> saveIdentifications(@RequestBody Identifications identifications) {

        RequestValidation<Identifications> requestValidation = new Validator(identifications, "Saving Settings")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(identifications.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, identifications.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        final WriteResult updateResult = identificationsDBConnection.updateIdentifications(identifications);

        if (updateResult.getN() == 0) {
            log.error("Updating Identifications for login {} has failed", identifications.getLogin());
            return responseError(IDENTIFICATIONS_SAVING_ERROR);
        }
        return responseSuccess(IDENTIFICATIONS_RETURNED, identifications);
    }

    /**
     * Get Identifications object using user's login
     *
     * @param getContainer - container with user's login
     * @return response object with Identifications payload
     */
    @PostMapping(value = "/getIdentifications")
    public AjaxRs<Identifications> getIdentifications(@RequestBody LoginGetContainer getContainer) {

//        // TODO: 25.02.2018 receive object and validate it
//
//        if (!isPersonExist(login)){
//            log.error("Person with login {} does not exist!", login);
//            return responseError(NO_PERSON_EXIST);
//        }

        RequestValidation<Identifications> requestValidation = new Validator(getContainer, "Requesting Identifications")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(getContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, getContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Identifications identifications = identificationsDBConnection.getIdentifications(getContainer.getLogin());

        if (identifications != null) {
            log.debug("returning Identifications for login {}: {}", getContainer.getLogin(), identifications);
            return responseSuccess(IDENTIFICATIONS_RETURNED, identifications);
        } else {
            log.error("Can not return Identifications for login {} - no Person found", getContainer.getLogin());
            return responseError(NO_PERSON_EXIST);
        }
    }
}
