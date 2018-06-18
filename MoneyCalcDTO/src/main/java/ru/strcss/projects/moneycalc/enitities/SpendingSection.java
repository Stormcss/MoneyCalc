package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class SpendingSection implements Serializable {
    /**
     * id of SpendingSection - unique id which is used to identify Section while calculating Statistics
     */
    private Integer id;
    /**
     * Name of SpendingSection which is seen in UI
     */
    private String name;
    /**
     * if Section is active in UI. It is allowed to disable unactual sections without having to delete it
     */
    private Boolean isAdded;

    /**
     * if Section is removed. Flag allows to disable ("remove" it for the user) required section without having
     * to physically remove it from database
     */
    private Boolean isRemoved;

    /**
     * Estimated budget for section. Used for Statistics calculation
     */
    private Integer budget;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (name == null || name.isEmpty()) reasons.add("name is empty");
        if (budget == null) reasons.add("budget is empty");
        if (budget != null && budget <= 0) reasons.add("budget must be >= 0");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
