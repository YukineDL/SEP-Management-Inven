package com.inventorymanagement.utils;

import java.util.Arrays;
import java.util.Locale;

public class Utils {
    public static String createCode(String name){
        String[] nameSplit = name.split(" ");
        return String.join("-",
                Arrays.stream(nameSplit).map(item -> item.toUpperCase(Locale.ROOT)).toList());
    }
}
