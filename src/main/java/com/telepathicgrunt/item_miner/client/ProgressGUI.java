package com.telepathicgrunt.item_miner.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ProgressGUI {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");

    // Copied from BossOverlayGUI. I never don gui rendering so lets see if this works.
    public static void HUDRenderEvent(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bind(GUI_BARS_LOCATION);

            // Reads packet from server to know what progress the hunted is at to display
            int level = ItemMinerClient.currentLevelToDisplay;
            int progress = ItemMinerClient.currentProgressToDisplay;
            int maxProgress = ItemMinerClient.currentMaxProgressToDisplay;

            ITextComponent textToRender;
            if(maxProgress == -1) {
                textToRender = new TranslationTextComponent("item_miner.max_progress", level);
            }
            else {
                textToRender = new TranslationTextComponent("item_miner.progress", level, progress, maxProgress);
            }
            int fontWidth = Minecraft.getInstance().font.width(textToRender);
            int guiScaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int barStartX = guiScaledWidth / 2 - 91;
            int center = guiScaledWidth / 2 - fontWidth / 2;
            drawBar(event.getMatrixStack(), barStartX, 12, progress, maxProgress);
            Minecraft.getInstance().font.draw(event.getMatrixStack(), textToRender, (float)center, 3f, 16777215);
            RenderSystem.enableBlend();
        }
    }

    private static void drawBar(MatrixStack matrixStack, int x, int y, int progress, int maxProgress) {
        blit(matrixStack, x, y, 0, BossInfo.Color.BLUE.ordinal() * 5 * 2, 182, 5);
        int scaledProgess = (int)((progress / (float)maxProgress) * 183.0F);
        if (scaledProgess > 0) {
            blit(matrixStack, x, y, 0, BossInfo.Color.BLUE.ordinal() * 5 * 2 + 5, scaledProgess, 5);
        }
    }

    private static void blit(MatrixStack matrixStack, int i, int i1, int i2, int i3, int i4, int i5) {
        AbstractGui.blit(matrixStack, i, i1, 4, (float)i2, (float)i3, i4, i5, 256, 256);
    }
}
