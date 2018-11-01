package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.entities.Settings;

public interface SettingsAPIService {
    ResponseEntity<MoneyCalcRs<Settings>> updateSettings(SettingsUpdateContainer updateContainer);

    ResponseEntity<MoneyCalcRs<Settings>> getSettings();
}
