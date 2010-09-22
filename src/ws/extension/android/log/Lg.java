package ws.extension.android.log;

public abstract class Lg {
    private static String className = Lg.class.getName();
    private static String tag;

    public static void setTag(String tag) { Lg.tag = tag;}

    private static String callSite() {
        boolean inside = true;
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if (inside) {
                if (frame.getClassName().equals(className)) inside = false;
            } else {
                if (!frame.getClassName().equals(className)) {
                    return frame.getClassName() + '.' +
                           frame.getMethodName() + ':' +
                           frame.getLineNumber();
                }
            }
        }
        return "[toplevel]";
    }

    private static String defaultTag() {
        return tag == null ? callSite() : tag;
    }

    public static void e(Throwable th) {
        android.util.Log.e(defaultTag(),th.getMessage(),th);
    }

    public static void e(String msg, Throwable th) {
        android.util.Log.e(defaultTag(),msg,th);
    }

    public static void w(String msg) {
        android.util.Log.w(defaultTag(),msg);
    }

    public static void i(String msg) {
        android.util.Log.i(defaultTag(),msg);
    }

    public static void d(String msg) {
        android.util.Log.d(defaultTag(),msg);
    }

    public static void tr(String msg) {
        android.util.Log.d(callSite(),msg);
    }

    public static void tr() {
        tr("TRACE");
    }
}
