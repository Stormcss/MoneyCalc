package ru.strcss.projects.moneycalc.moneycalcdto;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummaryFilterLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainerLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilterLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.AccessLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.IdentificationsLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SettingsLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSectionLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.TransactionLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;

/**
 * Created by Stormcss
 * Date: 12.12.2018
 */
public class PojoTest {
    @Test
    public void shouldHavePojoStructureAndBehavior() {
        PojoTestUtils.validateAccessors(Access.class);
        PojoTestUtils.validateAccessors(AccessLegacy.class);
        PojoTestUtils.validateAccessors(SummaryBySection.class);
        PojoTestUtils.validateAccessors(Identifications.class);
        PojoTestUtils.validateAccessors(IdentificationsLegacy.class);
        PojoTestUtils.validateAccessors(Person.class);
        PojoTestUtils.validateAccessors(Settings.class);
        PojoTestUtils.validateAccessors(SettingsLegacy.class);
        PojoTestUtils.validateAccessors(SpendingSection.class);
        PojoTestUtils.validateAccessors(SpendingSectionLegacy.class);
        PojoTestUtils.validateAccessors(Transaction.class);
        PojoTestUtils.validateAccessors(TransactionLegacy.class);

        PojoTestUtils.validateAccessors(SpendingSectionUpdateContainer.class);
        PojoTestUtils.validateAccessors(StatisticsFilter.class);
        PojoTestUtils.validateAccessors(FinanceSummaryFilterLegacy.class);
        PojoTestUtils.validateAccessors(TransactionsSearchFilterLegacy.class);
        PojoTestUtils.validateAccessors(TransactionsSearchFilter.class);
        PojoTestUtils.validateAccessors(TransactionUpdateContainer.class);
        PojoTestUtils.validateAccessors(TransactionUpdateContainerLegacy.class);
        PojoTestUtils.validateAccessors(Credentials.class);
        PojoTestUtils.validateAccessors(FinanceSummaryCalculationContainer.class);
        PojoTestUtils.validateAccessors(SpendingSectionsSearchRs.class);
        PojoTestUtils.validateAccessors(TransactionsSearchRs.class);
        PojoTestUtils.validateAccessors(ValidationResult.class);
    }
}
