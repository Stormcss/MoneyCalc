package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
@Builder
public class Settings {

    @Id
    private String _id;
    private String periodFrom;
    private String periodTo;
    private List<SettingsSection> sections;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (_id.isEmpty()) reasons.add("id is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}
