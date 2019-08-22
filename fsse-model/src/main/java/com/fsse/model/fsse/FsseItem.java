package com.fsse.model.fsse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.attribute.ItemType;
import com.fsse.model.attribute.Rarity;
import com.fsse.model.parts.FsItemEnhancement;
import com.fsse.model.parts.FsItemStatistics;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FsseItem {

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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private FsItemStatistics statistics;

    @Column(name = "SET_NAME")
    private String setName;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FsItemEnhancement> enhancements;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public BigInteger getLevel() {
        return level;
    }

    public void setLevel(BigInteger level) {
        this.level = level;
    }

    public FsItemStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(FsItemStatistics statistics) {
        this.statistics = statistics;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Minified {
        // ID
        private BigInteger i;
        // Name
        private String n;
        // Rarity ID
        private Integer r;
        // Type ID
        private Integer t;
        // Level
        private BigInteger l;
        // Item Statistics
        private FsseStatisticsMinified is;
        // Set Name
        private String sn;
        // Enhancements
        private List<FsseItemEnhancementMinified> e;

        public Minified(final FsseItem item) {
            this.i = item.getId();
            this.n = item.getName();
            this.r = item.getRarity().getId();
            this.t = item.getType().getId();
            this.l = item.getLevel();
            if (item.getStatistics() != null) {
                this.is = new FsseStatisticsMinified(item.getStatistics());
            }
            this.sn = item.getSetName();
            if (item.getEnhancements() != null) {
                this.e = item.getEnhancements().stream().map(FsseItemEnhancementMinified::new).collect(Collectors.toList());
            }
        }

        public BigInteger getI() {
            return i;
        }

        public String getN() {
            return n;
        }

        public Integer getR() {
            return r;
        }

        public Integer getT() {
            return t;
        }

        public BigInteger getL() {
            return l;
        }

        public FsseStatisticsMinified getIs() {
            return is;
        }

        public String getSn() {
            return sn;
        }

        public List<FsseItemEnhancementMinified> getE() {
            return e;
        }
    }
}
