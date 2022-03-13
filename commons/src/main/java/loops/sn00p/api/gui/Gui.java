package loops.sn00p.api.gui;

import loops.sn00p.api.gui.pane.Pane;
import loops.sn00p.api.player.SnoopPlayer;
import loops.sn00p.api.player.PlayerManager;

import java.util.function.Consumer;

/**
 *
 * An interface representing chest GUIs for the platform specific implementation
 *
 */
public interface Gui {

    /**
     *
     * Opens the GUI for the given player
     *
     * @param player The player to open the GUI for
     */
    void open(SnoopPlayer<?> player);

    /**
     *
     * Gui builder interface
     *
     */
    interface Builder {

        /**
         *
         * Sets the title of the GUI
         *
         * @param title The title of the GUI
         * @return The builder
         */
        Builder title(String title);

        /**
         *
         * Sets the height of the GUI
         *
         * @param height The height of the GUI
         * @return The builder
         */
        Builder height(int height);

        /**
         *
         * Adds a {@link Pane} to the GUI
         *
         * @param pane The pane to add
         * @return The builder
         */
        Builder addPane(Pane pane);

        /**
         *
         * Sets the player manager for the GUI to reference.
         * This MUST be set so that the GUI can use the {@link SnoopPlayer} class internally
         *
         * @param playerManager the mods player manager
         * @return The builder
         */
        Builder setPlayerManager(PlayerManager<?, ?> playerManager);

        /**
         *
         * Sets the consumer for when the GUI is closed.
         *
         * @param consumer The close consumer
         * @return The builder
         */
        Builder setCloseConsumer(Consumer<SnoopPlayer<?>> consumer);

        /**
         *
         * Builds the GUI from the given specifications
         *
         * @return The new GUI
         */
        Gui build();

    }
}
