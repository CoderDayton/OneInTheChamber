package com.dayton.oneinthechamber.utils;

import java.util.Comparator;
import java.util.Map;

public class OrderedMap implements Comparator<String> {

    private Map<String, Integer> map;

    public OrderedMap(Map<String, Integer> map) {
        this.map = map;
    }

    public int compare(String a, String b) {
        return map.get(a) >= map.get(b) ? -1 : 1;
    }

}
