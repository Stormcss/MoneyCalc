package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils;
import ru.strcss.projects.moneycalcserver.controllers.entities.AbstractApiController;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.FinanceStatistics;

@Slf4j
@RestController
@RequestMapping("/api/financeStatistics/")
public class ApiControllerFinanceStatistics extends AbstractApiController {

    @PostMapping(value = "/getFinanceStats")
    public AjaxRs getFinanceStats(@RequestBody String login) {

        // TODO: 16.01.2018 return FinanceStatistics from DB, not whole Person

        login = login.replace("\"","");

        FinanceStatistics financeStatistics = repository.findPersonByAccess_Login(login).getFinanceStatistics();

        if (financeStatistics != null) {
            log.debug("returning FinanceStatistics for login {}: {}", login, financeStatistics);
            return ControllerUtils.responseSuccess(RETURN_STATISTICS, financeStatistics);
        } else {
            log.error("Can not return FinanceStatistics for login {} - no Person found", login);
            return ControllerUtils.responseError(NO_PERSON_EXIST);
        }
    }
}
