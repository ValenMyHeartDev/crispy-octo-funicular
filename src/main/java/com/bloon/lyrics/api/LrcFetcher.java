package com.bloon.lyrics.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class LrcFetcher {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static TreeMap<Long, String> fetchSyncedLyrics(String trackName, String artistName) {
        TreeMap<Long, String> lyricsMap = new TreeMap<>();
        try {
            String query = String.format("https://lrclib.net/api/search?track_name=%s&artist_name=%s",
                    URLEncoder.encode(trackName, StandardCharsets.UTF_8),
                    URLEncoder.encode(artistName, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(query))
                    .header("User-Agent", "BloonLyricsMod/1.0 (Minecraft Mod)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonArray results = JsonParser.parseString(response.body()).getAsJsonArray();
                if (!results.isEmpty()) {
                    JsonObject firstResult = results.get(0).getAsJsonObject();
                    if (firstResult.has("syncedLyrics") && !firstResult.get("syncedLyrics").isJsonNull()) {
                        String syncedLrc = firstResult.get("syncedLyrics").getAsString();
                        parseLrc(syncedLrc, lyricsMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lyricsMap;
    }

    private static void parseLrc(String lrcContent, TreeMap<Long, String> map) {
        String[] lines = lrcContent.split("\n");
        for (String line : lines) {
            if (line.startsWith("[")) {
                int closeBracket = line.indexOf(']');
                if (closeBracket > 0) {
                    String timeStr = line.substring(1, closeBracket);
                    String text = line.substring(closeBracket + 1).trim();
                    long timestamp = parseTimestamp(timeStr);
                    if (timestamp >= 0) {
                        map.put(timestamp, text);
                    }
                }
            }
        }
    }

    private static long parseTimestamp(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            long minutes = Long.parseLong(parts[0]);
            String[] secParts = parts[1].split("\\.");
            long seconds = Long.parseLong(secParts[0]);
            long millis = Long.parseLong(secParts[1]) * (secParts[1].length() == 2 ? 10 : 1);
            return (minutes * 60 * 1000) + (seconds * 1000) + millis;
        } catch (Exception e) {
            return -1;
        }
    }
}
