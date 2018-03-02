package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;

import java.util.List;

@Data
@AllArgsConstructor
public class SettingsUpdateContainer extends AbstractContainer {

    private String login;
    private Settings settings;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login));
        if (settings == null) reasons.add("settings are empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}

