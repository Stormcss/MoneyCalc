package ru.strcss.projects.moneycalcmigrator.api;

import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.List;

public interface ServerConnectorI {
    String login(Access access);

    List<SpendingSection> saveSpendingSection(String token, SpendingSection spendingSection);

    Status saveTransactions(String token, List<Transaction> transactionsToAdd, String login);

    List<SpendingSection> getSectionsList(String token);
}
