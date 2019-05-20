package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.BaseStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsContainer<E> {
    private Long count;
    private BaseStatistics stats;
    private List<E> items;

    public static ItemsContainer buildEmpty() {
        return new ItemsContainer<>(0L, BaseStatistics.buildEmpty(), new ArrayList<>());
    }
}
