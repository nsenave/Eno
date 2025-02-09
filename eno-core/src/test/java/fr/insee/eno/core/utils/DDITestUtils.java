package fr.insee.eno.core.utils;

import reusable33.AbstractIdentifiableType;
import reusable33.IDType;
import reusable33.ReferenceType;

public class DDITestUtils {

    public static void setId(AbstractIdentifiableType ddiObject, String id) {
        ddiObject.getIDList().add(IDType.Factory.newInstance());
        ddiObject.getIDArray(0).setStringValue(id);
    }

    public static void setId(ReferenceType ddiReferenceObject, String id) {
        ddiReferenceObject.getIDList().add(IDType.Factory.newInstance());
        ddiReferenceObject.getIDArray(0).setStringValue(id);
    }

}
