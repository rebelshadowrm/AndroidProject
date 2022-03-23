package com.example.project1;

public class Character {

    String characterName, characterImage;

    public Character(String characterName, String characterImage) {
        this.characterName = characterName;
        this.characterImage = characterImage;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getCharacterImage() {
        return characterImage;
    }
}
