package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.entities.Settings;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SettingsUpdateContainer extends AbstractContainer {

    private Settings settings;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (settings == null) reasons.add("settings are empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}

