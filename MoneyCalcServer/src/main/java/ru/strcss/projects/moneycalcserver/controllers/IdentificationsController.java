package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.IdentificationsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@RequestMapping("/api/identifications/")
public class IdentificationsController extends AbstractController implements IdentificationsAPIService {

    /**
     * Save Identifications object using user's login
     *
     * @param identifications - user's login
     * @return response object with Identifications payload
     */
    @PostMapping(value = "/saveIdentifications")
    public AjaxRs saveIdentifications(@RequestBody Identifications identifications) {

        ValidationResult validationResult = identifications.isValid();

        if (!validationResult.isValidated()) {
            log.error("Saving Identifications failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        // TODO: 20.01.2018 Find out if there are ways to get rid of unnecessary find request to db

        Person person = repository.findPersonByAccess_Login(identifications.get_id());

        person.setIdentifications(identifications);
        DBObject dbObject = new BasicDBObject();
        mongoOperations.getConverter().write(person, dbObject);

        mongoOperations.upsert(query(where("_id").is(identifications.get_id())), Update.fromDBObject(dbObject, "_id"), Person.class);
        return responseSuccess(RETURN_SETTINGS, identifications);
    }

    /**
     * Get Identifications object using user's login
     *
     * @param login - user's login
     * @return response object with Identifications payload
     */
    @PostMapping(value = "/getIdentifications")
    public AjaxRs getIdentifications(@RequestBody String login) {

        login = login.replace("\"", "");

        Identifications identifications = repository.findIdentificationsByAccess_Login(login).getIdentifications();

        if (identifications != null) {
            log.debug("returning Identifications for login {}: {}", login, identifications);
            return responseSuccess(RETURN_IDENTIFICATIONS, identifications);
        } else {
            log.error("Can not return Identifications for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }
}
