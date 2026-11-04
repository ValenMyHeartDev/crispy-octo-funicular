package com.bloon.lyrics.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Map;
import java.util.TreeMap;

public class LyricsHudOverlay implements HudRenderCallback {
    private static String currentLyricsLine = "Bloon Lyrics - Conectando a Spotify / YouTube...";
    private static TreeMap<Long, String> activeLyrics = new TreeMap<>();
    private static long songStartTime = 0;

    public static void setTrack(String track, String artist, TreeMap<Long, String> lyrics) {
        activeLyrics = lyrics;
        songStartTime = System.currentTimeMillis();
        if (lyrics.isEmpty()) {
            currentLyricsLine = track + " - " + artist + " (Letra no encontrada)";
        }
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options.hudHidden) return;

        if (!activeLyrics.isEmpty()) {
            long currentOffset = System.currentTimeMillis() - songStartTime;
            Map.Entry<Long, String> entry = activeLyrics.floorEntry(currentOffset);
            if (entry != null) {
                currentLyricsLine = entry.getValue();
            }
        }

        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();

        int textWidth = client.textRenderer.getWidth(currentLyricsLine);
        int x = (width - textWidth) / 2;
        int y = height - 55;

        drawContext.drawTextWithShadow(client.textRenderer, currentLyricsLine, x, y, 0x00FF88);
    }
}
