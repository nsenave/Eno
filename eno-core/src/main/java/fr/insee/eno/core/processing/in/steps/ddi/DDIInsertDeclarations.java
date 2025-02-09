package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.ArrayList;
import java.util.List;

public class DDIInsertDeclarations implements ProcessingStep<EnoQuestionnaire> {

    /** Same idea as in the DDI insert controls processing, but for declarations.
     * (Declarations are placed before the object they belong to in the sequence items lists.)
     * Concerned objects : subsequences and questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) { // Note: code is a bit clumsy but works
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<String> declarationIdStack = new ArrayList<>();
            for (ItemReference sequenceItem : sequence.getSequenceItems()) {
                if (sequenceItem.getType() == ItemReference.ItemType.DECLARATION) {
                    declarationIdStack.add(sequenceItem.getId());
                }
                if (sequenceItem.getType() == ItemReference.ItemType.QUESTION) {
                    Question question = (Question) enoQuestionnaire.get(sequenceItem.getId());
                    declarationIdStack.forEach(declarationId ->
                            question.getDeclarations().add(
                                    (Declaration) enoQuestionnaire.get(declarationId)));
                    declarationIdStack = new ArrayList<>();
                }
                if (sequenceItem.getType() == ItemReference.ItemType.SUBSEQUENCE) {
                    Subsequence subsequence = (Subsequence) enoQuestionnaire.get(sequenceItem.getId());
                    declarationIdStack.forEach(declarationId ->
                            subsequence.getDeclarations().add(
                                    (Declaration) enoQuestionnaire.get(declarationId)));
                    declarationIdStack = new ArrayList<>();
                    for (ItemReference subsequenceItem : subsequence.getSequenceItems()) {
                        if (subsequenceItem.getType() == ItemReference.ItemType.DECLARATION) {
                            declarationIdStack.add(subsequenceItem.getId());
                        }
                        if (subsequenceItem.getType() == ItemReference.ItemType.QUESTION) {
                            Question question = (Question) enoQuestionnaire.get(subsequenceItem.getId());
                            declarationIdStack.forEach(declarationId ->
                                    question.getDeclarations().add(
                                            (Declaration) enoQuestionnaire.get(declarationId)));
                            declarationIdStack = new ArrayList<>();
                        }
                    }
                }
            }
        }
    }

}
