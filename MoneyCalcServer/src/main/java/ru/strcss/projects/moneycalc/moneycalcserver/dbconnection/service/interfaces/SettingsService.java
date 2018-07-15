package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.Settings;

public interface SettingsService {

    int saveSettings(Settings settings);

    Settings updateSettings(Settings settings);

    Settings getSettingsById(Integer id);
}
