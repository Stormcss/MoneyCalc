package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.entities.Identifications;

public interface IdentificationsAPIService {

    ResponseEntity<MoneyCalcRs<Identifications>> updateIdentifications(Identifications identifications);

    ResponseEntity<MoneyCalcRs<Identifications>> getIdentifications();
}
