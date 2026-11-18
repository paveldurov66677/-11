package com.pulsevisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.Identifier;

public class PulseVisualsMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("pulsevisuals", "pulse_hud"),
            PulseVisualsMod::render
        );
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        int fps = mc.getFps();

        int ping = 0;
        if (mc.player != null && mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (info != null) {
                ping = info.getLatency();
            }
        }

        String text = "pulsevisuals.pro / " + ping + " ms / " + fps + " FPS";

        int paddingX = 8;
        int iconSize = 10;
        int spacing = 6;
        int boxHeight = 20;
        int boxWidth = paddingX * 2 + iconSize + spacing + mc.font.width(text);

        int x = (mc.getWindow().getGuiScaledWidth() - boxWidth) / 2;
        int y = 6;

        // Свечение и фон
        graphics.fill(x - 1, y - 1, x + boxWidth + 1, y + boxHeight + 1, 0x338A2BE2);
        graphics.fill(x, y, x + boxWidth, y + boxHeight, 0xB0000000);

        // Иконка пульса: фиолетовый квадрат
        int iconX = x + paddingX;
        int iconY = y + (boxHeight - iconSize) / 2;
        graphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0xFF8A2BE2);

        // Текст
        int textX = iconX + iconSize + spacing;
        int textY = y + (boxHeight - 8) / 2;
        graphics.drawString(mc.font, text, textX, textY, 0xFFFFFFFF, true);
    }
}
