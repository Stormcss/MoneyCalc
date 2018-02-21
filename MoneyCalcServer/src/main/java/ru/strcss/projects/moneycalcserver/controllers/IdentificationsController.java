package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.IdentificationsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalcserver.dbconnection.IdentificationsDBConnection;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;

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

        ValidationResult validationResult = identifications.isValid();

        if (!validationResult.isValidated()) {
            log.error("Saving Identifications has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        // TODO: 20.01.2018 Find out if there are ways to get rid of unnecessary find request to db

//        Person person = repository.findPersonByAccess_Login(identifications.getLogin());
//
//        person.setIdentifications(identifications);

        final WriteResult updateResult = identificationsDBConnection.updateIdentifications(identifications);

        log.debug("updateResult is {}", updateResult);
        if (updateResult.getN() == 0) {
            log.error("Updating Identifications for login {} has failed", identifications.getLogin());
            return responseError(IDENTIFICATIONS_SAVING_ERROR);
        }

        //        DBObject dbObject = new BasicDBObject();
//        mongoOperations.getConverter().write(person, dbObject);
//
//        mongoOperations.upsert(query(where("login").is(identifications.getLogin())), Update.fromDBObject(dbObject, "login"), Person.class);
        return responseSuccess(IDENTIFICATIONS_RETURNED, identifications);
    }

    /**
     * Get Identifications object using user's login
     *
     * @param login - user's login
     * @return response object with Identifications payload
     */
    @PostMapping(value = "/getIdentifications")
    public AjaxRs<Identifications> getIdentifications(@RequestBody String login) {

        login = login.replace("\"", "");

        if (!isPersonExist(login)){
            log.error("Person with login {} does not exist!", login);
            return responseError(NO_PERSON_EXIST);
        }

        Identifications identifications = repository.findIdentificationsByAccess_Login(login).getIdentifications();

        if (identifications != null) {
            log.debug("returning Identifications for login {}: {}", login, identifications);
            return responseSuccess(IDENTIFICATIONS_RETURNED, identifications);
        } else {
            log.error("Can not return Identifications for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }
}
