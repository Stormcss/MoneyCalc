package ru.strcss.projects.moneycalc.moneycalcserver.services;

import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SettingsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;

@Service
public class SettingsServiceImpl implements SettingsService {

    private SettingsMapper settingsMapper;

    public SettingsServiceImpl(SettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    @Override
    public Settings updateSettings(String login, Settings settings) {
        settingsMapper.updateSettings(login, settings);
        return settingsMapper.getSettings(login);
    }

    @Override
    public Settings getSettings(String login) {
        return settingsMapper.getSettings(login);
    }
}
