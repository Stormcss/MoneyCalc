package ru.strcss.projects.moneycalc.enitities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpendingSectionLegacy implements Serializable {
    /**
     * id of SpendingSection - spending section id in DB Table
     */
    private Integer id;

    /**
     * Person Id - used for linking Person with current SpendingSection in DB
     */
    private Integer personId;

    /**
     * sectionId of SpendingSection - inner id of spending section for current Person (e.g. [0, 1, 2])
     */
    private Integer sectionId;

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
}
