package ru.strcss.projects.moneycalc.moneycalcdto;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created by Stormcss
 * Date: 12.12.2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PojoTestUtils {

    private static final Validator ACCESSOR_VALIDATOR = ValidatorBuilder.create()
            .with(new GetterTester())
            .with(new SetterTester())
            .build();

    static void validateAccessors(final Class<?> clazz) {
        ACCESSOR_VALIDATOR.validate(PojoClassFactory.getPojoClass(clazz));
    }
}