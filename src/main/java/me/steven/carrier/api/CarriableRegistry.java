package me.steven.carrier.api;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CarriableRegistry {

    public static final CarriableRegistry INSTANCE = new CarriableRegistry();

    private final Map<Identifier, Carriable> values = new HashMap<>();

    public Carriable register(Identifier identifier, Carriable carriable) {
        values.put(identifier, carriable);
        return carriable;
    }

    public boolean contains(Identifier identifier) {
        return values.containsKey(identifier);
    }

    public Carriable get(Identifier identifier) {
        return values.get(identifier);
    }
}
