package com.inventorymanagement.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

public class Utils {
    public static String createCode(String name){
        String[] nameSplit = name.split(" ");
        return String.join("-",
                Arrays.stream(nameSplit).map(item -> item.toUpperCase(Locale.ROOT)).toList());
    }
    public static String convertToCode(String name){
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", ""); // Remove non-spacing marks (accents)
        return createCode(withoutAccents);
    }

}
