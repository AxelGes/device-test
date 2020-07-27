package com.bluebloodapps.device_test.utils;

public enum TestType {
    SCREEN, SOUND_MAIN, SOUND_CALL, NETWORK, SENSOR_GYRO;

    public static TestType isTest(String s){
        for(TestType type : TestType.values()){
            if(s.equals(type.toString())){
                return type;
            }
        }

        return null;
    }
}
