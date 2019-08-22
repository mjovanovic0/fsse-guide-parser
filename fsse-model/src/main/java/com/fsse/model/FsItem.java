package com.fsse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.attribute.ItemType;
import com.fsse.model.attribute.Rarity;
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItem {

    @Id
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "NAME")
    private String name;

    @Type(type = "com.fsse.hibernate.CustomEnumType", parameters = { //
            @Parameter(name = "enumClassName", value = "com.fsse.model.attribute.Rarity"), //
            @Parameter(name = "enumPropertyName", value = "id"), //
            @Parameter(name = "type", value = "integer") //
    })
    @Column(name = "RARITY")
    private Rarity rarity;

    @Type(type = "com.fsse.hibernate.CustomEnumType", parameters = { //
            @Parameter(name = "enumClassName", value = "com.fsse.model.attribute.ItemType"), //
            @Parameter(name = "enumPropertyName", value = "id"), //
            @Parameter(name = "type", value = "integer") //
    })
    @Column(name = "TYPE")
    private ItemType type;

    @Column(name = "LEVEL")
    private BigInteger level;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemStatistics statistics;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemInventing inventing;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemEnhancement> enhancements;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemCrafting> craftings;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemDroppedBy droppedBy;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemQuestReward questReward;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemRequiredInQuest requiredInQuest;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemIngredient> ingredients;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemFragmentStash> fragmentStashes;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemUsedInRecipe> usedInRecipes;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemCreatedByRecipe createdByRecipe;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemExtractedFrom extractedFrom;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemCanOpen> canOpen;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemSoldAt soldAt;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemSetStatistics setStatistics;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemExtraInfo extraInfo;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FsItemExtractableComponent> extractableComponents;

    public BigInteger getId() {
        return id;
    }

    public void setId(final BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(final Rarity rarity) {
        this.rarity = rarity;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(final ItemType type) {
        this.type = type;
    }

    public BigInteger getLevel() {
        return level;
    }

    public void setLevel(final BigInteger level) {
        this.level = level;
    }

    public FsItemStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(final FsItemStatistics statistics) {
        this.statistics = statistics;
    }

    public FsItemInventing getInventing() {
        return inventing;
    }

    public void setInventing(final FsItemInventing inventing) {
        this.inventing = inventing;
    }

    public List<FsItemEnhancement> getEnhancements() {
        if (enhancements == null) {
            enhancements = new ArrayList<>();
        }
        return enhancements;
    }

    public void setEnhancements(final List<FsItemEnhancement> enhancements) {
        this.enhancements = enhancements;
    }

    public List<FsItemCrafting> getCraftings() {
        if (craftings == null) {
            craftings = new ArrayList<>();
        }
        return craftings;
    }

    public void setCraftings(final List<FsItemCrafting> craftings) {
        this.craftings = craftings;
    }

    public FsItemDroppedBy getDroppedBy() {
        return droppedBy;
    }

    public void setDroppedBy(final FsItemDroppedBy droppedBy) {
        this.droppedBy = droppedBy;
    }

    public FsItemQuestReward getQuestReward() {
        return questReward;
    }

    public void setQuestReward(final FsItemQuestReward questReward) {
        this.questReward = questReward;
    }

    public FsItemRequiredInQuest getRequiredInQuest() {
        return requiredInQuest;
    }

    public void setRequiredInQuest(final FsItemRequiredInQuest requiredInQuest) {
        this.requiredInQuest = requiredInQuest;
    }

    public List<FsItemIngredient> getIngredients() {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        return ingredients;
    }

    public void setIngredients(final List<FsItemIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<FsItemFragmentStash> getFragmentStashes() {
        if (fragmentStashes == null) {
            fragmentStashes = new ArrayList<>();
        }
        return fragmentStashes;
    }

    public void setFragmentStashes(final List<FsItemFragmentStash> fragmentStashes) {
        this.fragmentStashes = fragmentStashes;
    }

    public List<FsItemUsedInRecipe> getUsedInRecipes() {
        if (usedInRecipes == null) {
            usedInRecipes = new ArrayList<>();
        }
        return usedInRecipes;
    }

    public void setUsedInRecipes(final List<FsItemUsedInRecipe> usedInRecipes) {
        this.usedInRecipes = usedInRecipes;
    }

    public FsItemCreatedByRecipe getCreatedByRecipe() {
        return createdByRecipe;
    }

    public void setCreatedByRecipe(final FsItemCreatedByRecipe createdByRecipe) {
        this.createdByRecipe = createdByRecipe;
    }

    public FsItemExtractedFrom getExtractedFrom() {
        return extractedFrom;
    }

    public void setExtractedFrom(final FsItemExtractedFrom extractedFrom) {
        this.extractedFrom = extractedFrom;
    }

    public List<FsItemCanOpen> getCanOpen() {
        if (canOpen == null) {
            canOpen = new ArrayList<>();
        }
        return canOpen;
    }

    public void setCanOpen(final List<FsItemCanOpen> canOpen) {
        this.canOpen = canOpen;
    }

    public FsItemSoldAt getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(final FsItemSoldAt soldAt) {
        this.soldAt = soldAt;
    }

    public FsItemSetStatistics getSetStatistics() {
        return setStatistics;
    }

    public void setSetStatistics(final FsItemSetStatistics setStatistics) {
        this.setStatistics = setStatistics;
    }

    public FsItemExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(final FsItemExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<FsItemExtractableComponent> getExtractableComponents() {
        if (extractableComponents == null) {
            this.extractableComponents = new ArrayList<>();
        }
        return extractableComponents;
    }

    public void setExtractableComponents(final List<FsItemExtractableComponent> extractableComponents) {
        this.extractableComponents = extractableComponents;
    }

    @Override
    public String toString() {
        return "FsItem{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
