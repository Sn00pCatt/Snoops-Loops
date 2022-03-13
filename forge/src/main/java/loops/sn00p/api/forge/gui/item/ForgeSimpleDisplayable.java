package loops.sn00p.api.forge.gui.item;

import loops.sn00p.api.gui.item.Displayable;
import loops.sn00p.api.player.SnoopPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * A static Forge implementation of the {@link Displayable} interface. Meaning the itemstack cannot be changed once initially
 * set.
 *
 */
public class ForgeSimpleDisplayable implements Displayable {

    private final ItemStack itemStack;
    private final BiConsumer<SnoopPlayer<?>, ClickType> clickHandler;
    private final Consumer<SnoopPlayer<?>> updateHandler;

    public ForgeSimpleDisplayable(ItemStack itemStack, BiConsumer<SnoopPlayer<?>, ClickType> clickHandler,
                                  Consumer<SnoopPlayer<?>> updateHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
        this.updateHandler = updateHandler;
    }

    @Override
    public void onClick(SnoopPlayer<?> player, ClickType clickType) {
        this.clickHandler.accept(player, clickType);
    }

    @Override
    public void update(SnoopPlayer<?> viewer) {
        this.updateHandler.accept(viewer);
    }

    public static final class Converter {
        public static ItemStack toNative(ForgeSimpleDisplayable displayable) {
            return displayable.itemStack;
        }
    }

    public static final class Builder implements Displayable.Builder<ItemStack> {

        private ItemStack itemStack;
        private BiConsumer<SnoopPlayer<?>, ClickType> clickHandler = (SnoopPlayer, clickType) -> {};
        private Consumer<SnoopPlayer<?>> updateHandler = SnoopPlayer -> {};

        @Override
        public Displayable.Builder<ItemStack> itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> clickHandler(BiConsumer<SnoopPlayer<?>, ClickType> clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> updateHandler(Consumer<SnoopPlayer<?>> updateHandler) {
            this.updateHandler = updateHandler;
            return this;
        }

        @Override
        public Displayable build() {
            if (this.itemStack == null) {
                throw new RuntimeException("Cannot create displayable without itemstack");
            }

            return new ForgeSimpleDisplayable(this.itemStack, this.clickHandler, this.updateHandler);
        }
    }
}
