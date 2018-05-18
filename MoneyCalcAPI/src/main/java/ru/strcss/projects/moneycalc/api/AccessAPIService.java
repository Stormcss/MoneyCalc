package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Access;

public interface AccessAPIService {

    ResponseEntity<MoneyCalcRs<Access>> saveAccess(IdentificationsUpdateContainer updateContainer);

    ResponseEntity<MoneyCalcRs<Access>> getAccess();
}
