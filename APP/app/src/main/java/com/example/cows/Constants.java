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

    public static final String WIFI_AP_SSID = "COWS";
    public static final String WIFI_AP_PASS = "cowsgomoo";

    public static final String HTTP_COWS_HOSTNAME = "cows-for-yy.duckdns.org";
    public static final String HTTP_AP_ADDRESS = "http://192.168.5.20/";
    public static final String HTTP_IP_OCT = ".250";
    public static final String HTTP_DEFAULT_IP = "192.168.1.250";
    public static final String HTTP_PORT = "1250";
    public static final String HTTP_WATER = "duration=";
    public static final String HTTP_PREFIX = "http://";

    public static final int ALARM_ID = 520;

    public static final String DURATION_DEFAULT = "5";
    public static final int REMINDER_INTERVAL_DEFAULT = 1;
    public static final long REMINDER_TIME_DEFAULT = 0;

    public static final String PREF_FILE = "preferences";
    public static final String PREF_IP = "ip_address";
    public static final String PREF_DURATION = "duration";
    public static final String PREF_REMINDER_INTERVAL = "reminder_interval";
    public static final String PREF_REMINDER_TIME = "reminder_time";

    public static final String DATE_FORMAT = "h:mm a";

}
