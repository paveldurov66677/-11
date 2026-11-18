package com.pulsevisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class PulseVisualsMod implements ClientModInitializer {

    private static final Identifier ICON_TEX =
        Identifier.fromNamespaceAndPath("pulsevisuals", "textures/gui/pulse_icon.png");
    private static final Style HUD_FONT = Style.EMPTY.withFont(
        new FontDescription.Resource(Identifier.fromNamespaceAndPath("pulsevisuals", "hud")));

    private static final int BG = 0xD00D0D15;
    private static final int NAME = 0xFFF1F1F8;
    private static final int SEP = 0xFF6B6B7E;
    private static final int VALUE = 0xFFFFFFFF;
    private static final int UNIT = 0xFFB9B9C9;

    // Если текст стоит чуть выше или ниже центра, меняйте это число (в пикселях GUI)
    private static final int TEXT_DY = 0;

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("pulsevisuals", "pulse_hud"),
            PulseVisualsMod::render
        );
    }

    private static Component t(String s) {
        return Component.literal(s).withStyle(HUD_FONT);
    }

    private static void render(GuiGraphics g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        int fps = mc.getFps();
        int ping = 0;
        if (mc.player != null && mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (info != null) ping = info.getLatency();
        }

        Font f = mc.font;
        Component name = t("pulsevisuals.pro");
        Component sep = t(" / ");
        Component pingText = t(String.valueOf(ping));
        Component msText = t(" ms");
        Component fpsText = t(String.valueOf(fps));
        Component fpsUnit = t(" FPS");

        int textWidth = f.width(name) + f.width(sep) + f.width(pingText) + f.width(msText)
                + f.width(sep) + f.width(fpsText) + f.width(fpsUnit);

        int padding = 9;
        int iconW = 16;
        int iconH = 12;
        int gap = 6;
        int boxHeight = 20;
        int boxWidth = padding * 2 + iconW + gap + textWidth;

        int x = (mc.getWindow().getGuiScaledWidth() - boxWidth) / 2;
        int y = 6;
        int radius = 7;

        // Мягкое круглое затемнение вокруг плашки
        int layers = 9;
        for (int i = layers; i >= 1; i--) {
            float k = 1f - (i - 1f) / layers;
            int alpha = Math.max(1, Math.round(255f * 0.05f * k));
            roundedRect(g, x - i, y - i, x + boxWidth + i, y + boxHeight + i, radius + i, alpha << 24);
        }

        // Плашка
        roundedRect(g, x, y, x + boxWidth, y + boxHeight, radius, BG);

        // Иконка пульса из PNG
        int iconX = x + padding;
        int iconY = y + (boxHeight - iconH) / 2;
        g.blit(RenderPipelines.GUI_TEXTURED, ICON_TEX, iconX, iconY, 0f, 0f, iconW, iconH, 64, 48, 64, 48);

        // Текст со шрифтом Inter
        int tx = iconX + iconW + gap;
        int ty = y + (boxHeight - 8) / 2 + TEXT_DY;
        tx = part(g, f, name, tx, ty, NAME);
        tx = part(g, f, sep, tx, ty, SEP);
        tx = part(g, f, pingText, tx, ty, VALUE);
        tx = part(g, f, msText, tx, ty, UNIT);
        tx = part(g, f, sep, tx, ty, SEP);
        tx = part(g, f, fpsText, tx, ty, VALUE);
        part(g, f, fpsUnit, tx, ty, UNIT);
    }

    private static int part(GuiGraphics g, Font f, Component c, int x, int y, int color) {
        g.drawString(f, c, x, y, color, false);
        return x + f.width(c);
    }

    private static void roundedRect(GuiGraphics g, int x1, int y1, int x2, int y2, int r, int color) {
        int maxR = Math.min((x2 - x1) / 2, (y2 - y1) / 2);
        if (r > maxR) r = maxR;
        g.fill(x1, y1 + r, x2, y2 - r, color);
        for (int dy = 0; dy < r; dy++) {
            double d = r - dy - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - d * d));
            g.fill(x1 + inset, y1 + dy, x2 - inset, y1 + dy + 1, color);
            g.fill(x1 + inset, y2 - dy - 1, x2 - inset, y2 - dy, color);
        }
    }
}
