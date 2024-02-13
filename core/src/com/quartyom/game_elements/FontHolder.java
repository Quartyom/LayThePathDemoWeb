package com.quartyom.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.quartyom.LayThePath;

import java.util.HashMap;
import java.util.Map;

public class FontHolder {
    LayThePath game;

    private FreeTypeFontGenerator generator;
    private Map<Integer, BitmapFont> fontsLight, fontsWithLatin, internationalFonts;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private String charactersLight, charactersWithLatin, internationalCharacters;
    private boolean isLatinLight = false;   // are only local characters and local + latin the same

    // " !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz[\\]^_`{|}~";
    public static final String DEFAULT_CHARACTERS = " !\"#$%&\'()*+,-./0123456789:;<=>?@[\\]^_`{|}~";
    public static final String LATIN_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public FontHolder(LayThePath game, String path) {
        this.game = game;

        FreeTypeFontGenerator.setMaxTextureSize(2048);  // very important string, without it most part of symbols is invisible
        //FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
        generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;

        fontsLight = new HashMap<>();
        fontsWithLatin = new HashMap<>();
        internationalFonts = new HashMap<>();

        internationalCharacters = DEFAULT_CHARACTERS + LATIN_CHARACTERS;
        for (String key : game.locale.folders.keySet()) {
            internationalCharacters += Gdx.files.internal("texts/" + key + "/alphabet.txt").readString();
        }
        internationalCharacters = internationalCharacters.replaceAll("(.)\\1{1,}", "$1");   // avoid repeated chars

        updateLocale();
    }

    public void updateLocale() {
        clearFonts(fontsLight);
        clearFonts(fontsWithLatin);

        String localCharacters = Gdx.files.internal("texts/" + game.userData.locale + "/alphabet.txt").readString();
        if (localCharacters.contains("*")) {
            isLatinLight = true;
            localCharacters = localCharacters.replace("*", LATIN_CHARACTERS);
        } else {
            isLatinLight = false;
            charactersWithLatin = DEFAULT_CHARACTERS + LATIN_CHARACTERS + localCharacters;
            charactersWithLatin = charactersWithLatin.replaceAll("(.)\\1{1,}", "$1");
        }
        charactersLight = DEFAULT_CHARACTERS + localCharacters;
        charactersLight = charactersLight.replaceAll("(.)\\1{1,}", "$1"); // avoid repeated chars

    }

    public BitmapFont get(Integer fontSize, FontType fontType) {    // returns required font from existing, creates new if needed
        // if required light, latin or international also suffice bcs they contain such symbol
        switch (fontType) {  // searches in light, then latin, then international
            case LOCALIZED_LIGHT:
                if (fontsLight.containsKey(fontSize)) {
                    return fontsLight.get(fontSize);
                }
            case LOCALIZED_WITH_LATIN:
                if (fontsLight.containsKey(fontSize) && isLatinLight) {
                    return fontsLight.get(fontSize);
                }
                else if (fontsWithLatin.containsKey(fontSize)) {
                    return fontsWithLatin.get(fontSize);
                }
            case INTERNATIONAL:
                if (internationalFonts.containsKey(fontSize)) {
                    return internationalFonts.get(fontSize);
                }
        }

        Map<Integer, BitmapFont> fonts; // arbitrary fonts map reference

        if (fontType == FontType.LOCALIZED_LIGHT || fontType == FontType.LOCALIZED_WITH_LATIN && isLatinLight) {
            fonts = fontsLight;
            parameter.characters = charactersLight;
        } else if (fontType == FontType.LOCALIZED_WITH_LATIN) {
            fonts = fontsWithLatin;
            parameter.characters = charactersWithLatin;
        } else {
            fonts = internationalFonts;
            parameter.characters = internationalCharacters;
        }

        parameter.size = fontSize;
        BitmapFont font = generator.generateFont(parameter);    // creating a new font + put in according map

        fonts.put(fontSize, font);
        return font;
    }

    public void dispose() {
        clearFonts(fontsLight);
        clearFonts(fontsWithLatin);
        clearFonts(internationalFonts);
        generator.dispose();
    }

    public void clearFonts(Map<Integer, BitmapFont> fonts) {
        for (BitmapFont item : fonts.values()) {
            item.dispose();
        }
        fonts.clear();
    }
}
