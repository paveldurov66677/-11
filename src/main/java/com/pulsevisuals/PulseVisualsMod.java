package com.pulsevisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

public class PulseVisualsMod implements ClientModInitializer {
    // Путь к иконке пульса в ресурсах мода: assets/pulsevisuals/textures/gui/pulse_icon.png
    private static final Identifier PULSE_ICON = Identifier.of("pulsevisuals", "textures/gui/pulse_icon.png");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((DrawContext context, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            // Настоящий FPS
            int fps = client.getCurrentFps();

            // Настоящий пинг (ms)
            int ping = 0;
            if (client.player != null && client.getNetworkHandler() != null) {
                PlayerListEntry playerEntry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if (playerEntry != null) {
                    ping = playerEntry.getLatency();
                }
            }

            String text = "pulsevisuals.pro / " + ping + " ms / " + fps + " FPS";
            TextRenderer textRenderer = client.textRenderer;

            int textWidth = textRenderer.getWidth(text);
            int paddingX = 8;
            int iconSize = 10;
            int spacing = 6;

            // Размеры плашки
            int boxWidth = paddingX * 2 + iconSize + spacing + textWidth;
            int boxHeight = 20;

            // Позиция по центру сверху
            int screenWidth = client.getWindow().getScaledWidth();
            int x = (screenWidth - boxWidth) / 2;
            int y = 6;

            // 1. Полупрозрачный черный фон (#B0000000)
            context.fill(x, y, x + boxWidth, y + boxHeight, 0xB0000000);

            // 2. Фиолетовое свечение (тонкая рамка вокруг)
            context.fill(x - 1, y - 1, x + boxWidth + 1, y + boxHeight + 1, 0x338A2BE2);

            // 3. Отрисовка иконки пульса
            int iconX = x + paddingX;
            int iconY = y + (boxHeight - iconSize) / 2;
            context.drawTexture(PULSE_ICON, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);

            // 4. Отрисовка текста
            int textX = iconX + iconSize + spacing;
            int textY = y + (boxHeight - 8) / 2;
            context.drawText(textRenderer, text, textX, textY, 0xFFFFFFFF, true);
        });
    }
}
