package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;

import java.util.stream.Stream;

public class EnoModelIterator {

    //TODO : should be done with (new PathMatchingResourcePatternResolver()).getResources("classpath*:fr/insee/eno/core/model/**/*.class")
    public Stream<Class<? extends EnoObject>> stream(){
        return Stream.of(AbstractSequence.class,
                BindingReference.class,
               // CalculatedExpression.class (does not extend EnoObject),
                CodeItem.class,
                CodeResponse.class,
                //Control.Criticality.class (does not extend EnoObject),
                Control.class,
                Declaration.class,
                //DeclarationInterface.class (does not extend EnoObject),
                //EnoComponent.class (does not extend EnoObject),
                EnoIdentifiableObject.class,
                EnoObject.class,
                //EnoObjectWithExpression.class (does not extend EnoObject),
                EnoQuestionnaire.class,
                Filter.class,
                Instruction.class,
                Loop.class,
                //Mode.class (does not extend EnoObject),
                Response.class,
                //ResponseFormat.class (does not extend EnoObject),
                Sequence.class,
                //SequenceItem.SequenceItemType.class (does not extend EnoObject),
                SequenceItem.class,
                Subsequence.class,
                BooleanQuestion.class,
                DateQuestion.class,
                MultipleChoiceQuestion.class,
                MultipleResponseQuestion.class,
                NumericQuestion.class,
                Question.class,
                SingleResponseQuestion.class,
                TableCell.class,
                TableLine.class,
                TableQuestion.class,
                TextQuestion.class,
                //UniqueChoiceQuestion.DisplayFormat.class (does not extend EnoObject),
                UniqueChoiceQuestion.class,
                CalculatedVariable.class,
                CollectedVariable.class,
                Variable.class,
                VariableGroup.class);
    }
}
