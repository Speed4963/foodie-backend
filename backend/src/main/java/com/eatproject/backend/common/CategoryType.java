package com.eatproject.backend.common;

import lombok.Getter;

@Getter
public enum CategoryType {
    VEGETARIAN("채식사진"),
    MAINSTREAM("주류"),
    EXOTIC("이국요리"),
    ECCENTRIC("괴식요리"),
    FAMOUSCHEF("유명셰프"),
    MICHELIN("미슐랭"),
    KIDSZONE("키즈존"),
    PETACCESS("동물출입");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }
}