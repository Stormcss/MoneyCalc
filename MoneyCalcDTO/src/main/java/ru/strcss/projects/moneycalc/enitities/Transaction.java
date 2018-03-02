package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Builder
@Document
@Data
public class Transaction implements Validationable/*, IVisitable*/ {

    private String _id;
    private String date;
    private int sum;
    private String currency;
    private String description;
    private Integer sectionID;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (sum == 0) reasons.add("Transaction sum can not be 0!");
        if (sectionID < 0) reasons.add("SectionID can not be < 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

//    @Override
//    public String accept(Visitor visitor) {
//        return visitor.visitTransaction(this);
//    }
}


