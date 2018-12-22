package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;

public interface SettingsService {

    Settings updateSettings(String login, Settings settings) throws Exception;

    Settings getSettings(String login) throws Exception;
}
