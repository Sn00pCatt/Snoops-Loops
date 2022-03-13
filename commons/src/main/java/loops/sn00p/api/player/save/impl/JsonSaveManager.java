package loops.sn00p.api.player.save.impl;

import loops.sn00p.api.player.SnoopPlayer;
import loops.sn00p.api.player.attribute.PlayerAttribute;
import loops.sn00p.api.player.save.SaveManager;
import loops.sn00p.api.player.save.attribute.DataDirectory;
import loops.sn00p.api.player.save.attribute.TypeAdapter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class JsonSaveManager<T> implements SaveManager<T> {

    private static GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting();

    private static Gson gson = null;

    private final Map<Class<? extends PlayerAttribute<?>>, AttributeData> loadedAttributes = Maps.newHashMap();

    public JsonSaveManager() {}

    public static Gson getGson() {
        if (gson == null) {
            gson = builder.create();
        }

        return gson;
    }

    @Override
    public List<PlayerAttribute<?>> loadData(SnoopPlayer<T> player) {
        if (this.loadedAttributes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PlayerAttribute<?>> attributes = Lists.newArrayList();

        for (Map.Entry<Class<? extends PlayerAttribute<?>>, AttributeData> entry : this.loadedAttributes.entrySet()) {
            AttributeData data = entry.getValue();
            PlayerAttribute<?> attribute = data.getConstructor().apply(player, data.getManager());

            attribute.preLoad();

            File file = Paths.get(data.getDataDirectory(), player.getUuid().toString() + ".json").toFile();

            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    Files.createFile(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                attribute.postLoad();
                attributes.add(attribute);
                continue;
            }

            try (FileReader fileWriter = new FileReader(file)) {
                PlayerAttribute<?> loaded = getGson().fromJson(new JsonReader(fileWriter), entry.getKey());

                if (loaded == null) {
                    attribute.postLoad();
                    attributes.add(attribute);
                    continue;
                }

                for (Field declaredField : entry.getKey().getDeclaredFields()) {
                    if (Modifier.isTransient(declaredField.getModifiers()) || Modifier.isFinal(declaredField.getModifiers())) {
                        continue;
                    }

                    declaredField.setAccessible(true);
                    declaredField.set(attribute, declaredField.get(loaded));
                }
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }

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
            AttributeData data = entry.getValue();
            File file = Paths.get(data.getDataDirectory(), uuid.toString() + ".json").toFile();

            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    Files.createFile(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            try (FileReader fileWriter = new FileReader(file)) {
                attributes.add(getGson().fromJson(new JsonReader(fileWriter), entry.getKey()));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        File file = Paths.get(attributeData.getDataDirectory(), player.getUuid().toString() + ".json").toFile();

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            getGson().toJson(attribute, attribute.getClass(), new JsonWriter(fileWriter));
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        File file = Paths.get(attributeData.getDataDirectory(), uuid.toString() + ".json").toFile();

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            getGson().toJson(attribute, attribute.getClass(), new JsonWriter(fileWriter));
        } catch (IOException e) {
            e.printStackTrace();
        }

        attribute.postSave();
    }

    @Override
    public void registerAttribute(Object manager, Class<? extends PlayerAttribute<?>> attribute) {
        DataDirectory dataDirectory = attribute.getAnnotation(DataDirectory.class);

        if (dataDirectory == null) {
            return;
        }

        TypeAdapter typeAdapter = attribute.getAnnotation(TypeAdapter.class);

        if (typeAdapter != null) {
            try {
                builder.registerTypeAdapter(attribute, typeAdapter.value().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> constructor = this.getConstructor(manager, attribute);

        if (constructor == null) {
            return;
        }

        this.loadedAttributes.put(attribute, new AttributeData(dataDirectory.value(), manager, constructor));
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

    public static class AttributeData {

        private final String dataDirectory;
        private final Object manager;
        private final BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> constructor;

        public AttributeData(String dataDirectory, Object manager, BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> constructor) {
            this.dataDirectory = dataDirectory;
            this.manager = manager;
            this.constructor = constructor;
        }

        public String getDataDirectory() {
            return this.dataDirectory;
        }

        public Object getManager() {
            return this.manager;
        }

        public BiFunction<SnoopPlayer<?>, Object, PlayerAttribute<?>> getConstructor() {
            return this.constructor;
        }
    }
}