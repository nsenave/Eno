package fr.insee.eno.core.converter;

import fr.insee.eno.core.annotations.DDIMapping;
import fr.insee.eno.core.annotations.DDICommonMapping;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.model.EnoObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EnoMappings {

    private static final SpelExpressionParser spelExpressionParser=new SpelExpressionParser();
    private final Format format;

    private static final Map<Format, EnoMappings> mappingsByFormat=initMappings();
    private final Map<Class<?>, EnoMappingsForClass> mappingsByClass;

    private static Map<Format, EnoMappings> initMappings() {
        Map<Format, EnoMappings> mappingsByFormat=new HashMap<>();
        mappingsByFormat.put(Format.DDI, initEnoMappingsForClass(Format.DDI));
        return mappingsByFormat;
    }

    private static EnoMappings initEnoMappingsForClass(Format format) {
        Map<Class<?>, EnoMappingsForClass> mappingsByClass=new HashMap<>();

        EnoModelIterator enoModelIterator=new EnoModelIterator() {};
        enoModelIterator.stream()
                .filter(enoModelClass -> enoModelClass.isAnnotationPresent(DDIMapping.class))
                .forEach(c->addClassToDDIMappingsByClass(c, mappingsByClass));

        return new EnoMappings(format, mappingsByClass);

    }

    private static void addClassToDDIMappingsByClass(Class<? extends EnoObject> enoClass, Map<Class<?>, EnoMappingsForClass> mappingsByClass) {
        DDIMapping ddiMapping = enoClass.getAnnotation(DDIMapping.class);
        var expression= computeExpressionForDDIMapping(enoClass, ddiMapping);
        mappingsByClass.compute(ddiMapping.targetType(), (k, v) -> {
            if (v == null) {
                v = new EnoMappingsForClass(new HashMap<>(),k);
            }
            v.mappings().put(expression, enoClass);
            return v;
        });
    }

    private static Expression computeExpressionForDDIMapping(Class<? extends EnoObject> enoClass, DDIMapping ddiMapping) {
        return spelExpressionParser.parseExpression(computeParentExpressionForDDIMapping(enoClass.getSuperclass())+" && "+ddiMapping.inCondition());
    }

    private static String computeParentExpressionForDDIMapping(Class<?> classToCheck) {
        if (classToCheck !=null && EnoObject.class.isAssignableFrom(classToCheck)) {
            var ddiMappingRoute = classToCheck.getDeclaredAnnotation(DDICommonMapping.class);
            return ddiMappingRoute==null ?
                    computeParentExpressionForDDIMapping(classToCheck.getSuperclass())
                    : computeParentExpressionForDDIMapping(classToCheck.getSuperclass()) +" && "+ddiMappingRoute.value();
        }
        return "true";
    }

    public static EnoMappings forFormat(@NonNull Format format) {
        if (!mappingsByFormat.containsKey(format)) {
            throw new RuntimeException("No mappings for format " + format);
        }
        return mappingsByFormat.get(format);
    }

    public Class<? extends EnoObject> from(@NonNull Object ddiObject) {
        return findmappingsByClass(ddiObject.getClass()).from(ddiObject);
    }

    //TODO Cache this method
    private EnoMappingsForClass findmappingsByClass(Class<?> classToFind) {

        List<Class<?>> classesInstanceOf=mappingsByClass.keySet().stream()
                .filter(c->c.isAssignableFrom(classToFind))
                .toList();
        return switch (classesInstanceOf.size()) {
            case 0 -> throw new RuntimeException("No mappings for class " + classToFind);
            case 1 -> mappingsByClass.get(classesInstanceOf.get(0));
            default -> mappingsByClass.get(findClosestInClassHierarchy(classesInstanceOf, classToFind));
        };
    }

    //TODO : implement this method !!!
    private Class<?>  findClosestInClassHierarchy(List<Class<?>> classesInstanceOf, Class<?> classToFind) {
        //classHierarchy = ClassHierarchy.getClassHierarchy(classToFind);
        return classesInstanceOf.get(0);
    }
}
