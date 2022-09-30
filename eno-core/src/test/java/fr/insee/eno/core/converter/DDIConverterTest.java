package fr.insee.eno.core.converter;

import datacollection33.*;
import fr.insee.eno.core.model.question.BooleanQuestion;
import fr.insee.eno.core.model.question.DateQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import org.junit.jupiter.api.Test;
import physicaldataproduct33.BaseRecordLayoutDocument;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DDIConverterTest {

    @Test
    void instantiateFromDDIObject_NominalDomainType_OK() {
        var questionItemType=QuestionItemType.Factory.newInstance();
        questionItemType.setResponseDomain(NominalDomainType.Factory.newInstance());
        assertInstanceOf(BooleanQuestion.class,DDIConverter.instantiateFromDDIObject(questionItemType));
    }


    @Test
    void instantiateFromDDIObject_DateTimeDomainType_OK() {
        var questionItemType=QuestionItemType.Factory.newInstance();
        questionItemType.setResponseDomain(DateTimeDomainType.Factory.newInstance());
        assertInstanceOf(DateQuestion.class, DDIConverter.instantiateFromDDIObject(questionItemType));
    }

    @Test
    void instantiateFromDDIObject_DistributionDomainType_KO() {
        var questionItemType=QuestionItemType.Factory.newInstance();
        questionItemType.setResponseDomain(DistributionDomainType.Factory.newInstance());
        assertThrows(Exception.class, () -> DDIConverter.instantiateFromDDIObject(questionItemType));
    }


    @Test
    void instantiateFromDDIObject_CodeDomainType_OK() {
        var questionGridType=QuestionGridType.Factory.newInstance();
        var structuredMixedGridResponseDomainType = StructuredMixedGridResponseDomainType.Factory.newInstance();
        GridResponseDomainInMixedType[] gridResponseDomainInMixedArray = {GridResponseDomainInMixedType.Factory.newInstance()};
        gridResponseDomainInMixedArray[0].setResponseDomain(CodeDomainType.Factory.newInstance());
        structuredMixedGridResponseDomainType.setGridResponseDomainInMixedArray(gridResponseDomainInMixedArray);
        questionGridType.setStructuredMixedGridResponseDomain(structuredMixedGridResponseDomainType);
        assertInstanceOf(TableQuestion.class, DDIConverter.instantiateFromDDIObject(questionGridType));
    }

    @Test
    void instantiateFromDDIObject_DateTimeDomainType_KO() {
        var questionGridType=QuestionGridType.Factory.newInstance();
        var structuredMixedGridResponseDomainType = StructuredMixedGridResponseDomainType.Factory.newInstance();
        GridResponseDomainInMixedType[] gridResponseDomainInMixedArray = {GridResponseDomainInMixedType.Factory.newInstance()};
        gridResponseDomainInMixedArray[0].setResponseDomain(DateTimeDomainType.Factory.newInstance());
        structuredMixedGridResponseDomainType.setGridResponseDomainInMixedArray(gridResponseDomainInMixedArray);
        questionGridType.setStructuredMixedGridResponseDomain(structuredMixedGridResponseDomainType);
        assertThrows(Exception.class, () -> DDIConverter.instantiateFromDDIObject(questionGridType));
    }

    @Test
    void instantiateFromDDIObject_BaseRecordLayoutDocument_KO() {
        var baseRecordLayoutDocument = BaseRecordLayoutDocument.Factory.newInstance();
        assertThrows(Exception.class, () -> DDIConverter.instantiateFromDDIObject(baseRecordLayoutDocument));
    }

}