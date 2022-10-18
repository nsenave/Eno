package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import org.junit.jupiter.api.Test;
import org.slf4j.*;
import org.slf4j.event.*;
import org.slf4j.helpers.*;
import org.slf4j.spi.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoModelIteratorTest {

    @Test
    void findAllPublicClassesFromPackageAndSubPackagesTest() {

        assertEquals(Set.of(ILoggerFactory.class,
                IMarkerFactory.class,
                Logger.class,
                LoggerFactory.class,
                LoggerFactoryFriend.class,
                MDC.MDCCloseable.class,
                MDC.class,
                Marker.class,
                MarkerFactory.class,
                DefaultLoggingEvent.class,
                EventConstants.class,
                EventRecordingLogger.class,
                KeyValuePair.class,
                Level.class,
                LoggingEvent.class,
                SubstituteLoggingEvent.class,
                AbstractLogger.class,
                BasicMDCAdapter.class,
                BasicMarker.class,
                BasicMarkerFactory.class,
                CheckReturnValue.class,
                FormattingTuple.class,
                LegacyAbstractLogger.class,
                MarkerIgnoringBase.class,
                MessageFormatter.class,
                NOPLogger.class,
                NOPLoggerFactory.class,
                NOPMDCAdapter.class,
                NOP_FallbackServiceProvider.class,
                NormalizedParameters.class,
                SubstituteLogger.class,
                SubstituteLoggerFactory.class,
                SubstituteServiceProvider.class,
                ThreadLocalMapOfStacks.class,
                Util.class,
                CallerBoundaryAware.class,
                DefaultLoggingEventBuilder.class,
                LocationAwareLogger.class,
                LoggerFactoryBinder.class,
                LoggingEventAware.class,
                LoggingEventBuilder.class,
                MDCAdapter.class,
                MarkerFactoryBinder.class,
                NOPLoggingEventBuilder.class,
                SLF4JServiceProvider.class),
                (new EnoModelIterator()).findAllPublicClassesFromPackageAndSubPackages(Logger.class.getPackage()).collect(Collectors.toSet()));
    }


    @Test
    public void streamTest() {

        assertEquals(Set.of(AbstractSequence.class,
                BindingReference.class,
                // CalculatedExpression.class (does not extend EnoObject),
                CodeItem.class,
                CodeResponse.class,
                //Control.Criticality.class (does not extend EnoObject),
                Control.class,
                Declaration.class,
                //DeclarationInterface.class (does not extend EnoObject),
                //EnoComponent.class (does not extend EnoObject),
                EnoIdentifiableObject.class,
                EnoObject.class,
                //EnoObjectWithExpression.class (does not extend EnoObject),
                EnoQuestionnaire.class,
                Filter.class,
                Instruction.class,
                Loop.class,
                //Mode.class (does not extend EnoObject),
                Response.class,
                //ResponseFormat.class (does not extend EnoObject),
                Sequence.class,
                //SequenceItem.SequenceItemType.class (does not extend EnoObject),
                SequenceItem.class,
                Subsequence.class,
                BooleanQuestion.class,
                DateQuestion.class,
                MultipleChoiceQuestion.class,
                MultipleResponseQuestion.class,
                NumericQuestion.class,
                Question.class,
                SingleResponseQuestion.class,
                TableCell.class,
                TableLine.class,
                TableQuestion.class,
                TextQuestion.class,
                //UniqueChoiceQuestion.DisplayFormat.class (does not extend EnoObject),
                UniqueChoiceQuestion.class,
                CalculatedVariable.class,
                CollectedVariable.class,
                Variable.class,
                VariableGroup.class), (new EnoModelIterator()).stream().collect(Collectors.toSet()));
    }

    @Test
    public void shortenUriTest(){
        var enoModelIterator = new EnoModelIterator();
        assertEquals("/C:/Users/xrmfux/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/2.0.0/82b150a36b504a09076b29c0509e31702c9c3999/slf4j-api-2.0.0.jar",
                enoModelIterator.shortenUri("file:///C:/Users/xrmfux/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/2.0.0/82b150a36b504a09076b29c0509e31702c9c3999/slf4j-api-2.0.0.jar"));
        assertEquals("/C:/Users/xrmfux/DEPOTS/eno-java/eno-core/build/classes/java/main/",
                enoModelIterator.shortenUri("/C:/Users/xrmfux/DEPOTS/eno-java/eno-core/build/classes/java/main/"));
        assertEquals("/C:/Users/xrmfux/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/2.0.0/82b150a36b504a09076b29c0509e31702c9c3999/slf4j-api-2.0.0.jar!/org/slf4j/ILoggerFactory.class",
                enoModelIterator.shortenUri("file:/C:/Users/xrmfux/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/2.0.0/82b150a36b504a09076b29c0509e31702c9c3999/slf4j-api-2.0.0.jar!/org/slf4j/ILoggerFactory.class"));
    }
}