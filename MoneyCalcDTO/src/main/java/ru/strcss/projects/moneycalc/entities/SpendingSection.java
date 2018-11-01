package ru.strcss.projects.moneycalc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"SpendingSection\"")
public class SpendingSection implements Validationable, Serializable {
    /**
     * id of SpendingSection - spending section id in DB Table
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Person Id - used for linking Person with current SpendingSection in DB
     */
    @Column(name = "\"personId\"")
    private Integer personId;

    /**
     * sectionId of SpendingSection - inner id of spending section for current Person (e.g. [0, 1, 2])
     */
    @Column(name = "\"sectionId\"")
    private Integer sectionId;

    /**
     * logoId of SpendingSection - logo id which is showed in UI (e.g. [0, 1, 2])
     */
    @Column(name = "\"logoId\"")
    private Integer logoId;

    /**
     * Name of SpendingSection which is seen in UI
     */
    private String name;

    /**
     * if Section is active in UI. It is allowed to disable unactual sections without having to delete it
     */
    @Column(name = "\"isAdded\"")
    private Boolean isAdded;

    /**
     * if Section is removed. Flag allows to disable ("remove" it for the user) required section without having
     * to physically remove it from database
     */
    @Column(name = "\"isRemoved\"")
    private Boolean isRemoved;

    /**
     * Estimated budget for section. Used for Statistics calculation
     */
    private Integer budget;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (name == null || name.isEmpty()) reasons.add("name is empty");
        if (budget == null) reasons.add("budget is empty");
        if (budget != null && budget <= 0) reasons.add("budget must be >= 0");
        if (isRemoved != null) reasons.add("isRemoved can not be set as income parameter");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    public boolean isAnyFieldSet() {
        return (name != null && !name.isEmpty()) || budget != null || isAdded != null;
    }

}
