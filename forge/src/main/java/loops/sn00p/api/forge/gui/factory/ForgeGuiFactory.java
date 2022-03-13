package loops.sn00p.api.forge.gui.factory;

import loops.sn00p.api.forge.gui.ForgeGuiBuilder;
import loops.sn00p.api.forge.gui.item.ForgeSimpleDisplayable;
import loops.sn00p.api.forge.gui.pane.ForgeSimplePane;
import loops.sn00p.api.gui.Gui;
import loops.sn00p.api.gui.factory.PlatformGuiFactory;
import loops.sn00p.api.gui.item.Displayable;
import loops.sn00p.api.gui.pane.Pane;
import loops.sn00p.api.gui.pane.type.PagedPane;
import net.minecraft.item.ItemStack;

/**
 *
 * Forge implementation of the {@link PlatformGuiFactory} interface
 *
 */
public class ForgeGuiFactory implements PlatformGuiFactory<ItemStack> {

    @Override
    public Displayable.Builder<ItemStack> displayableBuilder() {
        return new ForgeSimpleDisplayable.Builder();
    }

    @Override
    public Pane.Builder paneBuilder() {
        return new ForgeSimplePane.Builder();
    }

    @Override
    public PagedPane.Builder pagedPaneBuilder() {
        return null; //TODO: not made yet
    }

    @Override
    public Gui.Builder guiBuilder() {
        return new ForgeGuiBuilder();
    }
}
