package me.steven.carrier.api;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CarriableRegistry {

    public static final CarriableRegistry INSTANCE = new CarriableRegistry();

    private final Map<Identifier, Carriable<?>> values = new HashMap<>();
    private final Map<Object, Carriable<?>> parents = new HashMap<>();

    public <T> Carriable<T> register(Identifier identifier, Carriable<T> carriable) {
        values.put(identifier, carriable);
        parents.put(carriable.getParent(), carriable);
        return carriable;
    }

    public boolean contains(Identifier identifier) {
        return values.containsKey(identifier);
    }

    public boolean contains(Object object) {
        return parents.containsKey(object);
    }

    public <T> Carriable<T> get(Identifier identifier) {
        return (Carriable<T>) values.get(identifier);
    }

    public <T> Carriable<T> get(T obj) {
        return (Carriable<T>) parents.get(obj);
    }
}
