package loops.sn00p.api.player.save.impl;

import loops.sn00p.api.player.SnoopPlayer;
import loops.sn00p.api.player.attribute.PlayerAttribute;
import loops.sn00p.api.player.save.SaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EmptySaveManager<T> implements SaveManager<T> {

    private final Map<Class<? extends PlayerAttribute<?>>, AttributeData> loadedAttributes = Maps.newConcurrentMap();

    public EmptySaveManager() {}

    @Override
    public List<PlayerAttribute<?>> loadData(SnoopPlayer<T> player) {
        if (this.loadedAttributes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PlayerAttribute<?>> attributes = Lists.newArrayList();

        for (Map.Entry<Class<? extends PlayerAttribute<?>>, AttributeData> entry : this.loadedAttributes.entrySet()) {
            AttributeData value = entry.getValue();
            PlayerAttribute<?> attribute = value.getConstructor().apply(player, value.getManager());

            attribute.preLoad();
            attribute.load();
            attribute.postLoad();
            attributes.add(attribute);
        }

        return attributes;
    }

    @Override
    public List<PlayerAttribute<?>> loadData(UUID uuid) {
        if (this.loadedAttributes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PlayerAttribute<?>> attributes = Lists.newArrayList();

        for (Map.Entry<Class<? extends PlayerAttribute<?>>, AttributeData> entry : this.loadedAttributes.entrySet()) {
            AttributeData value = entry.getValue();
            PlayerAttribute<?> attribute = value.getOfflineConstructor().apply(uuid);

            attribute.preLoad();
            attribute.load();
            attribute.postLoad();
            attributes.add(attribute);
        }

        return attributes;
    }

    @Override
    public void saveData(SnoopPlayer<T> player, PlayerAttribute<?> attribute) {
        AttributeData attributeData = this.loadedAttributes.get(attribute.getClass());

        if (attributeData == null) {
            attribute.save();
            return;
        }

        attribute.preSave();
        attribute.save();
        attribute.postSave();
    }

    @Override
    public void saveData(UUID uuid, PlayerAttribute<?> attribute) {
        AttributeData attributeData = this.loadedAttributes.get(attribute.getClass());

        if (attributeData == null) {
            attribute.save();
            return;
        }

        attribute.preSave();
        attribute.save();
        attribute.postSave();
    }

    @Override
    public void registerAttribute(Object manager, Class<? extends PlayerAttribute<?>> attribute) {
        BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> constructor = this.getConstructor(manager, attribute);
        Function<UUID, PlayerAttribute<?>> offlineConstructor = this.getOfflineConstructor(attribute);

        this.loadedAttributes.put(attribute, new AttributeData(manager, constructor, offlineConstructor));
    }

    private BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> getConstructor(Object manager, Class<? extends PlayerAttribute<?>> clazz) {
        try {
            Constructor<? extends PlayerAttribute<?>> constructor = clazz.getConstructor(
                    manager.getClass(),
                    SnoopPlayer.class
            );

            return (SnoopPlayer, o) -> {
                try {
                    return constructor.newInstance(o, SnoopPlayer);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return null;
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private Function<UUID, PlayerAttribute<?>> getOfflineConstructor(Class<? extends PlayerAttribute<?>> clazz) {
        try {
            Constructor<? extends PlayerAttribute<?>> constructor = clazz.getConstructor(UUID.class);

            return (uuid) -> {
                try {
                    return constructor.newInstance(uuid);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return null;
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class AttributeData {

        private final Object manager;
        private final BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> constructor;
        private final Function<UUID, PlayerAttribute<?>> offlineConstructor;

        public AttributeData(Object manager, BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> constructor,
                             Function<UUID, PlayerAttribute<?>> offlineConstructor) {
            this.manager = manager;
            this.constructor = constructor;
            this.offlineConstructor = offlineConstructor;
        }

        public Object getManager() {
            return this.manager;
        }

        public BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> getConstructor() {
            return this.constructor;
        }

        public Function<UUID, PlayerAttribute<?>> getOfflineConstructor() {
            return this.offlineConstructor;
        }
    }
}
