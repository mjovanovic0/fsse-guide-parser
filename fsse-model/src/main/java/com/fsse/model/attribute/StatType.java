package com.fsse.model.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Optional;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StatType {

    ATTACK(0, "Attack", "attack"), //
    DEFENSE(1, "Defense", "defense"), //
    ARMOR(2, "Armor", "armor"), //
    HP(3, "Hp", "hp"), //
    DAMAGE(4, "Damage", "damage"), //
    STAMINA(5, "Stamina", "stamina"), //
    STAMINA_GAIN(6, "Stamina Gain", "stamina-gain"), //
    GOLD_GAIN(7, "Gold Gain", "gold-gain"), //
    XP_GAIN(8, "Xp Gain", "xp-gain"), //
    ;

    private int id;
    private String name;
    private String code;

    StatType(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public static Optional<StatType> findById(int id) {
        for (final StatType type : values()) {
            if (type.getId() == id) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public static Optional<StatType> findByName(final String name) {
        for (final StatType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
