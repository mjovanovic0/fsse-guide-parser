package com.fsse.model.fsse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.parts.FsItemSetStatistics;
import com.fsse.model.parts.FsItemStatistics;

import java.math.BigInteger;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FsseStatisticsMinified {
    // Attack
    private BigInteger at;
    // Defense
    private BigInteger de;
    // Armor
    private BigInteger ar;
    // Damage
    private BigInteger da;
    // HP
    private BigInteger h;
    // XP Gain
    private BigInteger x;
    // Stamina
    private BigInteger s;
    // Stamina Gain
    private BigInteger sg;
    // Gold Gain
    private BigInteger g;

    public FsseStatisticsMinified(final FsItemStatistics stats) {
        // Null zero values so that result JSON size can be reduced
        this.at = ifNotZero(stats.getAttack());
        this.de = ifNotZero(stats.getDefense());
        this.ar = ifNotZero(stats.getArmor());
        this.da = ifNotZero(stats.getDamage());
        this.h = ifNotZero(stats.getHp());
        this.x = ifNotZero(stats.getXpGain());
        this.s = ifNotZero(stats.getStamina());
        this.sg = ifNotZero(stats.getStaminaGain());
        this.g = ifNotZero(stats.getGoldGain());
    }

    public FsseStatisticsMinified(final FsItemSetStatistics stats) {
        // Null zero values so that result JSON size can be reduced
        this.at = ifNotZero(stats.getAttack());
        this.de = ifNotZero(stats.getDefense());
        this.ar = ifNotZero(stats.getArmor());
        this.da = ifNotZero(stats.getDamage());
        this.h = ifNotZero(stats.getHp());
        this.x = ifNotZero(stats.getXpGain());
        this.s = ifNotZero(stats.getStamina());
        this.sg = ifNotZero(stats.getStaminaGain());
        this.g = ifNotZero(stats.getGoldGain());
    }

    private static BigInteger ifNotZero(final BigInteger value){
        return BigInteger.ZERO.equals(value) ? null : value;
    }

    public BigInteger getAt() {
        return at;
    }

    public BigInteger getDe() {
        return de;
    }

    public BigInteger getAr() {
        return ar;
    }

    public BigInteger getDa() {
        return da;
    }

    public BigInteger getH() {
        return h;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getS() {
        return s;
    }

    public BigInteger getSg() {
        return sg;
    }

    public BigInteger getG() {
        return g;
    }
}
