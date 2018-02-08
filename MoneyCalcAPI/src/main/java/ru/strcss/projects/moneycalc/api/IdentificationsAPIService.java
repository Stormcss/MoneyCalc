package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.enitities.Identifications;

public interface IdentificationsAPIService {

    AjaxRs<Identifications> saveIdentifications(Identifications identifications);

    AjaxRs<Identifications> getIdentifications(String login);
}
