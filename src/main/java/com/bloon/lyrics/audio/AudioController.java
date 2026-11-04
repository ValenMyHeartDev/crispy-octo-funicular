package com.bloon.lyrics.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.net.URL;

public class AudioController {
    private static Clip backgroundClip;

    public static void playAudioStream(String urlString) {
        try {
            stopAudio();
            URL url = new URL(urlString);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
            AudioFormat baseFormat = audioIn.getFormat();
            
            AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            AudioInputStream decodedAudioIn = AudioSystem.getAudioInputStream(decodeFormat, audioIn);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(decodedAudioIn);
            backgroundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAudio() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    public static long getCurrentPlaybackPosition() {
        if (backgroundClip != null && backgroundClip.isOpen()) {
            return backgroundClip.getMicrosecondPosition() / 1000;
        }
        return 0;
    }
}
