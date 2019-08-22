package com.fsse.model.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ItemType {

    HELMET(0, "Helmet"), //
    ARMOR(1, "Armor"), //
    GLOVES(2, "Gloves"), //
    BOOTS(3, "Boots"), //
    WEAPON(4, "Weapon"), //
    SHIELD(5, "Shield"), //
    RING(6, "Ring"), //
    AMULET(7, "Amulet"), //
    RUNE(8, "Rune"), //

    QUEST_ITEM(9, "Quest Item"), //
    POTION(10, "Potion"), //
    COMPONENT(11, "Component"), //
    RESOURCE(12, "Resource"), //
    RECIPE(13, "Recipe"), //
    CONTAINER(14, "Container"), //
    COMPOSED_POTION(15, "Composed Potion"), //
    FRAGMENT_STASH(16, "Fragment Stash"), //
    ;

    private int id;
    private String name;

    ItemType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<ItemType> findById(int id) {
        for (final ItemType type : values()) {
            if (type.getId() == id) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public static Optional<ItemType> findByName(String name) {
        for (final ItemType type : values()) {
            if (type.getName().equals(name)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public static List<ItemType> getWearableItemTypes() {
        return Arrays.asList(HELMET, ARMOR, GLOVES, BOOTS, WEAPON, SHIELD, RING, AMULET, RUNE);
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
