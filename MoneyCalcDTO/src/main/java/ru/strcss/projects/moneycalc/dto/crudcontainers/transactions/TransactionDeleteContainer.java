package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class TransactionDeleteContainer extends AbstractContainer {

    private Integer id;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (id == null) reasons.add("id is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
