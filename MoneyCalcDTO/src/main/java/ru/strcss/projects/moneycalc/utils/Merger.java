package ru.strcss.projects.moneycalc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.entities.Transaction;

/**
 * Utility class for merging filled object from database with income object with random null values.
 * Merging prevents overwriting non-null values in database with nulls.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Merger {
    public static SpendingSection mergeSpendingSections(SpendingSection sectionOld, SpendingSection sectionNew) {
        return SpendingSection.builder()
                .id(sectionOld.getId())
                .personId(sectionOld.getPersonId())
                .sectionId(sectionOld.getSectionId())
                .logoId(sectionNew.getLogoId() == null ? sectionOld.getLogoId() : sectionNew.getLogoId())
                .isRemoved(sectionNew.getIsRemoved() == null ? sectionOld.getIsRemoved() : sectionNew.getIsRemoved())
                .name(sectionNew.getName() == null ? sectionOld.getName() : sectionNew.getName())
                .budget(sectionNew.getBudget() == null ? sectionOld.getBudget() : sectionNew.getBudget())
                .isAdded(sectionNew.getIsAdded() == null ? sectionOld.getIsAdded() : sectionNew.getIsAdded())
                .build();
    }

    public static Transaction mergeTransactions(Transaction transactionOld, Transaction transactionNew) {
        return Transaction.builder()
                .id(transactionOld.getId())
                .personId(transactionOld.getPersonId())
                .sectionId(transactionNew.getSectionId() == null ? transactionOld.getSectionId() : transactionNew.getSectionId())
                .title(transactionNew.getTitle() == null ? transactionOld.getTitle() : transactionNew.getTitle())
                .currency(transactionNew.getCurrency() == null ? transactionOld.getCurrency() : transactionNew.getCurrency())
                .description(transactionNew.getDescription() == null ? transactionOld.getDescription() : transactionNew.getDescription())
                .sum(transactionNew.getSum() == null ? transactionOld.getSum() : transactionNew.getSum())
                .date(transactionNew.getDate() == null ? transactionOld.getDate() : transactionNew.getDate())
                .build();
    }
}
