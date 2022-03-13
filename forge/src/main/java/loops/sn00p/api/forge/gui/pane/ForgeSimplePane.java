package loops.sn00p.api.forge.gui.pane;

import loops.sn00p.api.forge.gui.item.EmptySlot;
import loops.sn00p.api.forge.gui.item.ForgeSimpleDisplayable;
import loops.sn00p.api.gui.item.Displayable;
import loops.sn00p.api.gui.pane.Pane;
import loops.sn00p.api.type.Pair;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 *
 * Simple implementation of the {@link Pane} interface where the height and width of the pane are unchanging.
 *
 */
public class ForgeSimplePane implements Pane {

    private final int topLeftX;
    private final int topLeftY;
    private final int width;
    private final int height;
    private final SimpleDisplayableSlot[][] items;
    private final InventoryBasic inventoryBasic;

    private boolean full = false;
    private Pair<Integer, Integer> lastPos = Pair.of(0, 0);

    private ForgeSimplePane(int topLeftX, int topLeftY, int height, int width) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.width = width;
        this.height = height;
        this.inventoryBasic = new InventoryBasic("", false, 1);
        this.items = new SimpleDisplayableSlot[height][width];

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.items[y][x] = new EmptySlot(this,x + y * 9);
            }
        }
    }

    @Override
    public void add(Displayable displayable) {
        if (this.full) {
            return;
        }

        SimpleDisplayableSlot slot;

        if (displayable == null) {
            slot = new EmptySlot(this, this.lastPos.getY() * this.lastPos.getX() * 9);
        } else {
            slot = new SimpleDisplayableSlot(this, displayable, this.lastPos.getX(), this.lastPos.getY());
        }

        this.items[this.lastPos.getY()][this.lastPos.getX()] = slot;

        if (this.width == (this.lastPos.getX() + 1)) {
            if (this.height == (this.lastPos.getY() + 1)) {
                this.full = true;
                return;
            }

            this.lastPos = Pair.of(0, this.lastPos.getY() + 1);
        } else {
            this.lastPos = Pair.of(this.lastPos.getX() + 1, this.lastPos.getY());
        }
    }

    @Override
    public void set(int posX, int posY, Displayable displayable) {
        if (posX >= (this.topLeftX + this.width)) {
            throw new RuntimeException("Cannot set an X position greater than the width");
        }

        if (posY >= (this.topLeftY + this.height)) {
            throw new RuntimeException("Cannot set an Y position greater than the height");
        }

        SimpleDisplayableSlot slot;

        if (displayable == null) {
            slot = new EmptySlot(this, posX * posY * 9);
        } else {
            slot = new SimpleDisplayableSlot(this, displayable, posX, posY);
        }

        this.items[posY][posX] = slot;
    }

    @Override
    public void set(int pos, Displayable displayable) {
        this.set(pos % (this.width), pos % (this.height), displayable);
    }

    @Override
    public void fill(Displayable displayable) {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.set(x, y, displayable);
            }
        }
    }

    @Override
    public void clear() {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.set(x, y, null);
            }
        }
    }

    public SimpleDisplayableSlot[][] getItems() {
        return this.items;
    }

    public boolean inPane(int xPos, int yPos) {
        if (xPos < this.topLeftX || yPos < this.topLeftY) {
            return false;
        }

        return yPos < (this.topLeftY + this.height) && xPos < (this.topLeftX + this.width);
    }

    public int updateIndex(int index) {
        return index + (9 * this.topLeftY) + this.topLeftX;
    }

    public Pair<Integer, Integer> convertXandY(int x, int y) {
        return Pair.of(x - this.topLeftX, y - this.topLeftY);
    }

    public static class SimpleDisplayableSlot extends Slot {

        private final Displayable displayable;
        private int clicks = 30;
        private long lastClick = -1;

        public SimpleDisplayableSlot(ForgeSimplePane pane, Displayable displayable, int xPosition, int yPosition) {
            super(pane.inventoryBasic, xPosition + yPosition * 9, pane.topLeftX + xPosition,
                    pane.topLeftY + yPosition);

            this.displayable = displayable;
        }

        public Displayable getDisplayable() {
            return this.displayable;
        }

        public int getClicks() {
            return this.clicks;
        }

        public void setClicks(int clicks) {
            this.clicks = clicks;
        }

        public boolean shouldClick() {
            if (this.lastClick == -1) {
                return true;
            }

            return (System.currentTimeMillis() - this.lastClick) > 500;
        }

        public void updateClick() {
            this.lastClick = System.currentTimeMillis();
        }

        @Override
        public ItemStack getStack() {
            return ForgeSimpleDisplayable.Converter.toNative((ForgeSimpleDisplayable) this.displayable);
        }
    }

    public static final class Builder implements Pane.Builder {

        private int topLeftX = 0;
        private int topLeftY = 0;
        private int width = 9;
        private int height = 5;

        public Builder() {}

        @Override
        public Pane.Builder topLeftX(int topLeftX) {
            this.topLeftX = topLeftX;
            return this;
        }

        @Override
        public Pane.Builder topLeftY(int topLeftY) {
            this.topLeftY = topLeftY;
            return this;
        }

        @Override
        public Pane.Builder width(int width) {
            this.width = width;
            return this;
        }

        @Override
        public Pane.Builder height(int height) {
            this.height = height;
            return this;
        }

        @Override
        public Pane build() {
            return new ForgeSimplePane(this.topLeftX, this.topLeftY, this.height, this.width);
        }
    }
}