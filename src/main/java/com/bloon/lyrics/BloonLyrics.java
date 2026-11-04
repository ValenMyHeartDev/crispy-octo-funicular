package com.bloon.lyrics;

import com.bloon.lyrics.hud.LyricsHudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class BloonLyrics implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Registrar el overlay del HUD
        HudRenderCallback.EVENT.register(new LyricsHudOverlay());
        System.out.println("Bloon Lyrics cargado correctamente en Minecraft 1.21.11");
    }
}
