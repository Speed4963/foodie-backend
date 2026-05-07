package com.eatproject.backend.common;

import lombok.Getter;

@Getter
public enum CategoryType {
    VEGAN("비건"),
    BIZARRE("괴식"),
    EXOTIC("이국요리"),
    CULTURE("컬쳐물"),
    FAMOUS_CHEF("유명셰프"),
    MICHELIN("미슐랭"),
    WORLD_LIQUOR("세계주류"),
    THEME("테마"),
    ANIMAL("동물");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }
}