package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenerationUtils {

    public static LocalDate generateDatePlus(TemporalUnit unit, int count) {
        return LocalDate.now().plus(count, unit);
    }

    public static LocalDate generateDateMinus(TemporalUnit unit, int count) {
        return LocalDate.now().minus(count, unit);
    }

    public static LocalDate currentDate() {
        return LocalDate.now();
    }

    public static Settings generateRegisteringSettings() {
        return new Settings(currentDate(), generateDatePlus(ChronoUnit.MONTHS, 1));
    }

    public static SpendingSection generateRegisteringSpendingSection(String name, Long budget, Integer logoId) {
        SpendingSection spendingSection = new SpendingSection();
        spendingSection.setName(name);
        spendingSection.setBudget(budget);
        spendingSection.setLogoId(logoId);
        return spendingSection;
    }
}
