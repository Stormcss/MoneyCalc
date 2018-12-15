package ru.strcss.projects.moneycalc.moneycalcdto;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummaryGetContainerLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionAddContainerLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainerLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchContainerLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.AccessLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.IdentificationsLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SettingsLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSectionLegacy;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.TransactionLegacy;

/**
 * Created by Stormcss
 * Date: 12.12.2018
 */
public class PojoTest {
    @Test
    void shouldHavePojoStructureAndBehavior() {
        PojoTestUtils.validateAccessors(Access.class);
        PojoTestUtils.validateAccessors(AccessLegacy.class);
        PojoTestUtils.validateAccessors(FinanceSummaryBySection.class);
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
        PojoTestUtils.validateAccessors(FinanceSummaryFilter.class);
        PojoTestUtils.validateAccessors(FinanceSummaryGetContainerLegacy.class);
        PojoTestUtils.validateAccessors(TransactionAddContainer.class);
        PojoTestUtils.validateAccessors(TransactionAddContainerLegacy.class);
        PojoTestUtils.validateAccessors(TransactionsSearchContainerLegacy.class);
        PojoTestUtils.validateAccessors(TransactionsSearchFilter.class);
        PojoTestUtils.validateAccessors(TransactionUpdateContainer.class);
        PojoTestUtils.validateAccessors(TransactionUpdateContainerLegacy.class);
        PojoTestUtils.validateAccessors(Credentials.class);
        PojoTestUtils.validateAccessors(FinanceSummaryCalculationContainer.class);
        PojoTestUtils.validateAccessors(MoneyCalcRs.class);
        PojoTestUtils.validateAccessors(ValidationResult.class);
    }
}
