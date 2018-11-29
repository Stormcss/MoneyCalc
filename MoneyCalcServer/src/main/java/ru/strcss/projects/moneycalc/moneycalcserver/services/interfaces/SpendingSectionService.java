package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.entities.SpendingSection;

import java.util.List;

public interface SpendingSectionService {

    List<SpendingSection> getSpendingSections(String login, boolean withNonAdded,
                                              boolean withRemoved, boolean withRemovedOnly);

    Boolean addSpendingSection(String login, SpendingSection section);

    Boolean updateSpendingSection(String login, SpendingSectionUpdateContainer updateContainer);

    Boolean deleteSpendingSection(String login, Integer sectionId);

    Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId);

    Boolean isSpendingSectionIdExists(String login, Integer sectionId);

    Boolean isSpendingSectionNameNew(String login, String name);

    Boolean isNewNameAllowed(String login, SpendingSectionUpdateContainer updateContainer);
}
