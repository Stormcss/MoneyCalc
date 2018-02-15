package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class StatisticsControllerTest extends AbstractControllerTest {

    @Test
    public void testGetFinanceSummaryBySection() {
        Integer numOfAddedTransactionsPerSection = 10;
        int numOfSections = 3;
        Integer budgetPerSection = 5000;

        String login = savePersonGetLogin(service);

        //Adding Sections if required
        checkPersonsSections(numOfSections, login, budgetPerSection);

        //Adding new Transactions
//        List<Transaction> addedTransactions = new ArrayList<>();

        Map<Integer, Integer> IdSumMap = new HashMap<>();

        //check is done by current date
        for (int i = 0; i < numOfSections; i++) {
            // FIXME: 11.02.2018 I suppose it could be done better
            int sectionID = i;
            List<Transaction> transactionsBySection = IntStream.range(0, numOfAddedTransactionsPerSection)
                    .mapToObj(s -> sendRequest(service.addTransaction(new TransactionAddContainer(generateTransaction(sectionID), login))).body())
                    .filter(Objects::nonNull)
                    .map(AjaxRs::getPayload)
                    .collect(Collectors.toList());

            IdSumMap.put(sectionID, transactionsBySection.stream().map(Transaction::getSum).mapToInt(Integer::intValue).sum());

//            addedTransactions.addAll(transactionsBySection);
        }
        assertEquals(IdSumMap.keySet().size(), numOfSections, "Map has wrong size!");

        //Request Statistics
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, formatDateToString(LocalDate.now()),
                formatDateToString(LocalDate.now()), new ArrayList<>(IdSumMap.keySet()));

        log.error("getContainer: {}", getContainer);

        AjaxRs<List<FinanceSummaryBySection>> responseGetStats
                = sendRequest(service.getFinanceSummaryBySection(getContainer)).body();
        assertEquals(responseGetStats.getStatus(), Status.SUCCESS, responseGetStats.getMessage());


        for (FinanceSummaryBySection summary : responseGetStats.getPayload()) {
            assertEquals(summary.getMoneySpendAll(), IdSumMap.get(summary.getSectionID()), "MoneySpendAll do not match!");
            assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - IdSumMap.get(summary.getSectionID()), "getMoneyLeftAll do not match!");
        }

    }

    /**
     * Add Person's Sections if required
     *
     * @param numOfSections - required number of Person's sections
     * @param login         - Person's login
     */
    private void checkPersonsSections(int numOfSections, String login, int budget) {
        Settings personSettings = sendRequest(service.getSettings(login)).body().getPayload();

        if (numOfSections > personSettings.getSections().size()) {
            for (int i = 0; i < numOfSections - personSettings.getSections().size(); i++) {
                personSettings.getSections().add(generateSpendingSection(budget, personSettings.getSections().size() + i));
            }
            for (SpendingSection section : personSettings.getSections()){
                if (section.getBudget() != budget) section.setBudget(budget);
            }

            assertTrue(personSettings.getSections().stream().allMatch(spendingSection -> spendingSection.getBudget() == budget));

            AjaxRs<Settings> updateSettingsResponse = sendRequest(service.saveSettings(personSettings)).body();
            assertEquals(updateSettingsResponse.getStatus(), Status.SUCCESS, updateSettingsResponse.getMessage());
        }
    }
}