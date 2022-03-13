package loops.sn00p.api.forge.gui.type;

import loops.sn00p.api.config.type.ConfigInterface;
import loops.sn00p.api.config.type.ConfigItem;
import loops.sn00p.api.config.type.PositionableConfigItem;
import loops.sn00p.api.forge.chat.UtilChatColour;
import loops.sn00p.api.forge.config.UtilConfigItem;
import loops.sn00p.api.forge.gui.item.PositionableItem;
import loops.sn00p.api.gui.Transformer;
import loops.sn00p.api.gui.factory.GuiFactory;
import loops.sn00p.api.gui.item.Displayable;
import loops.sn00p.api.gui.pane.Pane;
import loops.sn00p.api.player.SnoopPlayer;
import loops.sn00p.api.player.PlayerManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class TrueFalseSelectionUI {

    private static void open(Builder config) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.config.guiSettings.getHeight())
                .build();

        for (ConfigItem fillerItem : config.config.guiSettings.getFillerItems()) {
            pane.add(GuiFactory.displayable(UtilConfigItem.fromConfigItem(fillerItem, config.transformers)));
        }

        if (config.startsTrue) {
            UtilConfigItem.addConfigItem(pane, config.config.trueItem, config.transformers, (SnoopPlayer, clickType) -> {
                config.startsTrue = false;
                open(config);
            });
        } else {
            UtilConfigItem.addConfigItem(pane, config.config.falseItem, config.transformers, (SnoopPlayer, clickType) -> {
                config.startsTrue = true;
                open(config);
            });
        }

        UtilConfigItem.addConfigItem(pane, config.config.acceptItem, config.transformers, (SnoopPlayer, clickType) -> {
            config.confirm.player(SnoopPlayer);
            config.confirm.playerManager(config.playerManager);
            config.confirm.returnHandler((SnoopPlayer1, clickType1) -> open(config));
            config.confirm.transformers(config.transformers);

            if (config.startsTrue) {
                config.confirm.confirmHandler(config.trueAcceptHandler);
                config.confirm.descriptionItem(UtilConfigItem.fromConfigItem(config.config.trueItem));

                config.confirm.open();
            } else {
                config.confirm.confirmHandler(config.falseAcceptHandler);
                config.confirm.descriptionItem(UtilConfigItem.fromConfigItem(config.config.falseItem));
                config.confirm.open();
            }
        });

        UtilConfigItem.addConfigItem(pane, config.config.backButton, config.transformers, config.returnHandler);

        for (PositionableConfigItem displayItem : config.displayConfigItems) {
            UtilConfigItem.addConfigItem(pane, config.transformers, displayItem);
        }

        for (PositionableItem displayItem : config.displayItems) {
            pane.set(displayItem.getPosX(), displayItem.getPosY(), GuiFactory.displayable(displayItem.getItemStack()));
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(config.playerManager)
                .addPane(pane)
                .setCloseConsumer(SnoopPlayer -> {})
                .height(config.config.guiSettings.getHeight())
                .title(UtilChatColour.translateColourCodes('&', config.config.guiSettings.getTitle()))
                .build().open(config.player);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SnoopPlayer<?> player = null;
        private PlayerManager<?, ?> playerManager = null;
        private TrueFalseConfig config = null;
        private BiConsumer<SnoopPlayer<?>, Displayable.ClickType> returnHandler = null;
        private BiConsumer<SnoopPlayer<?>, Displayable.ClickType> trueAcceptHandler = null;
        private BiConsumer<SnoopPlayer<?>, Displayable.ClickType> falseAcceptHandler = null;
        private ConfirmationUI.Builder confirm = null;
        private boolean startsTrue = true;
        private List<PositionableConfigItem> displayConfigItems = Lists.newArrayList();
        private List<PositionableItem> displayItems = Lists.newArrayList();
        private List<Transformer> transformers = Lists.newArrayList();

        protected Builder() {}

        public Builder player(SnoopPlayer<?> player) {
            this.player = player;
            return this;
        }

        public Builder playerManager(PlayerManager<?, ?> playerManager) {
            this.playerManager = playerManager;
            return this;
        }

        public Builder config(TrueFalseConfig config) {
            this.config = config;
            return this;
        }

        public Builder returnHandler(BiConsumer<SnoopPlayer<?>, Displayable.ClickType> returnHandler) {
            this.returnHandler = returnHandler;
            return this;
        }

        public Builder trueAcceptHandler(BiConsumer<SnoopPlayer<?>, Displayable.ClickType> trueAcceptHandler) {
            this.trueAcceptHandler = trueAcceptHandler;
            return this;
        }

        public Builder falseAcceptHandler(BiConsumer<SnoopPlayer<?>, Displayable.ClickType> falseAcceptHandler) {
            this.falseAcceptHandler = falseAcceptHandler;
            return this;
        }

        public Builder startsTrue(boolean startsTrue) {
            this.startsTrue = startsTrue;
            return this;
        }

        public Builder confirm(ConfirmationUI.Builder confirm) {
            this.confirm = confirm;
            return this;
        }

        public Builder displayConfigItems(List<PositionableConfigItem> displayConfigItems) {
            this.displayConfigItems.addAll(displayConfigItems);
            return this;
        }

        public Builder displayConfigItem(PositionableConfigItem displayConfigItem) {
            this.displayConfigItems.add(displayConfigItem);
            return this;
        }

        public Builder displayConfigItems(PositionableConfigItem... displayConfigItems) {
            this.displayConfigItems.addAll(Arrays.asList(displayConfigItems));
            return this;
        }

        public Builder displayItems(List<PositionableItem> displayItems) {
            this.displayItems.addAll(displayItems);
            return this;
        }

        public Builder displayItem(PositionableItem displayItem) {
            this.displayItems.add(displayItem);
            return this;
        }

        public Builder displayItems(PositionableItem... displayItems) {
            this.displayItems.addAll(Arrays.asList(displayItems));
            return this;
        }

        public Builder transformers(List<Transformer> transformers) {
            this.transformers.addAll(transformers);
            return this;
        }

        public Builder transformer(Transformer transformer) {
            this.transformers.add(transformer);
            return this;
        }

        public Builder transformers(Transformer... transformers) {
            this.transformers.addAll(Arrays.asList(transformers));
            return this;
        }

        public void open() {
            if (this.player == null || this.playerManager == null || this.config == null ||
                    this.returnHandler == null || this.trueAcceptHandler == null || this.falseAcceptHandler == null ||
                    this.confirm == null) {
                return;
            }

            TrueFalseSelectionUI.open(this);
        }
    }

    @ConfigSerializable
    public static class TrueFalseConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "True or False", 3, "BLOCK",
                ImmutableMap.of("one", new ConfigItem(
                        "minecraft:stained_glass_pane", 1, (byte) 15, " ", Lists.newArrayList(), Maps.newHashMap()
                ))
        );

        private PositionableConfigItem trueItem;
        private PositionableConfigItem falseItem;

        private PositionableConfigItem acceptItem = new PositionableConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 5, "&a&lCONFIRM",
                Lists.newArrayList(), 6, 1, Maps.newHashMap()
        );

        private PositionableConfigItem backButton = new PositionableConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 0, 0, Maps.newHashMap()
        );

        public TrueFalseConfig(PositionableConfigItem trueItem, PositionableConfigItem falseItem) {
            this.trueItem = trueItem;
            this.falseItem = falseItem;
        }

        public TrueFalseConfig() {}

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getTrueItem() {
            return this.trueItem;
        }

        public PositionableConfigItem getFalseItem() {
            return this.falseItem;
        }

        public PositionableConfigItem getAcceptItem() {
            return this.acceptItem;
        }

        public PositionableConfigItem getBackButton() {
            return this.backButton;
        }
    }
}
