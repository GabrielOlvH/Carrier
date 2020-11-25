package me.steven.carrier.api;

import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;

public class CarriableRegistry {

    public static final CarriableRegistry INSTANCE = new CarriableRegistry();

    private final HashBiMap<Identifier, Carriable<?>> idToEntry = HashBiMap.create();
    private final HashBiMap<Object, Carriable<?>> objToEntry = HashBiMap.create();

    public <T> Carriable<T> register(Identifier identifier, Carriable<T> carriable) {
        idToEntry.put(identifier, carriable);
        objToEntry.put(carriable.getParent(), carriable);
        return carriable;
    }

    public boolean contains(Identifier identifier) {
        return idToEntry.containsKey(identifier);
    }

    public boolean contains(Object object) {
        return objToEntry.containsKey(object);
    }

    @SuppressWarnings("unchecked")
    public <T> Carriable<T> get(Identifier identifier) {
        return (Carriable<T>) idToEntry.get(identifier);
    }

    @SuppressWarnings("unchecked")
    public <T> Carriable<T> get(T obj) {
        return (Carriable<T>) objToEntry.get(obj);
    }

    public <T> Identifier getId(Carriable<T> carriable) {
        return idToEntry.inverse().get(carriable);
    }

    public <T> Object getObject(Carriable<T> carriable) {
        return objToEntry.inverse().get(carriable);
    }
}