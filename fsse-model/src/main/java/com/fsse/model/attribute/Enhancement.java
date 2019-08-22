package com.fsse.model.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Optional;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Enhancement {

    PIERCING_STRIKE(0, "Piercing Strike"), //
    REINFORCED_ARMOR(1, "Reinforced Armor"), //
    THIEVERY(2, "Thievery"), //
    CRITICAL_HIT(3, "Critical Hit"), //
    HOLY(4, "Holy"), //
    BREAKER(5, "Breaker"), //
    NULLIFY(6, "Nullify"), //
    BANISHMENT(7, "Banishment"), //
    PROTECTION(8, "Protection"), //
    OCEANIC(9, "Oceanic"), //
    MASTER_THIEF(10, "Master Thief"), //
    PROTECT_GOLD(11, "Protect Gold"), //
    DODGE(12, "Dodge"), //
    DISARM(13, "Disarm"), //
    MASTER_BLACKSMITH(14, "Master Blacksmith"), //
    ELITE_HUNTER(15, "Elite Hunter"), //
    SUSTAIN(16, "Sustain"), //
    MASTER_CRAFTER(17, "Master Crafter"), //
    FURY_CASTER(18, "Fury Caster"), //
    GREENSKIN_SLAYER(19, "Greenskin Slayer"), //
    BEAST_SLAYER(20, "Beast Slayer"), //
    DUELIST(21, "Duelist"), //
    GLORY_SEEKER(22, "Glory Seeker"), //
    FIRST_STRIKE(23, "First Strike"), //
    HYPNOTIZE(24, "Hypnotize"), //
    MASTER_INVENTOR(25, "Master Inventor"), //
    SOULLESS(26, "Soulless"), //
    TEMPORAL_SHIFT(27, "Temporal Shift"), //
    ;

    private int id;
    private String name;

    Enhancement(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<Enhancement> findById(int id) {
        for (final Enhancement enhancement : values()) {
            if (enhancement.getId() == id) {
                return Optional.of(enhancement);
            }
        }
        return Optional.empty();
    }

    public static Optional<Enhancement> findByName(String name) {
        for (final Enhancement enhancement : values()) {
            if (enhancement.getName().equals(name)) {
                return Optional.of(enhancement);
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
