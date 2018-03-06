package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.List;

public interface SettingsAPIService {

    AjaxRs<Settings> saveSettings(SettingsUpdateContainer updateContainer);

    AjaxRs<Settings> getSettings();

    AjaxRs<List<SpendingSection>> getSpendingSections();

    AjaxRs<List<SpendingSection>> addSpendingSection(SpendingSectionAddContainer addContainer);

    AjaxRs<List<SpendingSection>> updateSpendingSection(SpendingSectionUpdateContainer sectionUpdateContainer);

    AjaxRs<List<SpendingSection>> deleteSpendingSection(SpendingSectionDeleteContainer sectionDeleteContainer);
}
