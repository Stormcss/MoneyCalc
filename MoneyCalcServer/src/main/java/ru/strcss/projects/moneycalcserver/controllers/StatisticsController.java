package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.StatisticsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalcserver.dbconnection.TransactionsDBConnection;
import ru.strcss.projects.moneycalcserver.handlers.SummaryStatisticsHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/statistics/financeSummary")
public class StatisticsController extends AbstractController implements StatisticsAPIService {

    private TransactionsDBConnection transactionsDBConnection;
    private SettingsDBConnection settingsDBConnection;
    private SummaryStatisticsHandler statisticsHandler;

    @Autowired
    public StatisticsController(TransactionsDBConnection transactionsDBConnection, SettingsDBConnection settingsDBConnection, SummaryStatisticsHandler statisticsHandler) {
        this.transactionsDBConnection = transactionsDBConnection;
        this.settingsDBConnection = settingsDBConnection;
        this.statisticsHandler = statisticsHandler;
    }

    @Override
    @PostMapping(value = "/getFinanceSummaryBySection")
    public AjaxRs<List<FinanceSummaryBySection>> getFinanceSummaryBySection(@RequestBody FinanceSummaryGetContainer getContainer) {

        log.error("FinanceSummaryGetContainer: {}", getContainer);

        ValidationResult validationResult = getContainer.isValid();

        if (!validationResult.isValidated()) {
            log.error("getting FinanceSummaryBySection has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        Person person = settingsDBConnection.getSettings(getContainer.getLogin());

        if (person == null) {
            log.error("Person with login {} is not found!", getContainer.getLogin());
            return responseError("Person with login " + getContainer.getLogin() + " is not found!");
        }

        List<Transaction> transactions = transactionsDBConnection.getTransactions(new TransactionsSearchContainer(getContainer.getLogin(),
                getContainer.getRangeFrom(), getContainer.getRangeTo(), getContainer.getSectionIDs()));

        //оставляю только те секции клиента для которых мне нужна статистика
        List<SpendingSection> spendingSections = person.getSettings().getSections().stream()
                .filter(section -> getContainer.getSectionIDs().stream().anyMatch(id -> id.equals(section.getId())))
                .collect(Collectors.toList());

        if (spendingSections.size() != getContainer.getSectionIDs().size()) {
            log.error("List of required IDs is not equal with list filtered Person's list");
            return responseError("List of required IDs is not equal with list filtered Person's list");
        }

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(formatDateFromString(getContainer.getRangeFrom()))
                .rangeTo(formatDateFromString(getContainer.getRangeTo()))
                .sections(getContainer.getSectionIDs())
                .transactions(transactions)
                .spendingSections(spendingSections)
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        List<FinanceSummaryBySection> financeSummaryResult = statisticsHandler.calculateSummaryStatisticsBySections(calculationContainer);

        log.debug("Returned List of FinanceSummaryBySection for login: {} : {}", getContainer.getLogin(), financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }
}
