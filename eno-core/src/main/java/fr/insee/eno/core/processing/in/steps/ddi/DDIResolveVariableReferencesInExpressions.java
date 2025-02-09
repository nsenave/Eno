package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;

public class DDIResolveVariableReferencesInExpressions implements ProcessingStep<EnoQuestionnaire> {

    /**
     * In DDI, VTL expressions contain variables references instead of their name.
     * This method replaces the references with the names in calculated variables, filters and controls.
     */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Calculated variables
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                .map(CalculatedVariable.class::cast)
                .forEach(this::resolveExpression);
        // Controls
        enoQuestionnaire.getControls().forEach(this::resolveExpression);
        // Filters
        enoQuestionnaire.getFilters().forEach(this::resolveExpression);
        // Loops
        enoQuestionnaire.getLoops().stream()
                .filter(StandaloneLoop.class::isInstance)
                .map(StandaloneLoop.class::cast)
                .forEach(this::resolveExpression);
    }

    /**
     * Replace variable reference by variable name in given object's expression.
     */
    private void resolveExpression(EnoObjectWithExpression enoObject) {
        CalculatedExpression expression = enoObject.getExpression();
        resolveExpression(expression);
    }

    private void resolveExpression(StandaloneLoop standaloneLoop) {
        resolveExpression(standaloneLoop.getMinIteration());
        resolveExpression(standaloneLoop.getMaxIteration());
    }

    private static void resolveExpression(CalculatedExpression expression) {
        String value = expression.getValue();
        for (BindingReference bindingReference : expression.getBindingReferences()) {
            value = value.replace(bindingReference.getId(), bindingReference.getVariableName());
        }
        expression.setValue(value);
    }

}
