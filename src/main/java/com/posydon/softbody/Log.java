package com.posydon.softbody;

public class Log {
    public static void log(String prefix, String data) {
        System.out.println(prefix + " " + data);
    }

    public static void info(String data) {
        log("[+]", data);
    }

    public static void err(String data) {
        log("[-]", data);
    }
}
