package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
//@Document(collection = "Identifications")
@Document
public class Identifications {

    @Id
    private String _id;
    private String name;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (_id.isEmpty()) reasons.add("_id is empty");
        if (name.isEmpty()) reasons.add("Name is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
