package ru.strcss.projects.moneycalc.moneycalcserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Stormcss
 * Date: 01.11.2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpendingSectionFilter {
    boolean withNonAdded;
    boolean withRemoved;
    boolean withRemovedOnly;
}
