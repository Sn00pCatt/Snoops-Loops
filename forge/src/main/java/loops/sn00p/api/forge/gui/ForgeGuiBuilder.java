package loops.sn00p.api.forge.gui;

import loops.sn00p.api.forge.player.ForgeSnoopPlayer;
import loops.sn00p.api.gui.Gui;
import loops.sn00p.api.gui.pane.Pane;
import loops.sn00p.api.player.SnoopPlayer;
import loops.sn00p.api.player.PlayerManager;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Builder implementation for the ForgeGui
 *
 */
public class ForgeGuiBuilder implements Gui.Builder {

    private String title;
    private int height = 5;
    private List<Pane> panes = Lists.newArrayList();
    private PlayerManager<ForgeSnoopPlayer, EntityPlayerMP> playerManager;
    private Consumer<SnoopPlayer<?>> closeConsumer = null;

    @Override
    public Gui.Builder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Gui.Builder height(int height) {
        this.height = height;
        return this;
    }

    @Override
    public Gui.Builder addPane(Pane pane) {
        this.panes.add(pane);
        return this;
    }

    @Override
    public Gui.Builder setPlayerManager(PlayerManager<?, ?> playerManager) {
        this.playerManager = (PlayerManager<ForgeSnoopPlayer, EntityPlayerMP>) playerManager;
        return this;
    }

    @Override
    public Gui.Builder setCloseConsumer(Consumer<SnoopPlayer<?>> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    @Override
    public Gui build() {
        if (this.playerManager == null) {
            throw new RuntimeException("Cannot build GUI without PlayerManager being set");
        }

        return new ForgeGui(this.title, this.height, this.playerManager,
                forgeSnoopPlayer -> this.closeConsumer.accept(forgeSnoopPlayer), this.panes.toArray(new Pane[0]));
    }
}
