package ru.strcss.projects.moneycalcmigrator.utils;

import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcmigrator.dto.ConfigContainer;

public class GenerationUtils {
    /**
     * Generate SpendingSection with required fields
     *
     * @param sectionName - sectionName name
     * @return SpendingSection object
     */
    public static SpendingSection generateSpendingSection(String sectionName) {
        return SpendingSection.builder()
                .budget(5000)
                .isAdded(true)
                .name(sectionName)
                .build();
    }

    public static Access generateAccess(ConfigContainer config) {
        return Access.builder()
                .login(config.getLogin())
                .password(config.getPassword())
                .email(config.getEmail())
                .build();
    }

}
