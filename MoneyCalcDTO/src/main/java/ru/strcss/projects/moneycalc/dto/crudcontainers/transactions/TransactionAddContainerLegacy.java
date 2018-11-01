package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.entities.TransactionLegacy;

@Data
@AllArgsConstructor
public class TransactionAddContainerLegacy {
    private TransactionLegacy transaction;
}

