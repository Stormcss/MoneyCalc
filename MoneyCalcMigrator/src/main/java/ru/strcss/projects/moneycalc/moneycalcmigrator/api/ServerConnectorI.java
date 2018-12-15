package ru.strcss.projects.moneycalc.moneycalcmigrator.api;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.util.List;

public interface ServerConnectorI {
    String login(Access access);

    List<SpendingSection> saveSpendingSection(String token, SpendingSection spendingSection);

    Status saveTransactions(String token, List<Transaction> transactionsToAdd, String login);

    List<SpendingSection> getSectionsList(String token);
}
