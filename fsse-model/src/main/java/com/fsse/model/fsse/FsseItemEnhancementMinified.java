package com.fsse.model.fsse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.parts.FsItemEnhancement;

import java.math.BigDecimal;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FsseItemEnhancementMinified {

    // Enhancement ID
    private Integer i;
    // Percentage
    private BigDecimal p;

    public FsseItemEnhancementMinified(final FsItemEnhancement enhancement) {
        this.i = enhancement.getEnhancement().getId();
        this.p = enhancement.getPercentage();
    }

    public Integer getI() {
        return i;
    }

    public BigDecimal getP() {
        return p;
    }
}
