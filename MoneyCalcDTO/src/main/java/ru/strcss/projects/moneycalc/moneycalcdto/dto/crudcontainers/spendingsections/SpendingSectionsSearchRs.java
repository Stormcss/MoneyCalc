package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stormcss
 * Date: 23.04.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendingSectionsSearchRs {
    private Integer count;
    private List<SpendingSection> items;

    public static SpendingSectionsSearchRs generateEmpty() {
        return new SpendingSectionsSearchRs(0, new ArrayList<>());
    }
}
