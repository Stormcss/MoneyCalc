package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
@Builder
public class Settings implements Validationable/*, IVisitable*/ {

    //    @Id
    private String login;
    private String periodFrom;
    private String periodTo;
    private List<SpendingSection> sections;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login == null || login.isEmpty()) reasons.add("login is empty");
//        if (sections == null) reasons.add("sections is null!");
//        if (sections.stream().map(SpendingSection::getId).distinct().collect(Collectors.toList()).size() != sections.size()) reasons.add("SpendingSections have duplicates!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

//    @Override
//    public String accept(Visitor visitor) {
//        return visitor.visitSettings(this);
//    }
}
