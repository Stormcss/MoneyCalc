package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.enitities.Person;

public interface RegisterAPIService {
    AjaxRs<Person> registerPerson(Credentials credentials);
}
