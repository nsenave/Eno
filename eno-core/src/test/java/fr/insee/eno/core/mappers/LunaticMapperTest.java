package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.InputNumber;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LunaticMapperTest {

    @Test
    void mapIncompatibleTypes_throwsException() {
        // Given
        TextQuestion enoTextQuestion = new TextQuestion();
        InputNumber lunaticNumericQuestion = new InputNumber();
        // When + Then
        LunaticMapper lunaticMapper = new LunaticMapper();
        assertThrows(IllegalArgumentException.class, () ->
                lunaticMapper.mapEnoObject(enoTextQuestion, lunaticNumericQuestion));
    }

    @Test
    void modelToLunaticTest() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        // Id
        enoQuestionnaire.setId("TEST-ID");
        // Variables
        Variable v1 = new CollectedVariable();
        v1.setName("foo1");
        enoQuestionnaire.getVariables().add(v1);
        Variable v2 = new CollectedVariable();
        v2.setName("foo2");
        enoQuestionnaire.getVariables().add(v2);
        // Sequences
        Sequence s1 = new Sequence();
        s1.setId("s1-id");
        Sequence s2 = new Sequence();
        s2.setId("s2-id");
        enoQuestionnaire.getSequences().add(s1);
        enoQuestionnaire.getSequences().add(s2);
        // Subsequences
        Subsequence ss1 = new Subsequence();
        ss1.setId("ss1-id");
        Subsequence ss2 = new Subsequence();
        ss2.setId("ss2-id");
        enoQuestionnaire.getSubsequences().add(ss1);
        enoQuestionnaire.getSubsequences().add(ss2);

        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        //
        assertEquals("TEST-ID", lunaticQuestionnaire.getId());
        //
        assertEquals("foo1", lunaticQuestionnaire.getVariables().get(0).getName());
        assertEquals("foo2", lunaticQuestionnaire.getVariables().get(1).getName());
        //
        List<ComponentType> lunaticSequences = lunaticQuestionnaire.getComponents()
                .stream()
                .filter(componentType -> componentType instanceof fr.insee.lunatic.model.flat.Sequence)
                .toList();
        assertEquals("s1-id", lunaticSequences.get(0).getId());
        assertEquals("s2-id", lunaticSequences.get(1).getId());
        //
        List<ComponentType> lunaticSubsequences = lunaticQuestionnaire.getComponents()
                .stream()
                .filter(componentType -> componentType instanceof fr.insee.lunatic.model.flat.Subsequence)
                .toList();
        assertEquals("ss1-id", lunaticSubsequences.get(0).getId());
        assertEquals("ss2-id", lunaticSubsequences.get(1).getId());
    }
}
