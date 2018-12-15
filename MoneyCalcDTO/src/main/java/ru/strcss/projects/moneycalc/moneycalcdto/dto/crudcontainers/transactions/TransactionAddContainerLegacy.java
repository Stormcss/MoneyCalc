package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.TransactionLegacy;

@Data
@AllArgsConstructor
public class TransactionAddContainerLegacy {
    private TransactionLegacy transaction;
}

