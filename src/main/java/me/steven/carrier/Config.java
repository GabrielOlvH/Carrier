package me.steven.carrier;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private ListType type = ListType.BLACKLIST;
    private List<String> list = new ArrayList<>();

    public Config() {}

    public List<String> getList() {
        return list;
    }

    public ListType getType() {
        return type;
    }

    public enum ListType {
        WHITELIST, BLACKLIST
    }
}
