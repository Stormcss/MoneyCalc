package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SettingsLegacy;

@Data
@Deprecated
@AllArgsConstructor
public class SettingsLegacyUpdateContainer {
    private SettingsLegacy settings;
}

