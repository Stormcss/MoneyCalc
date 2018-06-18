package ru.strcss.projects.moneycalcmigrator.utils;

import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcmigrator.properties.MigrationProperties;

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

    public static Access generateAccess(MigrationProperties properties) {
        return Access.builder()
                .login(properties.getLogin())
                .password(properties.getPassword())
                .email(properties.getEmail())
                .build();
    }

}
