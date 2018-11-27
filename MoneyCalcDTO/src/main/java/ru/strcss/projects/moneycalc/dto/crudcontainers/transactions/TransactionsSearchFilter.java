package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.math.BigDecimal;
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
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsSearchFilter extends AbstractContainer {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private List<Integer> requiredSections;
    private String title;
    private String description;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (dateFrom == null) reasons.add("dateFrom is empty");
        if (dateTo == null) reasons.add("dateTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}

