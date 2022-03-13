package loops.sn00p.api.forge.command.completion.player;

import loops.sn00p.api.command.injector.TabCompleter;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.annotation.Annotation;
import java.util.List;

public class PlayerTabCompleter implements TabCompleter<String, EntityPlayerMP> {

    @Override
    public Class<EntityPlayerMP> getSenderClass() {
        return EntityPlayerMP.class;
    }

    @Override
    public Class<String> getCompletedClass() {
        return String.class;
    }

    @Override
    public List<String> getCompletions(EntityPlayerMP sender, String[] currentData, Annotation... completionData) {
        List<String> playerNames = Lists.newArrayList();

        for (EntityPlayerMP player :
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if (completionData.length < 1 || completionData[0] instanceof ExcludeSelfCompletion) {
                if (player.getName().equals(sender.getName())) {
                    continue;
                }
            }

            playerNames.add(player.getName());
        }

        return playerNames;
    }
}
