package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Document
@Builder
public class Settings {

//    @Id
    private String _id;
    private String periodFrom;
    private String periodTo;
    private List<SpendingSection> sections;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (_id.isEmpty()) reasons.add("id is empty");
        if (sections.stream().map(SpendingSection::getID).distinct().collect(Collectors.toList()).size() != sections.size()) reasons.add("SpendingSections have duplicates!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}
