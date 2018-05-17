package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Document
@Data
public class Transaction implements Validationable, Serializable {

    private String _id;
    private String date;
    private Integer sum;
    private String currency;
    private String description;
    private Integer sectionID;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (sum == null || sum == 0) reasons.add("Transaction sum can not be empty or 0!");
        if (sectionID == null || sectionID < 0) reasons.add("SectionID can not be empty or < 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}


