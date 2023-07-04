package fr.insee.eno.core.converter.in;

import fr.insee.eno.core.parameter.Format;
import org.springframework.expression.EvaluationContext;
import reusable33.AbstractIdentifiableType;

public class DDIInConverter extends InConverter<Object> { // TODO: use AbstractIdentifiableType here and in mapper, or remove generics

    public DDIInConverter(EvaluationContext context) {
        super(Format.DDI, context);
    }

}
