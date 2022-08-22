package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.DDIIndex;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.AbstractIdentifiableType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class DDIMapper extends Mapper {

    private EvaluationContext context;

    public DDIMapper() {}

    private void setup(AbstractIdentifiableType ddiObject) {
        log.debug("DDI mapping entry object: " + DDIToString(ddiObject));
        // Index DDI object
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDIObject(ddiObject);
        log.debug("DDI index: " + ddiIndex);
        // Init the context and put the DDI index
        context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
    }

    public void mapDDI(@NonNull DDIInstanceDocument ddiInstanceDocument, @NonNull EnoQuestionnaire enoQuestionnaire) {
        mapDDI(ddiInstanceDocument.getDDIInstance(), enoQuestionnaire);
    }

    public void mapDDI(@NonNull DDIInstanceType ddiInstanceType, @NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI instance and Eno questionnaire.");
        setup(ddiInstanceType);
        recursiveMapping(ddiInstanceType, enoQuestionnaire);
        log.info("Finished mapping between DDI instance and Eno questionnaire.");
    }

    public void mapDDIObject(AbstractIdentifiableType ddiObject, EnoObject enoObject) {
        // TODO
        //DDIContext ddiContext = enoObject.getClass().getAnnotation(DDIContext.class);
        //List<Class<?>> contextTypes = new ArrayList<>(ddiContext.contextType());
        //if (! contextTypes.contains(ddiObject.getClass())) {
        //    throw new IllegalArgumentException(String.format(
        //            "DDI object of type '%s' is not compatible with Eno object of type '%s'",
        //            ddiObject.getClass(), enoObject.getClass()));
        //{
        //
        setup(ddiObject);
        recursiveMapping(ddiObject, enoObject);
    }

    private void recursiveMapping(Object ddiObject, EnoObject enoObject) {

        // Local variable used for logging purposes
        Class<?> modelContextType = enoObject.getClass();

        log.debug("Start mapping for "+DDIToString(ddiObject)
                +" with model context type '"+modelContextType.getSimpleName()+"'");

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();

            // Property name
            String propertyName = propertyDescriptor.getName();

            // Spring TypeDescriptor of the current property descriptor (several usages below)
            TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyName);
            assert typeDescriptor != null;

            // Identify the class type of the property
            Class<?> classType = propertyDescriptor.getPropertyType();
            //Class<?> classType = typeDescriptor.getType();

            // Use the Spring type descriptor to get the DDI annotation if any
            DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
            if (ddiAnnotation != null) {

                log.debug("Processing property '"+propertyName
                        +"' of class '"+modelContextType.getSimpleName()+"'");

                // Instantiate a Spring expression with the annotation content
                Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

                // Simple types
                if (isSimpleType(classType)) {
                    // Simply set the value in the field
                    Object ddiValue = expression.getValue(context, ddiObject);
                    if (ddiValue != null) {
                        beanWrapper.setPropertyValue(propertyName, ddiValue);
                        log.debug("Value '"+beanWrapper.getPropertyValue(propertyName)+"' set"
                                +" on property '"+propertyName+"'"
                                +" of class '"+modelContextType.getSimpleName()+"'");
                    }
                    // It is allowed to have null values (a DDI property can be present or not depending on the case)
                    else {
                        log.debug("null value got from evaluating DDI annotation expression" +
                                " on property '"+propertyName+"'" +
                                " of class '"+modelContextType.getSimpleName()+"'");
                    }
                }

                // Complex types
                else if (EnoObject.class.isAssignableFrom(classType)) {
                    // Instantiate the model target object
                    EnoObject enoObject2 = callConstructor(classType);
                    // Attach it to the current object
                    beanWrapper.setPropertyValue(propertyName, enoObject2);
                    log.debug("New instance of '"+enoObject2.getClass().getSimpleName()+"' set"
                            +" on property '"+propertyName+"'"
                            +" of class '"+modelContextType.getSimpleName()+"'");
                    // Keep parent reference
                    enoObject2.setParent(enoObject);
                    // Recursive call of the mapper to dive into this object
                    Object ddiObject2 = expression.getValue(context, ddiObject);
                    if (ddiObject2 != null) {
                        recursiveMapping(ddiObject2, enoObject2);
                    }
                    // For now, it is not allowed to have a null DDI object on complex type properties
                    else {
                        throw new RuntimeException(String.format(
                                "DDI object mapped by the annotation on property '%s' in class %s is null",
                                propertyName, modelContextType));
                    }
                }

                // Lists
                else if (List.class.isAssignableFrom(classType)) {
                    // Get the DDI collection instance by evaluating the expression
                    List<?> ddiCollection = expression.getValue(context, ddiObject, List.class);
                    // If the DDI collection is null and null is not allowed by the annotation, exception
                    if (ddiCollection == null && !ddiAnnotation.allowNullList()) {
                        log.debug(String.format(
                                "Incoherent expression in field of DDI annotation on property '%s' in class %s.",
                                propertyName, modelContextType));
                        log.debug("If the DDI list can actually be null, use the annotation property to allow it.");
                        throw new RuntimeException(String.format(
                                "DDI list mapped by the annotation on property '%s' in class %s is null",
                                propertyName, modelContextType));
                    }
                    // If the DDI collection is null and null is allowed, do nothing, else:
                    else if (ddiCollection != null) {
                        // Get the Eno model collection
                        Collection<Object> modelCollection = readCollection(propertyDescriptor, enoObject);
                        // Get the content type of the model collection
                        Class<?> modelTargetType = typeDescriptor.getResolvableType()
                                .getGeneric(0).getRawClass();
                        assert modelTargetType != null;
                        // List of simple types
                        if (isSimpleType(modelTargetType)) {
                            modelCollection.addAll(ddiCollection);
                        }
                        // Lists of complex types // Get the model collection instance
                        else if (EnoObject.class.isAssignableFrom(modelTargetType)) {
                            // Iterate on the DDI collection
                            for (int i=0; i<ddiCollection.size(); i++) {
                                Object ddiObject2 = ddiCollection.get(i);
                                // Put current list index in context TODO: I don't really like this but... :(((
                                context.setVariable("listIndex", i);
                                // Instantiate a model object per DDI object and add it in the model collection
                                EnoObject enoObject2;
                                // If the list content type is abstract call the converter
                                if (Modifier.isAbstract(modelTargetType.getModifiers())) {
                                    enoObject2 = DDIConverter.instantiateFromDDIObject(ddiObject2); //TODO: remove usage of this (conversion using annotations)
                                }
                                // Else, call class constructor
                                else {
                                    enoObject2 = callConstructor(modelTargetType);
                                }
                                // Keep parent reference
                                enoObject2.setParent(enoObject);
                                // Add the created instance in the model list
                                modelCollection.add(enoObject2);
                                // Recursive call on these instances
                                recursiveMapping(ddiObject2, enoObject2);
                            }
                        }
                        //
                        else {
                            unknownTypeException(modelTargetType, propertyDescriptor, modelContextType);
                        }
                    }
                }

                else {
                    unknownTypeException(classType, propertyDescriptor, modelContextType);
                }

            }
        }

    }

    private EnoObject callConstructor(Class<?> classType) {
        try {
            return (EnoObject) classType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            log.debug("Default constructor may be missing in class " + classType);
            throw new RuntimeException("Unable to create instance for class " + classType, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to create instance for class " + classType, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> readCollection(PropertyDescriptor propertyDescriptor, EnoObject enoObject) {
        try {
            return (Collection<Object>) propertyDescriptor.getReadMethod().invoke(enoObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.debug("hint: Make sure that collection has been initialized (i.e. is not null) in model class.");
            log.debug("hint: Example: List<SomeEnoObject> = new ArrayList<>();");
            throw new RuntimeException(
                    String.format("Unable to get collection instance on property '%s' of class '%s'.",
                            propertyDescriptor.getName(), enoObject.getClass()),
                    e);
        }
    }

    private void unknownTypeException(Class<?> classType, PropertyDescriptor propertyDescriptor, Class<?> modelContextType) {
        log.debug(String.format(
                "Type '%s' found on Eno model property '%s' of model class '%s' " +
                        "is neither a simple type nor an Eno object.",
                classType, propertyDescriptor.getName(), modelContextType.getSimpleName()));
        log.debug("hint: If it should be a simple type, check isSimpleType method in Mapper class.");
        log.debug("hint: If it should be a complex type, make sure that the object inherits EnoObject.");
        throw new RuntimeException(String.format("Unknown type '%s' encountered in Eno model.", classType));
    }

    private String DDIToString(@NonNull Object ddiInstance) {
        return ddiInstance.getClass().getSimpleName()+"["
                +((ddiInstance instanceof AbstractIdentifiableType) ?
                "id="+((AbstractIdentifiableType)ddiInstance).getIDArray(0).getStringValue() :
                "")
                +"]";
    }

}
