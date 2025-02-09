package fr.insee.eno.core.model.navigation;

import datacollection33.IfThenElseType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Filter defined at the questionnaire level.
 * @see ComponentFilter for filter objects wihtin components. */
@Getter
@Setter
@Context(format = Format.DDI, type = IfThenElseType.class)
public class Filter extends EnoIdentifiableObject implements EnoObjectWithExpression {

    /** Lunatic filters doesn't have an identifier. */
    @DDI("getIDArray(0).getStringValue()")
    private String id;

    /** Filter expression. */
    @DDI("getIfCondition().getCommandArray(0)")
    private CalculatedExpression expression;

    /** Same principle as sequence items list in sequence objects. */
    @DDI("#index.get(#this.getThenConstructReference().getIDArray(0).getStringValue())" +
            ".getControlConstructReferenceList()")
    private List<ItemReference> filterItems = new ArrayList<>();

    /** References of sequences, subsequences or/and questions that are in the scope of the filter.
     * In DDI, this property is filled by a processing using the "filterItems" property. */
    private final List<StructureItemReference> filterScope = new ArrayList<>();

}
