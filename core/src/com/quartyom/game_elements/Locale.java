package com.quartyom.game_elements;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.quartyom.LayThePath;

import java.util.HashMap;
import java.util.Map;

public class Locale {
    private final LayThePath game;
    public Map<String, String> folders;
    private Map<String, String> tags;

    public Locale(LayThePath game) {
        this.game = game;
        tags = new HashMap<>();
        folders = new HashMap<>();

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            for (FileHandle item : Gdx.files.internal("texts").list()) {
                folders.put(item.name(), item.child("name.txt").readString());
            }
        } else {
            folders.put("en", "English");
            // folders.put("ru", "Русский");
        }

    }

    public void set(String language) {
        if (folders.containsKey(language)) {
            game.userData.locale = language;
            // game.saveUserData();
            // tags = game.json.fromJson(tags.getClass(), Gdx.files.internal("texts/" + language + "/tags.json"));
            tags = new HashMap<>();
        } else {
            Gdx.app.error("Locale", language + " is not found");
        }
    }

    public String get(String tag) {
        if (tags.containsKey(tag)) {
            return tags.get(tag);
        } else if (game.userData.locale.equals("en")) {
            return tag;
        } else {
            return "[" + tag + "]";
        }
    }
}
