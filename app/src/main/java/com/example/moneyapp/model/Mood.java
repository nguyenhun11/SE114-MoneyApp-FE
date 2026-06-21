package com.example.moneyapp.model;

import java.util.ArrayList;
import java.util.List;

public class Mood {
    private int id;
    private String name;
    private String emoji;

    public Mood(int id, String name, String emoji) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmoji() { return emoji; }

    public int getColor() {
        switch (id) {
            case 1: return 0xFF4CAF50; // Vui vẻ - Green
            case 2: return 0xFF2196F3; // Buồn bã - Blue
            case 3: return 0xFFFF9800; // Căng thẳng - Orange
            case 4: return 0xFFE91E63; // Bốc đồng - Pink
            default: return 0xFF9E9E9E; // Bình thường - Grey
        }
    }

    public static List<Mood> getAllMoods() {
        List<Mood> moods = new ArrayList<>();
        moods.add(new Mood(0, "Bình thường", "😐"));
        moods.add(new Mood(1, "Vui vẻ", "😊"));
        moods.add(new Mood(2, "Buồn bã", "😔"));
        moods.add(new Mood(3, "Căng thẳng", "😫"));
        moods.add(new Mood(4, "Bốc đồng", "🤑"));
        return moods;
    }

    public static String getEmojiById(int id) {
        for (Mood mood : getAllMoods()) {
            if (mood.getId() == id) return mood.getEmoji();
        }
        return "😐";
    }

    public static String getNameById(int id) {
        for (Mood mood : getAllMoods()) {
            if (mood.getId() == id) return mood.getName();
        }
        return "Bình thường";
    }

    public static Mood getMoodById(int id) {
        for (Mood mood : getAllMoods()) {
            if (mood.getId() == id) return mood;
        }
        return new Mood(0, "Bình thường", "😐");
    }
}
