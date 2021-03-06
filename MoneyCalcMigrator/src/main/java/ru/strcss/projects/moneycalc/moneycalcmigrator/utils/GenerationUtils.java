package ru.strcss.projects.moneycalc.moneycalcmigrator.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcmigrator.properties.MigrationProperties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenerationUtils {
    /**
     * Generate SpendingSection with required fields
     *
     * @param sectionName - sectionName name
     * @return SpendingSection object
     */
    public static SpendingSection generateSpendingSection(String sectionName) {
        SpendingSection spendingSection = new SpendingSection();
        spendingSection.setName(sectionName);
        spendingSection.setBudget(5000L);
        return spendingSection;
    }

    public static Access generateAccess(MigrationProperties properties) {
        return new Access(properties.getLogin(), properties.getPassword(), properties.getEmail());
    }
}
