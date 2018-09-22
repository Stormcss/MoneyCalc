package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.enitities.SettingsLegacy;

@Data
@AllArgsConstructor
public class SettingsLegacyUpdateContainer {
    private SettingsLegacy settings;
}

