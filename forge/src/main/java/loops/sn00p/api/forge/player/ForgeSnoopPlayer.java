package loops.sn00p.api.forge.player;

import loops.sn00p.api.player.SnoopPlayer;
import loops.sn00p.api.player.attribute.PlayerAttribute;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Forge implementation of the {@link SnoopPlayer} interface
 *
 */
public class ForgeSnoopPlayer implements SnoopPlayer<EntityPlayerMP> {

    protected final Map<Class<?>, PlayerAttribute<?>> attributes = Maps.newHashMap();

    private EntityPlayerMP player;

    protected ForgeSnoopPlayer(EntityPlayerMP player) {
        this.player = player;
    }

    @Override
    public UUID getUuid() {
        return this.player.getUniqueID();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public EntityPlayerMP getParent() {
        return this.player;
    }

    public void setPlayer(EntityPlayerMP player) {
        this.player = player;
    }

    @Override
    public void message(String message) {
        this.player.sendMessage(new TextComponentString(message));
    }

    @Override
    public void message(String... messages) {
        for (String message : messages) {
            this.message(message);
        }
    }

    @Override
    public void message(List<String> messages) {
        for (String message : messages) {
            this.message(message);
        }
    }

    @Override
    public void executeCommands(String... commands) {
        for (String command : commands) {
            this.executeCommand(command);
        }
    }

    @Override
    public void executeCommand(String command) {
        FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(this.player, command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends PlayerAttribute<B>, B> A getAttribute(Class<B> plugin) {
        return (A) this.attributes.get(plugin);
    }
}
