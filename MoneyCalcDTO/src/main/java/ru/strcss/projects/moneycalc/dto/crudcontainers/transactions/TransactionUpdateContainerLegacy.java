package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.entities.TransactionLegacy;

@Data
@AllArgsConstructor
public class TransactionUpdateContainerLegacy {

    /**
     * transaction Id which will be updated
     */
    private Integer id;

    /**
     * Transaction object with new values
     */
    private TransactionLegacy transaction;
}
