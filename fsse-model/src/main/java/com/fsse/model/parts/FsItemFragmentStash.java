package com.fsse.model.parts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.FsItem;
import com.fsse.model.attribute.Rarity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigInteger;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM_FRAGMENT_STASH")
@GenericGenerator(name = "FS_ITEM_FRAGMENT_STASH_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_FRAGMENT_STASH_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemFragmentStash {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_FRAGMENT_STASH_SEQ")
    private Integer id;

    @JsonIgnore
    @ManyToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Type(type = "com.fsse.hibernate.CustomEnumType", parameters = { //
            @Parameter(name = "enumClassName", value = "com.fsse.model.attribute.Rarity"), //
            @Parameter(name = "enumPropertyName", value = "id"), //
            @Parameter(name = "type", value = "integer") //
    })
    @Column(name = "RARITY")
    private Rarity rarity;

    @Column(name = "MIN")
    private BigInteger min;

    @Column(name = "MAX")
    private BigInteger max;

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

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public BigInteger getMin() {
        return min;
    }

    public void setMin(BigInteger min) {
        this.min = min;
    }

    public BigInteger getMax() {
        return max;
    }

    public void setMax(BigInteger max) {
        this.max = max;
    }
}
