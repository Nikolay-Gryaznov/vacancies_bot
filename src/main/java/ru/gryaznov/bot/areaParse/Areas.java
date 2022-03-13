package ru.gryaznov.bot.areaParse;

import java.util.HashMap;

//class for storing data about regions key = name, value = id
public class Areas {
    HashMap<String, String> areaData = new HashMap<>();

    public HashMap<String, String> getAreaData() {
        return areaData;
    }
}
