package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.entities.Settings;

public interface SettingsService {

    Settings updateSettings(String login, Settings settings);

    Settings getSettings(String login);
}
