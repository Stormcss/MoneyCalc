package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;

public interface IdentificationsAPIService {

    AjaxRs<Identifications> saveIdentifications(IdentificationsUpdateContainer updateContainer);

    AjaxRs<Identifications> getIdentifications(LoginGetContainer getContainer);
}
