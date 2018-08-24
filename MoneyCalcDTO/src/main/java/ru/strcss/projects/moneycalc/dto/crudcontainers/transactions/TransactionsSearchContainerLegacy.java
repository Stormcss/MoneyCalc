package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Container encapsulating searching parameters for Transactions
 *
 * If list of requiredSections is empty then all found transactions satisfying other parameters will be returned.
 * Otherwise only transactions with requested sectionID will be returned
 *
 * login - Person's login with searched transactions
 * rangeFrom - starting Date of range from which transactions should be received
 * rangeTo - ending Date of range from which transactions should be received
 *
 * LEGACY EDITION
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsSearchContainerLegacy{
    private String rangeFrom;
    private String rangeTo;
    private List<Integer> requiredSections;
}

