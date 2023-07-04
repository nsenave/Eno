package fr.insee.eno.core.model.question;

import datacollection33.CodeDomainType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.InConversion;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reusable33.RepresentationType;
import reusable33.StandardKeyValuePairType;

@Getter
@Setter
@Slf4j
@InConversion(format = Format.DDI, targetType = BooleanQuestion.class,
        expression = "getResponseDomain() instanceof T(datacollection33.NominalDomainType)")
@InConversion(format = Format.DDI, targetType = TextQuestion.class,
        expression = "getResponseDomain() instanceof T(reusable33.TextDomainType)")
@InConversion(format = Format.DDI, targetType = NumericQuestion.class,
        expression = "getResponseDomain() instanceof T(datacollection33.NumericDomainType)")
@InConversion(format = Format.DDI, targetType = TimeQuestion.class,
        expression = "getResponseDomain() instanceof T(datacollection33.DateTimeDomainType)")
@InConversion(format = Format.DDI, targetType = UniqueChoiceQuestion.class,
        expression = "getResponseDomain() instanceof T(datacollection33.CodeDomainType) " +
                "and #this.getUserAttributePairList().isEmpty()")
@InConversion(format = Format.DDI, targetType = UniqueChoiceQuestion.class,
        expression = "T(fr.insee.eno.core.model.question.SingleResponseQuestion).isPairwiseQuestion(#this)")
public abstract class SingleResponseQuestion extends Question {

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    @DDI(contextType = QuestionItemType.class, field = "getOutParameterArray(0)")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setResponse(#param)")
    Response response;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
                    "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic(contextType = {Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class, CheckboxOne.class, Radio.class, Dropdown.class},
            field = "setMandatory(#param)")
    boolean mandatory;

    public static final String DDI_PAIRWISE_KEY = "UIComponent";
    public static final String DDI_PAIRWISE_VALUE = "HouseholdPairing";

    public static boolean isPairwiseQuestion(QuestionItemType questionItemType) {
        RepresentationType representationType = questionItemType.getResponseDomain();
        if (!(representationType instanceof CodeDomainType))
            return false;
        if (! questionItemType.getUserAttributePairList().isEmpty()) {
            StandardKeyValuePairType userAttributePair = questionItemType.getUserAttributePairArray(0);
            String attributeKey = userAttributePair.getAttributeKey().getStringValue();
            String attributeValue = userAttributePair.getAttributeValue().getStringValue();
            if (! DDI_PAIRWISE_KEY.equals(attributeKey))
                log.warn(String.format(
                        "Attribute pair list found in question item '%s', but key is equal to '%s' (should be '%s')",
                        questionItemType.getIDArray(0).getStringValue(), attributeKey, DDI_PAIRWISE_KEY));
            if (! DDI_PAIRWISE_VALUE.equals(attributeValue))
                log.warn(String.format(
                        "Attribute pair list found in question item '%s', but value is equal to '%s' (should be '%s')",
                        questionItemType.getIDArray(0).getStringValue(), attributeValue, DDI_PAIRWISE_VALUE));
            return true;
        }
        return false;
    }

}
