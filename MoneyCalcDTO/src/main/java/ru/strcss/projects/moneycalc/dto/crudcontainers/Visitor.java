package ru.strcss.projects.moneycalc.dto.crudcontainers;

import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.Transaction;

public interface Visitor {
    String visitIdentifications(Identifications identifications);
    String visitSettings(Settings settings);
    String visitTransaction(Transaction transaction);
}