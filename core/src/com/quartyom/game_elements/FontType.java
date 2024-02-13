package com.quartyom.game_elements;

public enum FontType {
    LOCALIZED_LIGHT,        // only .*/#... + local characters
    LOCALIZED_WITH_LATIN,   // localized + latin (may intersect for initially latin locales such as english)
    INTERNATIONAL           // every character available in all the locales
}
