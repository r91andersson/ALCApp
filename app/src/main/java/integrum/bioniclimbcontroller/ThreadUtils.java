package integrum.bioniclimbcontroller;

import android.util.Log;

/**
 * Created by Robin on 2016-10-01.
 */
public class ThreadUtils {
        public static long getThreadId() {
            Thread t = Thread.currentThread();
            return t.getId();
        }

        public static String getThreadSignature() {
            Thread t = Thread.currentThread();
            long l = t.getId();
            String name = t.getName();
            long p = t.getPriority();
            String gname = t.getThreadGroup().getName();
            return (name
                    + ":(id)" + l
                    + ":(priority)" + p
                    + ":(group)" + gname);
        }

        public static void logThreadSignature() {
            Log.d("ThreadUtils", getThreadSignature());
        }

        public static void sleepForInSecs(int secs) {
            try {
                Thread.sleep(secs * 1000);
            } catch (InterruptedException x) {
                throw new RuntimeException("interrupted", x);
            }
        }

    public String getThreadName(){
        Thread t = Thread.currentThread();
        return t.getName();
    }

    public Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }

    }

