package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class TransactionAddContainer extends AbstractContainer {

    private Transaction transaction;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (transaction == null) reasons.add("transaction is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}

