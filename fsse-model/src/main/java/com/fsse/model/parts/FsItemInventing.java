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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM_INVENTING")
@GenericGenerator(name = "FS_ITEM_INVENTING_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_INVENTING_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemInventing {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_INVENTING_SEQ")
    private Integer id;

    @JsonIgnore
    @OneToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Column(name = "INVENTING_LEVEL")
    private BigInteger inventingLevel;

    @Column(name = "SUCCESS_RATE")
    private BigDecimal successRate;

    @Column(name = "CREATES_ITEM_ID")
    private BigInteger itemId;

    @Column(name = "CREATES_ITEM_NAME")
    private String itemName;

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

    public BigInteger getInventingLevel() {
        return inventingLevel;
    }

    public void setInventingLevel(BigInteger inventingLevel) {
        this.inventingLevel = inventingLevel;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public BigInteger getItemId() {
        return itemId;
    }

    public void setItemId(BigInteger itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
