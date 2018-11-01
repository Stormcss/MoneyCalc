package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.entities.Person;

public interface RegisterAPIService {
    ResponseEntity<MoneyCalcRs<Person>> registerPerson(Credentials credentials);
}
