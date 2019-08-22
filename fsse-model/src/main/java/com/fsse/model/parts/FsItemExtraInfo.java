package com.fsse.model.parts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.FsItem;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigInteger;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM_EXTRA_INFO")
@GenericGenerator(name = "FS_ITEM_EXTRA_INFO_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_EXTRA_INFO_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemExtraInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_EXTRA_INFO_SEQ")
    private Integer id;

    @JsonIgnore
    @OneToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Column(name = "EQUIPPED_COUNT")
    private BigInteger equippedCount;

    @Column(name = "BACKPACK_COUNT")
    private BigInteger backpackCount;

    @Column(name = "GUILD_TAGGED_COUNT")
    private BigInteger guildTaggedCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FsItem getItem() {
        return item;
    }

    public void setItem(FsItem item) {
        this.item = item;
    }

    public BigInteger getEquippedCount() {
        return equippedCount;
    }

    public void setEquippedCount(BigInteger equippedCount) {
        this.equippedCount = equippedCount;
    }

    public BigInteger getBackpackCount() {
        return backpackCount;
    }

    public void setBackpackCount(BigInteger backpackCount) {
        this.backpackCount = backpackCount;
    }

    public BigInteger getGuildTaggedCount() {
        return guildTaggedCount;
    }

    public void setGuildTaggedCount(BigInteger guildTaggedCount) {
        this.guildTaggedCount = guildTaggedCount;
    }
}
