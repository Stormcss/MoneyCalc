package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.enitities.Settings;

public interface SettingsAPIService {

    AjaxRs<Settings> saveSettings(Settings settings);

    AjaxRs<Settings> getSettings(String login);
}
