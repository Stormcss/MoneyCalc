package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.StatisticsAPIService;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SettingsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.TransactionsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.STATISTICS_RETURNED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Slf4j
@RestController
@RequestMapping("/api/stats/summaryBySection")
public class StatisticsController extends AbstractController implements StatisticsAPIService {

    private TransactionsDao transactionsDao;
    private SpendingSectionDao sectionsDao;
    private SettingsDao settingsDao;
    private PersonDao personDao;
    private SummaryStatisticsHandler statisticsHandler;

    public StatisticsController(TransactionsDao transactionsDao, SpendingSectionDao sectionsDao, PersonDao personDao,
                                SettingsDao settingsDao, SummaryStatisticsHandler statisticsHandler) {
        this.transactionsDao = transactionsDao;
        this.sectionsDao = sectionsDao;
        this.personDao = personDao;
        this.settingsDao = settingsDao;
        this.statisticsHandler = statisticsHandler;
    }

    // TODO: 26.08.2018 test me
    /**
     * Get finance summary for all active sections
     */
    @Override
    @GetMapping(value = "/get")
    public ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer personId = personDao.getPersonIdByLogin(login);

        Settings settings = settingsDao.getSettingsById(personId);
        List<SpendingSection> spendingSections = sectionsDao.getActiveSpendingSectionsByPersonId(personId);
        List<Integer> sectionIds = spendingSections.stream().map(SpendingSection::getSectionId).collect(Collectors.toList());

        LocalDate dateFrom = settings.getPeriodFrom();
//        LocalDate dateFrom = string2LocalDate(settings.getPeriodFrom());
        LocalDate dateTo = settings.getPeriodTo();
//        LocalDate dateTo = string2LocalDate(settings.getPeriodTo());
        List<Transaction> transactionsList = transactionsDao.getTransactionsByPersonId(personId, dateFrom, dateTo, sectionIds);

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(dateFrom)
                .rangeTo(dateTo)
                .sections(sectionIds)
                .transactions(transactionsList)
                .spendingSections(spendingSections)
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        List<FinanceSummaryBySection> financeSummaryResult = statisticsHandler.calculateSummaryStatisticsBySections(calculationContainer);

        log.debug("Returned List of FinanceSummaryBySections for login \"{}\" : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }

    @Override
    @PostMapping(value = "/getFiltered")
    public ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@RequestBody FinanceSummaryGetContainer getContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<FinanceSummaryBySection>> requestValidation = new Validator(getContainer, "Getting Finance Summary")
                .addValidation(() -> isDateSequenceValid(getContainer.getRangeFrom(), getContainer.getRangeTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Integer personId = personDao.getPersonIdByLogin(login);

        List<Transaction> transactions = transactionsDao.getTransactionsByPersonId(personId, getContainer.getRangeFrom(),
                getContainer.getRangeTo(), getContainer.getSectionIds());

        //оставляю только те секции клиента для которых мне нужна статистика
        List<SpendingSection> spendingSections = sectionsDao.getSpendingSectionsByPersonId(personId).stream()
                .filter(section -> getContainer.getSectionIds().stream().anyMatch(id -> id.equals(section.getSectionId())))
                .collect(Collectors.toList());

        if (spendingSections.size() != getContainer.getSectionIds().size()) {
            log.error("List of required IDs is not equal with list filtered Person's list for login: \"{}\"", login);
            return responseError("List of required IDs is not equal with list filtered Person's list");
        }

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(getContainer.getRangeFrom())
                .rangeTo((getContainer.getRangeTo()))
                .sections(getContainer.getSectionIds())
                .transactions(transactions)
                .spendingSections(spendingSections)
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        List<FinanceSummaryBySection> financeSummaryResult = statisticsHandler.calculateSummaryStatisticsBySections(calculationContainer);

        log.debug("Returned List of FinanceSummaryBySection for login \"{}\" : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }
}
