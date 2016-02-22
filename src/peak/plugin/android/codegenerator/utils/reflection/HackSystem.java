package peak.plugin.android.codegenerator.utils.reflection;

/**
 * Created by wenping on 2016/2/21.
 */

import java.io.*;
import java.nio.channels.Channel;
import java.util.Map;
import java.util.Properties;

/**
 * 为JavaClass劫持java.lang.System提供支持
 * 除了out和err外，其余的都直接转发给System处理
 *
 * @author zzm
 */
public class HackSystem {


    public final static InputStream in = System.in;

    private static ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public final static PrintStream out = new PrintStream(buffer);

    public final static PrintStream err = out;

    public static String getBufferString() {
        return buffer.toString();
    }

    public static void clearBuffer() {
        buffer.reset();
    }

    public static void setIn(InputStream newIn) {
        System.setIn(newIn);
    }

    public static void setOut(PrintStream newOut) {
        System.setOut(newOut);
    }

    public static void setErr(PrintStream newErr) {
        System.setErr(newErr);
    }

    public static void exit(int code) {
        System.exit(code);
    }

    public static void gc() {
        System.gc();
    }

    public static String getenv(String name) {
        return System.getenv(name);
    }

    public static Map<String, String> getenv() {
        return System.getenv();
    }

    public static Channel inheritedChannel() throws IOException {
        return System.inheritedChannel();
    }

    public static String getProperty(String propertyName) {
        return System.getProperty(propertyName);
    }

    public static String clearProperty(String name) {
        return System.clearProperty(name);
    }

    public static Console console() {
        return System.console();
    }

    public static void setSecurityManager(final SecurityManager s) {
        System.setSecurityManager(s);
    }

    public static SecurityManager getSecurityManager() {
        return System.getSecurityManager();
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public static int identityHashCode(Object x) {
        return System.identityHashCode(x);
    }

    // 下面所有的方法都与java.lang.System的名称一样
    // 实现都是字节转调System的对应方法
    // 因版面原因，省略了其他方法
    public static String lineSeparator() {
        return System.lineSeparator();
    }

    public static void load(String pathName) {
        System.load(pathName);
    }

    public static void loadLibrary(String libName) {
        System.loadLibrary(libName);
    }

    public static void runFinalization() {
        System.runFinalization();
    }

    /** @deprecated */
    @Deprecated
    public static void runFinalizersOnExit(boolean flag) {
        System.runFinalizersOnExit(flag);
    }

    public static void setProperties(Properties p) {
        System.setProperties(p);
    }

    public static void setProperty(String key, String value) {
        System.setProperty(key, value);
    }
    public static String mapLibraryName(String var0){
        return System.mapLibraryName(var0);
    }

    public static long nanoTime(){
        return System.nanoTime();
    }
}
