package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.enitities.Identifications;

public interface IdentificationsAPIService {

    AjaxRs saveIdentifications(Identifications identifications);

    AjaxRs getIdentifications(String login);
}
