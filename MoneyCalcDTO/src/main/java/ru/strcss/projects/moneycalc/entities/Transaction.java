package ru.strcss.projects.moneycalc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Validationable, Serializable {

    /**
     * id of Transaction - transaction id in DB Table
     */
    private Long id;

    /**
     * Person Id - used for linking Person with current Transaction in DB
     */
    private Long userId;

    private Integer sectionId;

    private LocalDate date;

    // TODO: 18.11.2018 should be BigDecimal
    private Integer sum;
    private String currency;
    private String title;
    private String description;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (sum == null || sum == 0) reasons.add("Transaction sum can not be empty or 0!");
        if (sectionId == null || sectionId < 0) reasons.add("sectionId can not be empty or < 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
