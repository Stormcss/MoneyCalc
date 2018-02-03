package ru.strcss.projects.moneycalcserver.controllers.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.controllers.dto.Credentials;

public interface RegisterAPIService {

    @PostMapping(value = "/registerPerson")
    AjaxRs registerPerson(@RequestBody Credentials credentials);

}
