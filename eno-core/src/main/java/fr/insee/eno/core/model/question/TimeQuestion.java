package fr.insee.eno.core.model.question;

import fr.insee.eno.core.annotations.InConversion;
import fr.insee.eno.core.parameter.Format;

import java.util.Set;

@InConversion(format = Format.DDI, targetType = DateQuestion.class,
        expression = "T(fr.insee.eno.core.model.question.TimeQuestion).DDI_DATE_TYPE_CODE.contains(" +
                "#this.getResponseDomain().getDateTypeCode().getStringValue())")
@InConversion(format = Format.DDI, targetType = DurationQuestion.class,
        expression = "T(fr.insee.eno.core.model.question.TimeQuestion).DDI_DURATION_TYPE_CODE.contains(" +
                "#this.getResponseDomain().getDateTypeCode().getStringValue())")
public class TimeQuestion extends SingleResponseQuestion {

    public static final Set<String> DDI_DATE_TYPE_CODE = Set.of("date", "gYearMonth", "gYear");
    public static final Set<String> DDI_DURATION_TYPE_CODE = Set.of("duration");

}
