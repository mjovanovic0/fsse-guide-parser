package com.fsse.parser.model;

import java.util.Optional;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
public enum Header {

    FIRST_HEADER("First Header"), //
    STATISTICS("Statistics"), //
    ENHANCEMENTS("Enhancements"), //
    CRAFTING("Crafting"), //
    DROPPED_BY("Dropped By"), //
    QUEST_REWARD("Quest Reward"), //
    SOLD_AT("Sold At"), //
    SET_BONUSES("Set Bonuses"), //
    REQUIRED_IN_QUEST("Required in Quest"), //
    INGREDIENTS("Ingredients"), //
    FRAGMENT_STASH("Fragment Stash"), //
    USED_IN_RECIPES("Used In Recipes"), //
    CREATED_BY_RECIPE("Created by Recipe"), //
    EXTRACTED_FROM("Extracted From"), //
    CAN_OPEN("Can Open"), //
    EXTRACTABLE_COMPONENTS("Extractable Components"), //
    EXTRA_INFO("Extra Info"), //
    ;

    private String name;

    Header(final String name) {
        this.name = name;
    }

    public static Optional<Header> findByName(final String name) {
        for (final Header header : values()) {
            if (header.getName().equals(name)) {
                return Optional.of(header);
            }
        }
        return Optional.empty();
    }

    public String getName() {
        return name;
    }
}
