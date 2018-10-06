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
public class TransactionLegacy implements Serializable {

    /**
     * id of Transaction - transaction id in DB Table
     */
    private Integer id;

    /**
     * Person Id - used for linking Person with current Transaction in DB
     */
    private Integer personId;
    private Integer sectionId;
    private String date;
    private Integer sum;
    private String currency;
    private String title;
    private String description;
}


