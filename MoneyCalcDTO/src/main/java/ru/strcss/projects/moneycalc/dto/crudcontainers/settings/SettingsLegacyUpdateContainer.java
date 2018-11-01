package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.entities.SettingsLegacy;

@Data
@AllArgsConstructor
public class SettingsLegacyUpdateContainer {
    private SettingsLegacy settings;
}

