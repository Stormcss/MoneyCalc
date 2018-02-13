package ru.strcss.projects.moneycalcserver.controllers;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

public class StatisticsControllerTest extends AbstractControllerTest{

    @Test
    public void testGetFinanceSummaryBySection() {
        String login = savePersonGetLogin(service);

        int numOfAddedTransactionsPerSection = 10;
        int numOfSections = 3;

        //Adding new Transactions
        List<Transaction> addedTransactions = new ArrayList<>();
        for (int i = 0; i < numOfSections; i++) {
            // FIXME: 11.02.2018 I suppose it could be done better
            int sectionID = i;
            addedTransactions.addAll(IntStream.range(0, numOfAddedTransactionsPerSection)
                    .mapToObj(s -> sendRequest(service.addTransaction(new TransactionAddContainer(generateTransaction(sectionID), login))).body())
                    .filter(Objects::nonNull)
                    .map(AjaxRs::getPayload)
                    .collect(Collectors.toList()));
        }
        // TODO: 13.02.2018 finish Test
    }
}