package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class Mapper {

    public static Iterator<PropertyDescriptor> propertyDescriptorIterator(BeanWrapper beanWrapper) {
        return Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .iterator();
    }

    public static boolean isSimpleType(Class<?> classType) {
        return String.class.isAssignableFrom(classType)
                || int.class.isAssignableFrom(classType)
                || BigInteger.class.isAssignableFrom(classType)
                || double.class.isAssignableFrom(classType)
                || boolean.class.isAssignableFrom(classType)
                || Enum.class.isAssignableFrom(classType);
        // TODO: other simple types later (probably)
    }

    @SuppressWarnings("unchecked")
    Collection<Object> readCollection(PropertyDescriptor propertyDescriptor, EnoObject enoObject) {
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

    void unknownTypeException(Class<?> classType, PropertyDescriptor propertyDescriptor, Class<?> modelContextType) {
        log.debug(String.format(
                "Type '%s' found on Eno model property '%s' of model class '%s' " +
                        "is neither a simple type nor an Eno object.",
                classType, propertyDescriptor.getName(), modelContextType.getSimpleName()));
        log.debug("hint: If it should be a simple type, check isSimpleType method in Mapper class.");
        log.debug("hint: If it should be a complex type, make sure that the object inherits EnoObject.");
        throw new RuntimeException(String.format("Unknown type '%s' encountered in Eno model.", classType));
    }

}
