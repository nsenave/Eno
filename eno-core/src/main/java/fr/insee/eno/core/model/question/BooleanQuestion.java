package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDIMapping;

@DDIMapping(targetType = QuestionItemType.class, inCondition = "responseDomain instanceof datacollection33.NominalDomainType")
public class BooleanQuestion extends SingleResponseQuestion {
}
