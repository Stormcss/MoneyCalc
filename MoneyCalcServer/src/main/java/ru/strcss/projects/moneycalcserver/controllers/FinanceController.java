//package ru.strcss.projects.moneycalcserver.controllers;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;
//import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
//import ru.strcss.projects.moneycalcserver.enitities.dto.Finance;
//import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
//import ru.strcss.projects.moneycalcserver.enitities.dto.Settings;
//
//import static org.springframework.data.mongodb.core.query.Criteria.where;
//import static org.springframework.data.mongodb.core.query.Query.query;
//import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.responseError;
//import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.responseSuccess;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/finance/")
//public class FinanceController extends AbstractController {
//
//    /**
//     * Save FinanceSummary object using user's login stored inside
//     *
//     * @param finance - income Settings object
//     * @return response object
//     */
//    @PostMapping(value = "/saveFinanceSummary")
//    public AjaxRs saveFinance(@RequestBody Finance finance) {
//
//        Person person = repository.findPersonByAccess_Login(finance.get_id());
//
//        if (person == null) {
//            log.error("Person with login {} is not found!", settings.get_id());
//            return responseError("Person with login "+ settings.get_id() +" is not found!");
//        }
//
//        if (mongoOperations.findById(finance.get_id(), Finance.class) != null) {
//            // Update
//            DBObject dbObject = new BasicDBObject();
//            mongoOperations.getConverter().write(identifications, dbObject);
//            mongoOperations.upsert(query(where("_id").is(identifications.get_id())), Update.fromDBObject(dbObject, "_id"), Identifications.class);
//            return responseSuccess(SAVE_IDENTIFICATIONS, identifications);
//        } else {
//            //save new
//            mongoOperations.insert(identifications, "Identifications");
//            return responseSuccess(SAVE_IDENTIFICATIONS, identifications);
//        }
//
//        return responseSuccess(RETURN_SETTINGS, settings);
//    }
//
//    /**
//     * Get Setting object using user's login
//     *
//     * @param login - user's login
//     * @return response object
//     */
//    @PostMapping(value = "/getSettings")
//    public AjaxRs getSettings(@RequestBody String login) {
//
//        // TODO: 14.01.2018 return Settings from DB, not whole Person
//
//        login = login.replace("\"", "");
//
//        Person person = repository.findPersonByAccess_Login(login);
//
//        if (person == null) {
//            log.error("Person with login {} is not found!", login);
//            return responseError("Person with login "+ login +" is not found!");
//        }
//
//        Settings settings = person.getSettings();
//
//        if (settings != null) {
//            log.debug("returning PersonalSettings for login {}: {}", login, settings);
//            return responseSuccess(RETURN_SETTINGS, settings);
//        } else {
//            log.error("Can not return PersonalSettings for login {} - no Person found", login);
//            return responseError(NO_PERSON_EXIST);
//        }
//    }
//}
