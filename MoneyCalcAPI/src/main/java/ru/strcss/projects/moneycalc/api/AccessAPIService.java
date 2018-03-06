package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Access;

public interface AccessAPIService {

    AjaxRs<Access> saveAccess(IdentificationsUpdateContainer updateContainer);

    AjaxRs<Access> getAccess();
}
