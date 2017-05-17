package org.timepedia.exporter.client;

import java.util.Date;

import org.timepedia.exporter.client.ExporterBaseActual.JsArrayObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

/**
 * Holds utility methods and wrapper state
 *
 * @author Ray Cromwell
 */
public class ExporterUtil {

    public static abstract class ExportAll implements Exportable, Exporter {
        @Override
        public void export() {
            export(false);
        }

        public abstract void export(boolean all);
    }

    private static ExporterBaseImpl impl = GWT.create(ExporterBaseImpl.class);
    static {
    }

    public static JavaScriptObject declarePackage(final String qualifiedExportName) {
        return impl.declarePackage(qualifiedExportName);
    }

    /**
     * Automatically export all instantiable and public classes marked with the
     * Exportable interface.
     * 
     * @param all,
     *            when this parameter is true it will export additionally all
     *            non-instantiable classes marked with Exportable
     */
    public static void export(final boolean all) {
        final ExportAll export = GWT.create(ExportAll.class);
        export.export(all);
    }

    /**
     * Export all classes marked with the Exportable interface, including those
     * which are not default instantiable (interfaces, abstracts, without
     * constructor, etc).
     * 
     * Use ExporterUtil.export(false) to export just the set of instantiable
     * classes and save some js size, gwt-exporter will take care of exporting
     * dependent classes when used.
     * 
     */
    public static void exportAll() {
        export(true);
    }

    // public static void exportAllAsync() {
    // GWT.runAsync(new RunAsyncCallback() {
    // public void onFailure(Throwable reason) {
    // throw new RuntimeException(reason);
    // }
    //
    // public void onSuccess() {
    // GWT.create(ExportAll.class);
    // onexport();
    // }
    //
    // private native void onexport() /*-{
    // $wnd.onexport();
    // }-*/;
    // });
    // }

    public static JavaScriptObject runDispatch(final Object instance, final Class clazz, final int meth,
            final JsArray<JavaScriptObject> arguments, final boolean isStatic, final boolean isVarArgs) {
        return impl.runDispatch(instance, clazz, meth, arguments, isStatic, isVarArgs);
    }

    public static native byte getStructuralFieldbyte(JavaScriptObject jso, String field) /*-{
                                                                                         return jso[field];
                                                                                         }-*/;

    public static native char getStructuralFieldchar(JavaScriptObject jso, String field) /*-{
                                                                                         return jso[field];
                                                                                         }-*/;

    public static native double getStructuralFielddouble(JavaScriptObject jso, String field) /*-{
                                                                                             return jso[field];
                                                                                             }-*/;

    public static native float getStructuralFieldfloat(JavaScriptObject jso, String field) /*-{
                                                                                           return jso[field];
                                                                                           }-*/;

    public static native int getStructuralFieldint(JavaScriptObject jso, String field) /*-{
                                                                                       return jso[field];
                                                                                       }-*/;

    public static long getStructuralFieldlong(final JavaScriptObject jso, final String field) {
        return (long) getStructuralFielddouble(jso, field);
    }

    public static native <T> T getStructuralFieldObject(JavaScriptObject jso, String field) /*-{
                                                                                            return jso[field];
                                                                                            }-*/;

    public static native short getStructuralFieldshort(JavaScriptObject jso, String field) /*-{
                                                                                           return jso[field];
                                                                                           }-*/;

    public static void registerDispatchMap(final Class clazz, final JavaScriptObject dispMap, final boolean isStatic) {
        impl.registerDispatchMap(clazz, dispMap, isStatic);
    }

    public static native void setStructuralField(JavaScriptObject jso, String field, Object val) /*-{
                                                                                                 jso[field]=type;
                                                                                                 }-*/;

    public static void setWrapper(final Object instance, final JavaScriptObject wrapper) {
        impl.setWrapper(instance, wrapper);
    }

    public static JavaScriptObject typeConstructor(final Exportable type) {
        return impl.typeConstructor(type);
    }

    public static JavaScriptObject typeConstructor(final String type) {
        return impl.typeConstructor(type);
    }

    public static native final JavaScriptObject getBoolean(boolean value)/*-{  return value; }-*/;

    public static JavaScriptObject wrap(final Object type) {
        if (type instanceof Boolean) {
            if (type.equals(true)) {
                return getBoolean(true);
            } else {
                return getBoolean(false);
            }
        }
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final JavaScriptObject[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final Object[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final Exportable[] type) {
        return wrap((Object[]) type);
    }

    public static JavaScriptObject wrap(final String[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final double[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final float[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final int[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final char[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final byte[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final long[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final short[] type) {
        return impl.wrap(type);
    }

    public static JavaScriptObject wrap(final Date[] type) {
        return impl.wrap(type);
    }

    public static String[] toArrString(final JavaScriptObject type) {
        return impl.toArrString(type.<JsArrayString>cast());
    }

    public static double[] toArrDouble(final JavaScriptObject type) {
        return impl.toArrDouble(type.<JsArrayNumber>cast());
    }

    public static float[] toArrFloat(final JavaScriptObject type) {
        return impl.toArrFloat(type.<JsArrayNumber>cast());
    }

    public static int[] toArrInt(final JavaScriptObject type) {
        return impl.toArrInt(type.<JsArrayNumber>cast());
    }

    public static byte[] toArrByte(final JavaScriptObject type) {
        return impl.toArrByte(type.<JsArrayNumber>cast());
    }

    public static char[] toArrChar(final JavaScriptObject type) {
        return impl.toArrChar(type.<JsArrayNumber>cast());
    }

    public static long[] toArrLong(final JavaScriptObject type) {
        return impl.toArrLong(type.<JsArrayNumber>cast());
    }

    public static <T> T[] toArrObject(final JavaScriptObject type, final T[] ret) {
        return impl.toArrObject(type, ret);
    }

    public static Object[] toArrJsObject(final JavaScriptObject type) {
        return impl.toArrJsObject(type);
    }

    public static Date[] toArrDate(final JavaScriptObject type) {
        return impl.toArrDate(type);
    }

    // Although in Compiled mode we could cast an Exportable[] to any other T[]
    // array
    // In hosted mode it is not possible, so we only support Exportable[]
    // parameter
    public static Exportable[] toArrExport(final JavaScriptObject type) {
        return impl.toArrExport(type);
    }

    public static JavaScriptObject unshift(final Object o, final JavaScriptObject arr) {
        return impl.unshift(o, arr);
    }

    public static JavaScriptObject dateToJsDate(final Date d) {
        return impl.dateToJsDate(d);
    }

    public static Date jsDateToDate(final JavaScriptObject jd) {
        return impl.jsDateToDate(jd);
    }

    public static int length(final JavaScriptObject o) {
        return o.<JsArrayObject>cast().length();
    }

    public static Object gwtInstance(final Object o) {
        return impl.gwtInstance(o);
    }

    public static <T extends Exporter> void addExporter(final Class<?> c, final T o) {
        impl.addExporter(c, o);
    }

}
