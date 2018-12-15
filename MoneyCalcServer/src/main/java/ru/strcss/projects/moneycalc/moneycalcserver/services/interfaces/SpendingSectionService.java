package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;

import java.util.List;

public interface SpendingSectionService {

    List<SpendingSection> getSpendingSections(String login, boolean withNonAdded,
                                              boolean withRemoved, boolean withRemovedOnly) throws Exception;

    Boolean addSpendingSection(String login, SpendingSection section) throws Exception;

    Boolean updateSpendingSection(String login, SpendingSectionUpdateContainer updateContainer) throws Exception;

    Boolean deleteSpendingSection(String login, Integer sectionId) throws Exception;

    Boolean isSpendingSectionIdExists(String login, Integer sectionId);

    Boolean isSpendingSectionNameNew(String login, String name);

    Boolean isNewNameAllowed(String login, SpendingSectionUpdateContainer updateContainer);
}
