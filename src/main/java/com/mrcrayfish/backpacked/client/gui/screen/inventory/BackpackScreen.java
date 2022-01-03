package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModList;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class BackpackScreen extends AbstractContainerScreen<BackpackContainerMenu>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final Component CUSTOMISE_TOOLTIP = new TranslatableComponent("backpacked.button.customise.tooltip");
    private static final Component CONFIG_TOOLTIP = new TranslatableComponent("backpacked.button.config.tooltip");

    private final int rows;
    private boolean opened;

    public BackpackScreen(BackpackContainerMenu backpackContainerMenu, Inventory playerInventory, Component titleIn)
    {
        super(backpackContainerMenu, playerInventory, titleIn);
        this.rows = backpackContainerMenu.getRows();
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
    }

    @Override
    public void init()
    {
        super.init();
        if(!this.opened)
        {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            this.opened = true;
        }
        int titleWidth = minecraft.font.width(this.title);
        this.addRenderableWidget(new MiniButton(this.leftPos + titleWidth + 8 + 3, this.topPos + 5, 225, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
            Network.getPlayChannel().sendToServer(new MessageRequestCustomisation());
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, CUSTOMISE_TOOLTIP, mouseX, mouseY);
        }));
        this.addRenderableWidget(new MiniButton(this.leftPos + titleWidth + 8 + 3 + 13, this.topPos + 5, 235, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
            this.openConfigScreen();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, CONFIG_TOOLTIP, mouseX, mouseY);
        }));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack); //Draw background
        super.render(matrixStack, mouseX, mouseY, partialTicks); //Super
        this.renderTooltip(matrixStack, mouseX, mouseY); //Render hovered tooltips

        this.children().forEach(widget ->
        {
            if(widget instanceof Button button && button.isHoveredOrFocused())
            {
                button.renderToolTip(matrixStack, mouseX, mouseY);
            }
        });
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.rows * 18 + 17);
        this.blit(matrixStack, this.leftPos, this.topPos + this.rows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    /*@Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY)
    {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 0x404040);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float) (this.imageHeight - 96 + 2), 0x404040);
    }*/

    private void openConfigScreen()
    {
        ModList.get().getModContainerById(Reference.MOD_ID).ifPresent(container ->
        {
            Screen screen = container.getCustomExtension(ConfigGuiHandler.ConfigGuiFactory.class).map(function -> function.screenFunction().apply(this.minecraft, null)).orElse(null);
            if(screen != null)
            {
                this.minecraft.setScreen(screen);
            }
            else if(this.minecraft != null && this.minecraft.player != null)
            {
                TextComponent modName = new TextComponent("Configured");
                modName.setStyle(modName.getStyle()
                        .withColor(ChatFormatting.YELLOW)
                        .withUnderlined(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("backpacked.chat.open_curseforge_page")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured")));
                Component message = new TranslatableComponent("backpacked.chat.install_configured", modName);
                this.minecraft.player.displayClientMessage(message, false);
            }
        });
    }
}
