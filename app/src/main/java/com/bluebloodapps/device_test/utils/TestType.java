package com.bluebloodapps.device_test.utils;

public enum TestType {
    SCREEN, BATTERY, LOCAL_STORAGE, EXTERNAL_STORAGE, SOUND_MAIN, SOUND_CALL, WIFI, MOBILE, BUTTONS_HARDWARE, SENSOR_GYRO, CHARGER, PROXIMITY, MICROPHONE, GPS, BLUETOOTH;

    public static TestType isTest(String s){
        for(TestType type : TestType.values()){
            if(s.equals(type.toString())){
                return type;
            }
        }

        return null;
    }
}
