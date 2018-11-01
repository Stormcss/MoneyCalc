package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.entities.Settings;

public interface SettingsService {

    Settings updateSettings(String login, Settings settings);

    Settings getSettings(String login);
}
