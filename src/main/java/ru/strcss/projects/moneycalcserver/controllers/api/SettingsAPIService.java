package ru.strcss.projects.moneycalcserver.controllers.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.Settings;

public interface SettingsAPIService {

    @PostMapping(value = "/saveSettings")
    AjaxRs saveSettings(@RequestBody Settings settings);

    @PostMapping(value = "/getSettings")
    AjaxRs getSettings(@RequestBody String login);
}
