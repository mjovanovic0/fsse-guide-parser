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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigInteger;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM_INGREDIENT")
@GenericGenerator(name = "FS_ITEM_INGREDIENT_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_INGREDIENT_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_INGREDIENT_SEQ")
    private Integer id;

    @JsonIgnore
    @ManyToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Column(name = "INGREDIENT_ID")
    private BigInteger ingredientId;

    @Column(name = "INGREDIENT_NAME")
    private String ingredientName;

    @Column(name = "AMOUNT")
    private BigInteger amount;

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

    public BigInteger getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(BigInteger ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}
