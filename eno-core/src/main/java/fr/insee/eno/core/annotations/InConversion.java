package fr.insee.eno.core.annotations;

import fr.insee.eno.core.parameter.Format;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(value = InConversions.class)
public @interface InConversion {

    Format format();

    Class<?> targetType();

    String expression();

}
