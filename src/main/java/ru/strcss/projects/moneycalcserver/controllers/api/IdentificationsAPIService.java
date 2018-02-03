package ru.strcss.projects.moneycalcserver.controllers.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.Identifications;

public interface IdentificationsAPIService {

    @PostMapping(value = "/saveIdentifications")
    AjaxRs saveIdentifications(@RequestBody Identifications identifications);

    @PostMapping(value = "/getIdentifications")
    AjaxRs getIdentifications(@RequestBody String login);
}
