package loops.sn00p.api.forge.player;

import loops.sn00p.api.json.JsonUsernameCache;

import java.util.UUID;

public class UsernameProxy {

    private static JsonUsernameCache usernameCache;

    public static JsonUsernameCache getUsernameCache() {
        return usernameCache;
    }

    public static void setUsernameCache(JsonUsernameCache usernameCache) {
        UsernameProxy.usernameCache = usernameCache;
    }

    public static void addCache(UUID uuid, String name) {
        usernameCache.addCache(uuid, name);
    }

    public static UUID getUUID(String name) {
        return usernameCache.getUUID(name);
    }

    public static void invalidateUUID(UUID uuid) {
        usernameCache.invalidateUUID(uuid);
    }

    public static void setSaving(boolean saving) {
        usernameCache.setSaving(saving);
    }

    public static void save() {
        usernameCache.save();
    }
}
