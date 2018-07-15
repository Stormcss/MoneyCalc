package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SettingsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SettingsService;

@Service
public class SettingsServiceImpl implements SettingsService {

    private SettingsDao settingsDao;

    public SettingsServiceImpl(SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    @Override
    @Transactional
    public int saveSettings(Settings settings) {
        return settingsDao.saveSettings(settings);
    }

    @Override
    @Transactional
    public Settings updateSettings(Settings settings) {
        return settingsDao.updateSettings(settings);
    }

    @Override
    @Transactional
    public Settings getSettingsById(Integer id) {
        return settingsDao.getSettingsById(id);
    }
}
