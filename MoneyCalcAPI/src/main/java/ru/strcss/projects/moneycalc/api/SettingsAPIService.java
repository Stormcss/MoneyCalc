package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.List;

public interface SettingsAPIService {

    ResponseEntity<MoneyCalcRs<Settings>> updateSettings(SettingsUpdateContainer updateContainer);

    ResponseEntity<MoneyCalcRs<Settings>> getSettings();

    ResponseEntity<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(boolean withNonAdded, boolean withRemoved, boolean withRemovedOnly);

    ResponseEntity<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(SpendingSectionAddContainer addContainer);

    ResponseEntity<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(SpendingSectionUpdateContainer sectionUpdateContainer);

    ResponseEntity<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(SpendingSectionDeleteContainer sectionDeleteContainer);
}
