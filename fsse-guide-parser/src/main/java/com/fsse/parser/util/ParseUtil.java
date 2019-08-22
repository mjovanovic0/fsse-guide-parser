package com.fsse.parser.util;

import com.fsse.model.FsItem;
import com.fsse.model.attribute.Enhancement;
import com.fsse.model.attribute.ItemType;
import com.fsse.model.attribute.Rarity;
import com.fsse.model.attribute.StatType;
import com.fsse.model.parts.FsItemCanOpen;
import com.fsse.model.parts.FsItemCrafting;
import com.fsse.model.parts.FsItemCreatedByRecipe;
import com.fsse.model.parts.FsItemDroppedBy;
import com.fsse.model.parts.FsItemEnhancement;
import com.fsse.model.parts.FsItemExtraInfo;
import com.fsse.model.parts.FsItemExtractableComponent;
import com.fsse.model.parts.FsItemExtractedFrom;
import com.fsse.model.parts.FsItemFragmentStash;
import com.fsse.model.parts.FsItemIngredient;
import com.fsse.model.parts.FsItemInventing;
import com.fsse.model.parts.FsItemQuestReward;
import com.fsse.model.parts.FsItemRequiredInQuest;
import com.fsse.model.parts.FsItemSetStatistics;
import com.fsse.model.parts.FsItemSoldAt;
import com.fsse.model.parts.FsItemStatistics;
import com.fsse.model.parts.FsItemUsedInRecipe;
import com.fsse.parser.model.Header;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
public final class ParseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseUtil.class);

    private static final String NOT_GAINED_FROM_A_QUEST = "[not gained from a quest]";
    private static final String NOT_DROPPED = "[not dropped]";
    private static final String NOT_SOLD = "[not sold]";

    private static final String NO_ENHANCEMENTS = "[no enhancements]";
    private static final String NO_CRAFTING = "[none]";
    private static final String NO_SET_BONUSES = "[none]";

    private static final String PLAYER_EQUIPPED = "Player Equipped Total:";
    private static final String PLAYER_BACKPACK = "Player Backpack Total:";
    private static final String GUILD_INVENTORY = "Guild Inventory Total:";

    private static final String ITEM_ID_REQUEST_PARAM = "item_id=";

    // Check GUIDE_ISSUES.md for details why these item errors are suppressed
    private static final List<String> IGNORE_ERROR_FOR_ITEMS = Arrays.asList("6646", "6647", "8914", "9999");

    private ParseUtil() {
    }

    public static void parseItem(final Document doc, final FsItem fsItem, final String itemId) {
        Element itemTable = extractTable(doc, itemId);
        final Map<Header, Elements> headerMap = splitByHeaders(itemTable);

        parseFirstHeader(headerMap, fsItem, itemId);
        parseStatistics(headerMap, fsItem, itemId);
        parseEnhancements(headerMap, fsItem, itemId);
        parseCrafting(headerMap, fsItem, itemId);
        parseDroppedBy(headerMap, fsItem, itemId);
        parseQuestReward(headerMap, fsItem, itemId);
        parseSoldAt(headerMap, fsItem, itemId);
        parseSetBonus(headerMap, fsItem, itemId);
        parseRequiredInQuest(headerMap, fsItem, itemId);
        parseIngredients(headerMap, fsItem, itemId);
        parseFragmentStash(headerMap, fsItem, itemId);
        parseUsedInRecipes(headerMap, fsItem, itemId);
        parseCreatedByRecipe(headerMap, fsItem, itemId);
        parseExtractedFrom(headerMap, fsItem, itemId);
        parseCanOpen(headerMap, fsItem, itemId);
        parseExtractableComponents(headerMap, fsItem, itemId);
        parseExtraInfo(headerMap, fsItem, itemId);
    }

    private static Element extractTable(final Document doc, final String itemId) {
        final Elements tables = doc.select("table[border='0'][cellpadding='2'][cellspacing='0']");
        if (tables == null || tables.size() != 1) {
            throw new IllegalStateException("Fail to extract item table for item " + itemId);
        }
        return tables.get(0);
    }

    private static Map<Header, Elements> splitByHeaders(final Element table) {
        final Elements rows = table.select("tr");

        boolean isFirstHeader = true;
        Header currentHeader = Header.FIRST_HEADER;
        List<Element> elements = new ArrayList<>();
        final Map<Header, Elements> groupedElements = new EnumMap<>(Header.class);

        for (final Element row : rows) {
            final Elements header = row.select(".tHeader");
            if (header != null && !header.isEmpty()) {
                if (isFirstHeader) {
                    isFirstHeader = false;
                    elements.add(row);
                    continue;
                }

                groupedElements.put(currentHeader, new Elements(elements));

                final String headerName = header.get(0).text();
                final Optional<Header> resolvedHeader = Header.findByName(headerName);
                if (resolvedHeader.isPresent()) {
                    currentHeader = resolvedHeader.get();
                    elements = new ArrayList<>();
                } else {
                    LOGGER.error("Fail to resolve item table header {}", headerName);
                }
            } else {
                elements.add(row);
            }
        }

        // Add last group
        groupedElements.put(currentHeader, new Elements(elements));

        return groupedElements;
    }

    private static void parseFirstHeader(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.FIRST_HEADER)) {
                final Elements firstHeaderElements = headerMap.get(Header.FIRST_HEADER);

                final Elements header = firstHeaderElements.select(".tHeader");
                final Elements itemNameElement = header.select("b");

                final String itemName = itemNameElement.text();
                final String rarityName = StringUtils.substringAfter(StringUtils.substringBeforeLast(StringUtils.remove(header.text(), itemName), ")"), "(");

                final Optional<Rarity> rarity = Rarity.findByName(rarityName);
                if (rarity.isPresent()) {
                    fsItem.setRarity(rarity.get());
                } else {
                    LOGGER.warn("Fail to determine item rarity for item id {}. Fail to resolve rarity with name {}", itemId, rarityName);
                }

                fsItem.setName(itemName);
            }
            fsItem.setId(toBigInteger(itemId, itemId, Header.FIRST_HEADER, "item id"));
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing first header for item id: " + itemId, e);
        }
    }

    private static void parseStatistics(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.STATISTICS)) {
                final Elements statisticsHeader = headerMap.get(Header.STATISTICS);

                final Elements cell = statisticsHeader.select("tr");

                final String type = cell.get(0).select("td").get(1).text();
                final String level = cell.get(0).select("td").get(3).text();

                fsItem.setLevel(toBigInteger(level, itemId, Header.STATISTICS, "level"));

                final Optional<ItemType> itemType = ItemType.findByName(type);
                if (itemType.isPresent()) {
                    fsItem.setType(itemType.get());
                } else {
                    LOGGER.warn("Fail to determine item type for item id {}. Fail to resolve item type with name {}", itemId, type);
                    return;
                }

                if (ItemType.getWearableItemTypes().contains(fsItem.getType())) {
                    fsItem.setStatistics(new FsItemStatistics());
                    fsItem.getStatistics().setItem(fsItem);

                    final String attack = cell.get(1).select("td").get(1).text();
                    final String defense = cell.get(1).select("td").get(4).text();

                    final String armor = cell.get(2).select("td").get(1).text();
                    final String damange = cell.get(2).select("td").get(4).text();

                    final String hp = cell.get(3).select("td").get(1).text();
                    final String xpGain = cell.get(3).select("td").get(4).text();

                    final String stamina = cell.get(4).select("td").get(1).text();
                    final String staminaGain = cell.get(4).select("td").get(4).text();

                    final String goldGain = cell.get(5).select("td").get(1).text();

                    fsItem.getStatistics().setAttack(toBigInteger(attack, itemId, Header.STATISTICS, "Attack"));
                    fsItem.getStatistics().setDefense(toBigInteger(defense, itemId, Header.STATISTICS, "Defense"));
                    fsItem.getStatistics().setArmor(toBigInteger(armor, itemId, Header.STATISTICS, "Armor"));
                    fsItem.getStatistics().setDamage(toBigInteger(damange, itemId, Header.STATISTICS, "Damage"));
                    fsItem.getStatistics().setHp(toBigInteger(hp, itemId, Header.STATISTICS, "HP"));
                    fsItem.getStatistics().setXpGain(toBigInteger(xpGain, itemId, Header.STATISTICS, "XP Gain"));
                    fsItem.getStatistics().setStamina(toBigInteger(stamina, itemId, Header.STATISTICS, "Stamina"));
                    fsItem.getStatistics().setStaminaGain(toBigInteger(staminaGain, itemId, Header.STATISTICS, "Stamina Gain"));
                    fsItem.getStatistics().setGoldGain(toBigInteger(goldGain, itemId, Header.STATISTICS, "Gold Gain"));
                } else if (ItemType.RECIPE.equals(fsItem.getType())) {
                    fsItem.setInventing(new FsItemInventing());
                    fsItem.getInventing().setItem(fsItem);

                    final String inventingLevel = cell.get(1).select("td").get(1).text();
                    final String successRate = StringUtils.substringBefore(cell.get(1).select("td").get(4).text(), "%").trim();

                    final Elements createsItemLink = cell.get(2).select("td").get(1).select("a");
                    final String createsItemId = StringUtils.substringAfter(createsItemLink.attr("href"), ITEM_ID_REQUEST_PARAM);
                    final String itemName = createsItemLink.text();

                    fsItem.getInventing().setInventingLevel(toBigInteger(inventingLevel, itemId, Header.STATISTICS, "Recipe inventing level"));
                    fsItem.getInventing().setSuccessRate(toBigDecimal(successRate, itemId, Header.STATISTICS, "Recipe inventing success rate"));
                    fsItem.getInventing().setItemId(toBigInteger(createsItemId, itemId, Header.STATISTICS, "Recipe inventing item id"));
                    fsItem.getInventing().setItemName(itemName);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing statistics for item id: " + itemId, e);
        }
    }

    private static void parseEnhancements(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.ENHANCEMENTS)) {
                final Elements enhancementsHeader = headerMap.get(Header.ENHANCEMENTS);

                if (!NO_ENHANCEMENTS.equals(enhancementsHeader.text().trim())) {
                    final Elements rows = enhancementsHeader.select("tr");
                    rows.remove(rows.size() - 1);

                    for (final Element row : rows) {
                        final Elements cells = row.select("td");

                        String enhancementName = cells.get(0).text();
                        String enhancementValue = cells.get(1).text();

                        enhancementName = StringUtils.substringBefore(enhancementName, ":");
                        enhancementValue = StringUtils.substringBefore(enhancementValue, "%");

                        final Optional<Enhancement> resolvedEnhancement = Enhancement.findByName(enhancementName);
                        if (resolvedEnhancement.isPresent()) {
                            final Enhancement enhancement = resolvedEnhancement.get();

                            final FsItemEnhancement itemEnhancement = new FsItemEnhancement();
                            itemEnhancement.setItem(fsItem);
                            itemEnhancement.setEnhancement(enhancement);
                            itemEnhancement.setPercentage(toBigDecimal(enhancementValue, itemId, Header.ENHANCEMENTS, "Percentage"));

                            fsItem.getEnhancements().add(itemEnhancement);
                        } else if (!ItemType.CONTAINER.equals(fsItem.getType())) { // All containers have invalid enhancement entry without name
                            LOGGER.warn("Fail to determine enhancement for item id {}. Fail to resolve enhancement with name '{}'", itemId, enhancementName);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing enhancements for item id: " + itemId, e);
        }
    }

    private static void parseCrafting(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.CRAFTING)) {
                final Elements craftingHeader = headerMap.get(Header.CRAFTING);

                if (!NO_CRAFTING.equals(craftingHeader.text().trim())) {
                    final Elements rows = craftingHeader.select("tr");
                    rows.remove(rows.size() - 1);

                    for (final Element row : rows) {
                        final Elements cell = row.select("td");

                        String type = cell.get(0).text();
                        final String min = cell.get(1).text();
                        final String max = cell.get(3).text();

                        type = StringUtils.substringBefore(type, ":");

                        final Optional<StatType> stat = StatType.findByName(type);
                        if (stat.isPresent()) {
                            final FsItemCrafting itemCrafting = new FsItemCrafting();
                            itemCrafting.setItem(fsItem);
                            itemCrafting.setStat(stat.get());
                            itemCrafting.setMin(toBigInteger(min, itemId, Header.CRAFTING, "Min"));
                            itemCrafting.setMax(toBigInteger(max, itemId, Header.CRAFTING, "Max"));

                            fsItem.getCraftings().add(itemCrafting);
                        } else {
                            LOGGER.warn("Fail to determine crafting for item id {}. Fail to resolve stat type with name {}", itemId, type);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing crafting for item id: " + itemId, e);
        }
    }

    private static void parseDroppedBy(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.DROPPED_BY)) {
                final Elements droppedByHeader = headerMap.get(Header.DROPPED_BY);

                final Elements cell = droppedByHeader.select("td");
                if (!NOT_DROPPED.equals(cell.text().trim())) {
                    fsItem.setDroppedBy(new FsItemDroppedBy());
                    fsItem.getDroppedBy().setItem(fsItem);

                    final Elements creatureLink = cell.get(0).select("a");
                    final Element dropRateCell = cell.get(1);

                    final String creatureId = StringUtils.substringAfter(creatureLink.attr("href"), "creature_id=");
                    final String creatureName = creatureLink.text();
                    final String dropRate = StringUtils.substringBefore(dropRateCell.text(), "%");

                    fsItem.getDroppedBy().setCreatureId(toBigInteger(creatureId, itemId, Header.DROPPED_BY, "Creature ID"));
                    fsItem.getDroppedBy().setCreatureName(creatureName);
                    fsItem.getDroppedBy().setDropRate(toBigDecimal(dropRate, itemId, Header.DROPPED_BY, "Drop Rate"));
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing dropped by for item id: " + itemId, e);
        }
    }

    private static void parseQuestReward(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.QUEST_REWARD)) {
                final Elements questRewardHeader = headerMap.get(Header.QUEST_REWARD);

                final Elements cell = questRewardHeader.select("td");
                if (!NOT_GAINED_FROM_A_QUEST.equals(cell.text().trim())) {
                    fsItem.setQuestReward(new FsItemQuestReward());
                    fsItem.getQuestReward().setItem(fsItem);

                    final Elements questLink = cell.get(0).select("a");
                    final String questId = StringUtils.substringAfter(questLink.attr("href"), "quest_id=");
                    final String questName = questLink.text();

                    fsItem.getQuestReward().setQuestId(toBigInteger(questId, itemId, Header.QUEST_REWARD, "Quest ID"));
                    fsItem.getQuestReward().setQuestName(questName);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing quest reward for item id: " + itemId, e);
        }
    }

    private static void parseSoldAt(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.SOLD_AT)) {
                final Elements soldAtHeader = headerMap.get(Header.SOLD_AT);

                final Elements cell = soldAtHeader.select("td");
                if (!NOT_SOLD.equals(cell.text().trim())) {
                    fsItem.setSoldAt(new FsItemSoldAt());
                    fsItem.getSoldAt().setItem(fsItem);

                    final Elements shopLink = cell.get(0).select("a");
                    final String shopId = StringUtils.substringAfter(shopLink.attr("href"), "shop_id=");
                    final String shopName = shopLink.text();

                    fsItem.getSoldAt().setShopId(toBigInteger(shopId, itemId, Header.SOLD_AT, "Shop ID"));
                    fsItem.getSoldAt().setShopName(shopName);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing sold at for item id: " + itemId, e);
        }
    }

    private static void parseSetBonus(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            // For some strange reason, Containers has set bonuses
            if (headerMap.containsKey(Header.SET_BONUSES) && !ItemType.CONTAINER.equals(fsItem.getType())) {
                final Elements setBonusesHeader = headerMap.get(Header.SET_BONUSES);

                final String groupText = setBonusesHeader.text().trim();
                if (StringUtils.isNotBlank(groupText) && !NO_SET_BONUSES.equals(groupText)) {

                    final Elements cell = setBonusesHeader.select("tr");

                    final Elements setLink = cell.get(0).select("a");

                    final String setId = StringUtils.substringAfter(setLink.attr("href"), "search_set_id=");
                    final String setName = setLink.text();

                    final String attack = cell.get(1).select("td").get(1).text();
                    final String defense = cell.get(1).select("td").get(4).text();

                    final String armor = cell.get(2).select("td").get(1).text();
                    final String damange = cell.get(2).select("td").get(4).text();

                    final String hp = cell.get(3).select("td").get(1).text();
                    final String xpGain = cell.get(3).select("td").get(4).text();

                    final String stamina = cell.get(4).select("td").get(1).text();
                    final String staminaGain = cell.get(4).select("td").get(4).text();

                    final String goldGain = cell.get(5).select("td").get(1).text();

                    fsItem.setSetStatistics(new FsItemSetStatistics());
                    fsItem.getSetStatistics().setItem(fsItem);
                    fsItem.getSetStatistics().setSetId(toBigInteger(setId, itemId, Header.SET_BONUSES, "Set ID"));
                    fsItem.getSetStatistics().setSetName(setName);

                    fsItem.getSetStatistics().setAttack(toBigInteger(attack, itemId, Header.SET_BONUSES, "Attack"));
                    fsItem.getSetStatistics().setDefense(toBigInteger(defense, itemId, Header.SET_BONUSES, "Defense"));
                    fsItem.getSetStatistics().setArmor(toBigInteger(armor, itemId, Header.SET_BONUSES, "Armor"));
                    fsItem.getSetStatistics().setDamage(toBigInteger(damange, itemId, Header.SET_BONUSES, "Damage"));
                    fsItem.getSetStatistics().setHp(toBigInteger(hp, itemId, Header.SET_BONUSES, "HP"));
                    fsItem.getSetStatistics().setXpGain(toBigInteger(xpGain, itemId, Header.SET_BONUSES, "XP Gain"));
                    fsItem.getSetStatistics().setStamina(toBigInteger(stamina, itemId, Header.SET_BONUSES, "Stamina"));
                    fsItem.getSetStatistics().setStaminaGain(toBigInteger(staminaGain, itemId, Header.SET_BONUSES, "Stamina Gain"));
                    fsItem.getSetStatistics().setGoldGain(toBigInteger(goldGain, itemId, Header.SET_BONUSES, "Gold Gain"));
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing set bonuses for item id: " + itemId, e);
        }
    }

    private static void parseRequiredInQuest(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.REQUIRED_IN_QUEST)) {
                final Elements requiredInQuest = headerMap.get(Header.REQUIRED_IN_QUEST);

                final String groupText = requiredInQuest.text().trim();
                if (StringUtils.isNotBlank(groupText)) {
                    final Elements cell = requiredInQuest.select("td");

                    final Elements questLink = cell.get(0).select("a");
                    final String questId = StringUtils.substringAfter(questLink.attr("href"), "quest_id=");
                    final String questName = questLink.text();

                    fsItem.setRequiredInQuest(new FsItemRequiredInQuest());
                    fsItem.getRequiredInQuest().setItem(fsItem);
                    fsItem.getRequiredInQuest().setQuestId(toBigInteger(questId, itemId, Header.REQUIRED_IN_QUEST, "Quest ID"));
                    fsItem.getRequiredInQuest().setQuestName(questName);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing required in quest for item id: " + itemId, e);
        }
    }

    private static void parseIngredients(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.INGREDIENTS)) {
                final Elements ingredientsHeader = headerMap.get(Header.INGREDIENTS);

                final Elements rows = ingredientsHeader.select("tr");
                rows.remove(rows.size() - 1);

                for (final Element row : rows) {
                    final Elements cell = row.select("td");

                    final Elements ingredientLink = cell.get(0).select("a");
                    final String ingredientId = StringUtils.substringAfter(ingredientLink.attr("href"), ITEM_ID_REQUEST_PARAM);
                    final String ingredientName = ingredientLink.text();

                    final String amount = cell.get(1).text();

                    final FsItemIngredient itemIngredient = new FsItemIngredient();
                    itemIngredient.setItem(fsItem);
                    itemIngredient.setIngredientId(toBigInteger(ingredientId, itemId, Header.INGREDIENTS, "Ingredient ID"));
                    itemIngredient.setIngredientName(ingredientName);
                    itemIngredient.setAmount(toBigInteger(amount, itemId, Header.INGREDIENTS, "Amount"));

                    fsItem.getIngredients().add(itemIngredient);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing ingredients for item id: " + itemId, e);
        }
    }

    private static void parseFragmentStash(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.FRAGMENT_STASH)) {
                final Elements fragmentStashHeader = headerMap.get(Header.FRAGMENT_STASH);

                final Elements rows = fragmentStashHeader.select("tr");
                rows.remove(rows.size() - 1);

                for (final Element row : rows) {
                    final Elements cell = row.select("td");

                    String type = cell.get(0).text();
                    final String minMaxText = cell.get(1).text().trim();
                    final String[] minMax = StringUtils.split(minMaxText, " - ");

                    type = StringUtils.substringBefore(type, ":");

                    final Optional<Rarity> rarity = Rarity.findByName(type);
                    if (rarity.isPresent()) {
                        final FsItemFragmentStash fragmentStash = new FsItemFragmentStash();
                        fragmentStash.setItem(fsItem);
                        fragmentStash.setRarity(rarity.get());
                        fragmentStash.setMin(toBigInteger(minMax[0], itemId, Header.FRAGMENT_STASH, "Min"));
                        fragmentStash.setMax(toBigInteger(minMax[1], itemId, Header.FRAGMENT_STASH, "Max"));

                        fsItem.getFragmentStashes().add(fragmentStash);
                    } else {
                        LOGGER.warn("Fail to determine crafting for item id {}. Fail to resolve rarity type with name '{}'", itemId, type);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing fragment stash for item id: " + itemId, e);
        }
    }

    private static void parseUsedInRecipes(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.USED_IN_RECIPES)) {
                final Elements usedInRecipesHeader = headerMap.get(Header.USED_IN_RECIPES);

                final Elements rows = usedInRecipesHeader.select("tr");
                rows.remove(rows.size() - 1);

                for (final Element row : rows) {
                    final Elements cell = row.select("td");

                    final Elements recipeLink = cell.get(0).select("a");
                    final String recipeId = StringUtils.substringAfter(recipeLink.attr("href"), ITEM_ID_REQUEST_PARAM);
                    final String recipeName = recipeLink.text();

                    final FsItemUsedInRecipe usedInRecipe = new FsItemUsedInRecipe();
                    usedInRecipe.setItem(fsItem);
                    usedInRecipe.setRecipeId(toBigInteger(recipeId, itemId, Header.USED_IN_RECIPES, "Recipe ID"));
                    usedInRecipe.setRecipeName(recipeName);

                    fsItem.getUsedInRecipes().add(usedInRecipe);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing used in recipes for item id: " + itemId, e);
        }
    }

    private static void parseCreatedByRecipe(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.CREATED_BY_RECIPE)) {
                final Elements createdByRecipeHeader = headerMap.get(Header.CREATED_BY_RECIPE);

                final Elements cell = createdByRecipeHeader.select("td");

                final Elements recipeLink = cell.get(0).select("a");
                final String recipeId = StringUtils.substringAfter(recipeLink.attr("href"), ITEM_ID_REQUEST_PARAM);
                final String recipeName = recipeLink.text();

                fsItem.setCreatedByRecipe(new FsItemCreatedByRecipe());
                fsItem.getCreatedByRecipe().setItem(fsItem);
                fsItem.getCreatedByRecipe().setRecipeId(toBigInteger(recipeId, itemId, Header.CREATED_BY_RECIPE, "Recipe ID"));
                fsItem.getCreatedByRecipe().setRecipeName(recipeName);
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing created by recipes for item id: " + itemId, e);
        }
    }

    private static void parseExtractedFrom(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.EXTRACTED_FROM)) {
                final Elements extractedFrom = headerMap.get(Header.EXTRACTED_FROM);

                final Elements cell = extractedFrom.select("td");

                final Elements itemLink = cell.get(0).select("a");
                final String extractedFromItemId = StringUtils.substringAfter(itemLink.attr("href"), ITEM_ID_REQUEST_PARAM);
                final String itemName = itemLink.text();

                fsItem.setExtractedFrom(new FsItemExtractedFrom());
                fsItem.getExtractedFrom().setItem(fsItem);
                fsItem.getExtractedFrom().setItemId(toBigInteger(extractedFromItemId, itemId, Header.EXTRACTED_FROM, "Item ID"));
                fsItem.getExtractedFrom().setItemName(itemName);
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing extracted from for item id: " + itemId, e);
        }
    }

    private static void parseCanOpen(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.CAN_OPEN)) {
                final Elements canOpenHeader = headerMap.get(Header.CAN_OPEN);

                final Elements rows = canOpenHeader.select("tr");
                rows.remove(rows.size() - 1);

                for (final Element row : rows) {
                    final Elements cell = row.select("td");

                    final FsItemCanOpen canOpen = new FsItemCanOpen();
                    canOpen.setItem(fsItem);
                    canOpen.setChestName(cell.text().trim());

                    fsItem.getCanOpen().add(canOpen);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing can open for item id: " + itemId, e);
        }
    }

    private static void parseExtractableComponents(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.EXTRACTABLE_COMPONENTS)) {
                final Elements extractableComponentsHeader = headerMap.get(Header.EXTRACTABLE_COMPONENTS);

                final Elements rows = extractableComponentsHeader.select("tr");
                rows.remove(rows.size() - 1);

                for (final Element row : rows) {
                    final Elements cell = row.select("td");

                    final Elements itemLink = cell.get(0).select("a");
                    final String componentItemId = StringUtils.substringAfter(itemLink.attr("href"), ITEM_ID_REQUEST_PARAM);
                    final String itemName = itemLink.text();

                    final FsItemExtractableComponent extractableComponent = new FsItemExtractableComponent();
                    extractableComponent.setItem(fsItem);
                    extractableComponent.setComponentId(toBigInteger(componentItemId, itemId, Header.EXTRACTABLE_COMPONENTS, "Component ID"));
                    extractableComponent.setComponentName(itemName);

                    fsItem.getExtractableComponents().add(extractableComponent);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing used in recipes for item id: " + itemId, e);
        }
    }

    private static void parseExtraInfo(final Map<Header, Elements> headerMap, final FsItem fsItem, final String itemId) {
        try {
            if (headerMap.containsKey(Header.EXTRA_INFO)) {
                final Elements extraInfoHeader = headerMap.get(Header.EXTRA_INFO);

                final Elements rows = extraInfoHeader.select("tr");

                for (final Element row : rows) {
                    final Elements cell = row.select("td");

                    final String type = cell.get(0).text();
                    final String value = cell.get(1).text();

                    if (fsItem.getExtraInfo() == null) {
                        fsItem.setExtraInfo(new FsItemExtraInfo());
                        fsItem.getExtraInfo().setItem(fsItem);
                    }

                    switch (type) {
                        case PLAYER_EQUIPPED:
                            fsItem.getExtraInfo().setEquippedCount(toBigInteger(value, itemId, Header.EXTRA_INFO, "Equipped Count"));
                            break;
                        case PLAYER_BACKPACK:
                            fsItem.getExtraInfo().setBackpackCount(toBigInteger(value, itemId, Header.EXTRA_INFO, "Backpack Count"));
                            break;
                        case GUILD_INVENTORY:
                            fsItem.getExtraInfo().setGuildTaggedCount(toBigInteger(value, itemId, Header.EXTRA_INFO, "Guild Tagged Count"));
                            break;
                        default:
                            LOGGER.warn("Unsupported extra info type: '{}'.", type);
                            break;
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Unhandled exception while parsing extra info for item id: " + itemId, e);
        }
    }

    public static int toInt(final String value, final String itemId, final Header section, final String element) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            logError(value, itemId, section, element);
            return 0;
        }
    }

    private static BigInteger toBigInteger(final String value, final String itemId, final Header section, final String element) {
        try {
            return new BigInteger(StringUtils.replace(value, ",", ""));
        } catch (final NumberFormatException e) {
            logError(value, itemId, section, element);
            return BigInteger.ZERO;
        }
    }

    private static BigDecimal toBigDecimal(final String value, final String itemId, final Header section, final String element) {
        try {
            // Replace empty space due wrong data from guide. Check GUIDE_ISSUES.md for details
            return new BigDecimal(StringUtils.replace(value, " ", ""));
        } catch (final NumberFormatException e) {
            logError(value, itemId, section, element);
            return BigDecimal.ZERO;
        }
    }

    private static void logError(final String value, final String itemId, final Header section, final String element) {
        if (IGNORE_ERROR_FOR_ITEMS.contains(itemId)) {
            LOGGER.error("[Item:{}][Section:{}][Element:{}] Number not parsable. Received string value: '{}'.", itemId, section.getName(), element, value);
        }
    }
}
