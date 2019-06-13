package ru.strcss.projects.moneycalc.moneycalcmigrator.api;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.util.List;

public interface ServerConnectorI {
    String login(Access access);

    SpendingSectionsSearchRs saveSpendingSection(String token, SpendingSection spendingSection);

    boolean saveTransactions(String token, List<Transaction> transactionsToAdd, String login);

    SpendingSectionsSearchRs getSectionsList(String token);
}
