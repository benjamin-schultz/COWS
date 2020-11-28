package com.example.cows;

public final class Constants {
    private Constants() {

    }

    public static final int NOTIFICATION_MANAGER_ID = 0;
    public static final String NOTIFICATION_CHANNEL_ID = "COWS_Reminder";

    public static final int NOTIFICATION_DEFAULT = 0;
    public static final int NOTIFICATION_WATER = 1;
    public static final int NOTIFICATION_SNOOZE = 2;

    public static final String NOTIFICATION_ACTION_NAME = "action";
    public static final String NOTIFICATION_ACTION_WATER = "water";
    public static final String NOTIFICATION_ACTION_SNOOZE = "snooze";

    public static final String HTTP_DEFAULT_IP = "0.0.0.0";
    public static final String HTTP_PORT = "1250";
    public static final String HTTP_WATER = "duration=";
    public static final String HTTP_PREFIX = "http://";

    public static final int ALARM_ID = 520;

}
