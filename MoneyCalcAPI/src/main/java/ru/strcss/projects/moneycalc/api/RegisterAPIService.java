package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;

public interface RegisterAPIService {
    AjaxRs registerPerson(Credentials credentials);
}
