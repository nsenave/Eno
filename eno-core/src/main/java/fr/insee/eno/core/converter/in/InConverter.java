package fr.insee.eno.core.converter.in;

import fr.insee.eno.core.annotations.InConversion;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class InConverter<T> {

    Format format;
    EvaluationContext context;

    InConverter(Format format, EvaluationContext context) {
        this.format = format;
        this.context = context;
    }

    public EnoObject instantiateFrom(Class<?> enoClass, T inObject) {
        InConversion[] inConversions = enoClass.getAnnotationsByType(InConversion.class);
        if (inConversions.length == 0)
            return callConstructor(enoClass);
        List<InConversion> inConversionResult = Arrays.stream(inConversions)
                .filter(inConversion -> format.equals(inConversion.format()))
                .filter(inConversion -> evaluateConversionExpression(inObject, inConversion, enoClass))
                .toList();
        verifyConversionResult(enoClass, inConversionResult);
        return callConstructor(inConversionResult.get(0).targetType());
    }

    private static void verifyConversionResult(Class<?> enoClass, List<InConversion> inConversionResult) {
        if (inConversionResult.size() == 0)
            throw new ConversionException("No match evaluating conversion annotation on class " + enoClass);
        if (inConversionResult.size() > 1)
            throw new ConversionException("Several matches evaluating conversion annotation on class " + enoClass);
    }

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
