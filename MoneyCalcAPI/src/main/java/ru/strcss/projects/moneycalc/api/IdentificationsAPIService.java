package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;

public interface IdentificationsAPIService {

    ResponseEntity<MoneyCalcRs<Identifications>> updateIdentifications(IdentificationsUpdateContainer updateContainer);

    ResponseEntity<MoneyCalcRs<Identifications>> getIdentifications();
}
