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
public enum Rarity {

    COMMON(0, "Common"), //
    RARE(1, "Rare"), //
    UNIQUE(2, "Unique"), //
    LEGENDARY(3, "Legendary"), //
    SUPER_ELITE(4, "Super Elite", "Super-Elite"), //
    CRYSTALLINE(5, "Crystalline (Non-Repairable)", "Crystalline"), //
    EPIC(6, "Epic"), //
    ;

    private int id;
    private List<String> names;

    Rarity(int id, String... names) {
        this.id = id;
        this.names = Arrays.asList(names);
    }

    public static Optional<Rarity> findById(int id) {
        for (final Rarity rarity : values()) {
            if (rarity.getId() == id) {
                return Optional.of(rarity);
            }
        }
        return Optional.empty();
    }

    public static Optional<Rarity> findByName(String name) {
        for (final Rarity rarity : values()) {
            for (final String rarityName : rarity.getNames()) {
                if (rarityName.equals(name)) {
                    return Optional.of(rarity);
                }
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return names.get(0);
    }

    public List<String> getNames() {
        return names;
    }
}
