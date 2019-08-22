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
@Table(name = "FS_ITEM_SET_STATISTICS")
@GenericGenerator(name = "FS_ITEM_SET_STATISTICS_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_SET_STATISTICS_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemSetStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_SET_STATISTICS_SEQ")
    private Integer id;

    @JsonIgnore
    @OneToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Column(name = "SET_ID")
    private BigInteger setId;

    @Column(name = "SET_NAME")
    private String setName;

    @Column(name = "ATTACK")
    private BigInteger attack = BigInteger.ZERO;

    @Column(name = "DEFENSE")
    private BigInteger defense = BigInteger.ZERO;

    @Column(name = "ARMOR")
    private BigInteger armor = BigInteger.ZERO;

    @Column(name = "DAMAGE")
    private BigInteger damage = BigInteger.ZERO;

    @Column(name = "HP")
    private BigInteger hp = BigInteger.ZERO;

    @Column(name = "XP_GAIN")
    private BigInteger xpGain = BigInteger.ZERO;

    @Column(name = "STAMINA")
    private BigInteger stamina = BigInteger.ZERO;

    @Column(name = "STAMINA_GAIN")
    private BigInteger staminaGain = BigInteger.ZERO;

    @Column(name = "GOLD_GAIN")
    private BigInteger goldGain = BigInteger.ZERO;

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

    public BigInteger getSetId() {
        return setId;
    }

    public void setSetId(BigInteger setId) {
        this.setId = setId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public BigInteger getAttack() {
        return attack;
    }

    public void setAttack(BigInteger attack) {
        this.attack = attack;
    }

    public BigInteger getDefense() {
        return defense;
    }

    public void setDefense(BigInteger defense) {
        this.defense = defense;
    }

    public BigInteger getArmor() {
        return armor;
    }

    public void setArmor(BigInteger armor) {
        this.armor = armor;
    }

    public BigInteger getDamage() {
        return damage;
    }

    public void setDamage(BigInteger damage) {
        this.damage = damage;
    }

    public BigInteger getHp() {
        return hp;
    }

    public void setHp(BigInteger hp) {
        this.hp = hp;
    }

    public BigInteger getXpGain() {
        return xpGain;
    }

    public void setXpGain(BigInteger xpGain) {
        this.xpGain = xpGain;
    }

    public BigInteger getStamina() {
        return stamina;
    }

    public void setStamina(BigInteger stamina) {
        this.stamina = stamina;
    }

    public BigInteger getStaminaGain() {
        return staminaGain;
    }

    public void setStaminaGain(BigInteger staminaGain) {
        this.staminaGain = staminaGain;
    }

    public BigInteger getGoldGain() {
        return goldGain;
    }

    public void setGoldGain(BigInteger goldGain) {
        this.goldGain = goldGain;
    }
}
