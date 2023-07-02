package fr.insee.eno.core.converter;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.converter.in.DDIInConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.TextDomainType;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DDIInConverterTest {

    @Test
    void pocTest() {
        //
        QuestionItemType questionItemType = QuestionItemType.Factory.newInstance();
        questionItemType.setResponseDomain(TextDomainType.Factory.newInstance());
        //
        DDIInConverter converter = new DDIInConverter(new StandardEvaluationContext());
        EnoObject result = converter.instantiateFrom(SingleResponseQuestion.class, questionItemType);
        //
        assertTrue(result instanceof TextQuestion);
    }

}
