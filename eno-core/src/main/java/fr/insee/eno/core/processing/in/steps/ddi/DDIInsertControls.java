package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.List;

public class DDIInsertControls implements ProcessingStep<EnoQuestionnaire> {

    /** Controls are mapped directly in a flat list in the questionnaire object.
     * This processing is intended to insert them into the objects to which they belong.
     * (Controls are placed after the object they belong to in the sequence items lists.)
     * Concerned objects : sequences, subsequences and questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) { // Note: code is a bit clumsy but works
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<ItemReference> sequenceItems = sequence.getSequenceItems();
            if (! sequenceItems.isEmpty()) {
                int bound = sequenceItems.size();
                // Sequence controls
                int i = 0;
                while (i<bound && sequenceItems.get(i).getType() == ItemReference.ItemType.CONTROL) {
                    sequence.getControls().add(
                            (Control) enoQuestionnaire.get(sequenceItems.get(i).getId()));
                    i ++;
                }
                // Elements (questions, subsequences) in sequence
                while (i < bound) {
                    ItemReference sequenceItem = sequenceItems.get(i);
                    if (sequenceItem.getType() == ItemReference.ItemType.QUESTION) {
                        Question question = (Question) enoQuestionnaire.get(sequenceItem.getId());
                        i ++;
                        while (i<bound && sequenceItems.get(i).getType() == ItemReference.ItemType.CONTROL) {
                            question.getControls().add(
                                    (Control) enoQuestionnaire.get(sequenceItems.get(i).getId()));
                            i ++;
                        }
                    }
                    else if (sequenceItem.getType() == ItemReference.ItemType.SUBSEQUENCE) {
                        Subsequence subsequence = (Subsequence) enoQuestionnaire.get(sequenceItem.getId());
                        List<ItemReference> subsequenceItems = subsequence.getSequenceItems();
                        if (! subsequenceItems.isEmpty()) {
                            int bound2 = subsequenceItems.size();
                            // Subsequence controls
                            int j = 0;
                            while (j<bound2 && subsequenceItems.get(j).getType() == ItemReference.ItemType.CONTROL) {
                                subsequence.getControls().add(
                                        (Control) enoQuestionnaire.get(subsequenceItems.get(j).getId()));
                                j ++;
                            }
                            // Elements (questions) in subsequence
                            while (j < bound2) {
                                ItemReference subsequenceItem = subsequenceItems.get(j);
                                if (subsequenceItem.getType() == ItemReference.ItemType.QUESTION) {
                                    Question question = (Question) enoQuestionnaire.get(subsequenceItem.getId());
                                    j ++;
                                    while (j<bound2 && subsequenceItems.get(j).getType() == ItemReference.ItemType.CONTROL) {
                                        question.getControls().add(
                                                (Control) enoQuestionnaire.get(subsequenceItems.get(j).getId()));
                                        j ++;
                                    }
                                }
                                else { // skip other elements
                                    j ++;
                                }
                            }
                        }
                        i ++;
                    }
                    else { // skip other elements
                        i ++;
                    }
                }
            }
        }
    }

}
