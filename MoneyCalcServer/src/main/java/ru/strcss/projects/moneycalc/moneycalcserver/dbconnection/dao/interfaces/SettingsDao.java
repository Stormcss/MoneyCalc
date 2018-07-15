package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

import ru.strcss.projects.moneycalc.enitities.Settings;

public interface SettingsDao {

    int saveSettings(Settings settings);

    Settings updateSettings(Settings settings);

    Settings getSettingsById(Integer id);

}