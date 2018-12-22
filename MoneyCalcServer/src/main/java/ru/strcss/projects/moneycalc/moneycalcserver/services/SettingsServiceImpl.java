package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.TimerType;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SettingsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;

@Service
@AllArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private SettingsMapper settingsMapper;
    private MetricsService metricsService;

    @Override
    public Settings updateSettings(String login, Settings settings) throws Exception {
        metricsService.getTimersStorage().get(TimerType.SETTINGS_UPDATE_TIMER).record(() ->
                settingsMapper.updateSettings(login, settings)
        );
        return metricsService.getTimersStorage().get(TimerType.SETTINGS_GET_TIMER)
                .recordCallable(() -> settingsMapper.getSettings(login));
    }

    @Override
    public Settings getSettings(String login) throws Exception {
        return metricsService.getTimersStorage().get(TimerType.SETTINGS_GET_TIMER)
                .recordCallable(() -> settingsMapper.getSettings(login));
    }
}
