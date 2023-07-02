package fr.insee.eno.core.converter.out;

import reusable33.AbstractIdentifiableType;

import java.lang.reflect.InvocationTargetException;

public class DDIOutConverter {

    /** Public class while this is at "proof of concept" stage.
     * To be put private later on. */
    public AbstractIdentifiableType newDDIObjectInstance(Class<? extends AbstractIdentifiableType> ddiClass)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object factory = ddiClass.getDeclaredField("Factory").get(null);
        return (AbstractIdentifiableType) factory.getClass().getMethod("newInstance").invoke(factory);
    }

}
