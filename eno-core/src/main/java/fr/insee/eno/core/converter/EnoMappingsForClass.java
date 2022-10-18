package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.EnoObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
public record EnoMappingsForClass (
        Map<Expression, Class<? extends EnoObject>> mappings,
        Class<?> externalModelClass) {

    private static final EvaluationContext context = new StandardEvaluationContext();

    public Class<? extends EnoObject> from(@NonNull Object object) throws NoSuchElementException {
        return switch (mappings.size()) {
            case 0 -> throw new RuntimeException("No mappings for class " + object.getClass());
            case 1 -> mappings.values().iterator().next();
            default -> mappings.entrySet().stream()
                    .filter(entry -> objectFitSpEL(object, entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseThrow(()->
                            new RuntimeException("No mappings for objet " + object +" with class "+externalModelClass));
        };
    }

    private static boolean objectFitSpEL(Object object, Expression expression) {
        Boolean retour=expression.getValue(context, object, Boolean.class);
        if (retour==null) {
            log.atWarn().log("Expression {} returned null for object {}", expression, object);
            retour=false;
        }
        return retour;
    }
}
