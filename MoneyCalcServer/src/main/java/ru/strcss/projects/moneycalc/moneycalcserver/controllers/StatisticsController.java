package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.StatisticsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.TransactionsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateCorrect;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Slf4j
@RestController
@RequestMapping("/api/statistics/financeSummary")
public class StatisticsController extends AbstractController implements StatisticsAPIService {

    private TransactionsDBConnection transactionsDBConnection;
    private SettingsDBConnection settingsDBConnection;
    private SummaryStatisticsHandler statisticsHandler;

    public StatisticsController(TransactionsDBConnection transactionsDBConnection, SettingsDBConnection settingsDBConnection, SummaryStatisticsHandler statisticsHandler) {
        this.transactionsDBConnection = transactionsDBConnection;
        this.settingsDBConnection = settingsDBConnection;
        this.statisticsHandler = statisticsHandler;
    }

    @Override
    @PostMapping(value = "/getFinanceSummaryBySection")
    public AjaxRs<List<FinanceSummaryBySection>> getFinanceSummaryBySection(@RequestBody FinanceSummaryGetContainer getContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<FinanceSummaryBySection>> requestValidation = new Validator(getContainer, "Getting Finance Summary")
//                .addValidation(() -> repository.existsByAccess_Login(formatLogin(login)),
//                        () -> fillLog(NO_PERSON_EXIST, login))
                .addValidation(() -> isDateCorrect(getContainer.getRangeFrom()),
                        () -> fillLog(DATE_INCORRECT, getContainer.getRangeFrom()))
                .addValidation(() -> isDateCorrect(getContainer.getRangeTo()),
                        () -> fillLog(DATE_INCORRECT, getContainer.getRangeTo()))
                .addValidation(() -> isDateSequenceValid(getContainer.getRangeFrom(), getContainer.getRangeTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        List<Transaction> transactions = transactionsDBConnection.getTransactions(login, new TransactionsSearchContainer(
                getContainer.getRangeFrom(), getContainer.getRangeTo(), getContainer.getSectionIDs()));

        //оставляю только те секции клиента для которых мне нужна статистика
        List<SpendingSection> spendingSections = settingsDBConnection.getSpendingSectionList(login).stream()
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

        log.debug("Returned List of FinanceSummaryBySection for login: {} : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }
}
