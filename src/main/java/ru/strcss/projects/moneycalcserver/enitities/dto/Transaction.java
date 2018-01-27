package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String _id;

    //should it be object containing login with month/year?
    private String identifier;

    private String date;
    private int sum;
    private String currency;
    private String description;
    private int sectionID;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (identifier.isEmpty()) reasons.add("identifier is empty");
        if (sum == 0) reasons.add("Transaction sum can not be 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}


