package fr.insee.eno.core.converter.in;

import fr.insee.eno.core.annotations.InConversion;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Class to convert objects between input formats and Eno model.
 * @param <T> Type objects in the input format.
 */
@Slf4j
public abstract class InConverter<T> {

    /** Input format. */
    Format format;
    /** Evaluation context used when evaluating SpEL expression. */
    EvaluationContext context; // TODO: context might not be useful for conversion expressions, to be removed probably

    InConverter(Format format, EvaluationContext context) {
        this.format = format;
        this.context = context;
    }

    /**
     * Returns the appropriate Eno object from eno class type and input format object given,
     * using the conversion annotations defined in the model.
     * @param enoClass Type to be instantiated.
     * @param inObject Corresponding object of the input format.
     * @return The appropriate Eno object in this context.
     * @see InConversion
     */
    public final EnoObject instantiateFrom(Class<?> enoClass, T inObject) {
        // Read the conversion annotations that match the format
        List<InConversion> inConversions = Arrays.stream(enoClass.getAnnotationsByType(InConversion.class))
                .filter(inConversion -> format.equals(inConversion.format()))
                .toList();
        // If none, this means that there is no conversion to do
        if (inConversions.isEmpty()) {
            // Temporary block while annotations are not placed everywhere needed: use the old converter classes
            if (Modifier.isAbstract(enoClass.getModifiers())) {
                return DDIConverter.instantiateFromDDIObject(inObject); //TODO: remove usage of this (conversion using annotations)
            }
            //
            return callConstructor(enoClass);
        }
        // Else, evaluate the conversion expressions
        List<InConversion> inConversionResult = inConversions.stream()
                .filter(inConversion -> evaluateConversionExpression(inObject, inConversion, enoClass))
                .toList();
        // Exactly one match is expected
        verifyConversionResult(enoClass, inConversionResult);
        // Recursive call using the type defined in the match
        return instantiateFrom(inConversionResult.get(0).targetType(), inObject);
    }

    /** Throws a conversion exception if the result is not exactly of size 1. */
    private static void verifyConversionResult(Class<?> enoClass, List<InConversion> inConversionResult) {
        int size = inConversionResult.size();
        if (size == 0)
            throw new ConversionException("No match evaluating conversion annotation on class " + enoClass);
        if (size > 1)
            throw new ConversionException("Several matches evaluating conversion annotation on class " + enoClass);
    }

    /** Evaluate the expression contained the in conversion annotation against the object given,
     * also using the evaluation context.
     * The result is expected to be boolean, an exception is thrown if the expression doesn't respect that. */
    private Boolean evaluateConversionExpression(T inObject, InConversion inConversion, Class<?> enoClass) {
        Expression expression = new SpelExpressionParser().parseExpression(inConversion.expression());
        try {
            return expression.getValue(context, inObject, Boolean.class);
        } catch (EvaluationException e) {
            log.debug("Evaluation failed on SpEL expression: " + inConversion.expression());
            log.debug("Make sure that the expression returns a boolean value.");
            throw new ConversionException("Error when trying to evaluate conversion expression on class " + enoClass, e);
        }
    }

    /** Call the constructor of the given type using reflection. */
    private EnoObject callConstructor(Class<?> classType) {
        try {
            return (EnoObject) classType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            log.debug("Default constructor may be missing in class " + classType);
            throw new MappingException("Unable to create instance for class " + classType, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.debug("Conversion annotation may be missing in class " + classType);
            throw new MappingException("Unable to create instance for class " + classType, e);
        }
    }

}
