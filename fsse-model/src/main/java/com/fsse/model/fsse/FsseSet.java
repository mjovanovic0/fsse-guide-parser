package com.fsse.model.fsse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.attribute.StatType;
import com.fsse.model.parts.FsItemSetStatistics;
import com.fsse.model.parts.FsItemStatistics;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@Entity
//@Table(name = "FS_SET")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FsseSet {

    @Id
    @GeneratedValue
    private BigInteger id;

    @Column(name = "SET_NAME")
    private String name;

    @Column(name = "ATTACK")
    private BigInteger minLevel = BigInteger.ZERO;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemStatistics itemStatistics = new FsItemStatistics();

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private FsItemSetStatistics setStatistics = new FsItemSetStatistics();

    private List<BigInteger> parts = new ArrayList<>();

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

    public BigInteger getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(BigInteger minLevel) {
        this.minLevel = minLevel;
    }

    public FsItemStatistics getItemStatistics() {
        return itemStatistics;
    }

    public void setItemStatistics(FsItemStatistics itemStatistics) {
        this.itemStatistics = itemStatistics;
    }

    public FsItemSetStatistics getSetStatistics() {
        return setStatistics;
    }

    public void setSetStatistics(FsItemSetStatistics setStatistics) {
        this.setStatistics = setStatistics;
    }

    public List<BigInteger> getParts() {
        return parts;
    }

    public void setParts(List<BigInteger> parts) {
        this.parts = parts;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Minified {
        // Set ID
        private BigInteger i;
        // Set Name
        private String n;
        // Set Level
        private BigInteger l;
        // Item Statistics
        private FsseStatisticsMinified is;
        // Set Statistics
        private FsseStatisticsMinified ss;
        // Parts - List of ITem IDs in Set
        private List<BigInteger> p;

        public Minified(final FsseSet set) {
            this.i = set.getId();
            this.n = set.getName();
            this.l = set.getMinLevel();
            this.is = new FsseStatisticsMinified(set.getItemStatistics());
            this.ss = new FsseStatisticsMinified(set.getSetStatistics());
            this.p = set.getParts();
        }

        public BigInteger getI() {
            return i;
        }

        public String getN() {
            return n;
        }

        public BigInteger getL() {
            return l;
        }

        public FsseStatisticsMinified getIs() {
            return is;
        }

        public FsseStatisticsMinified getSs() {
            return ss;
        }

        public List<BigInteger> getP() {
            return p;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ExtendedMinified {
        // Set ID
        private BigInteger i;
        // Set Name
        private String n;
        // Set Level
        private BigInteger l;
        // Item Statistics
        private FsseStatisticsMinified is;
        // Set Statistics
        private FsseStatisticsMinified ss;
        // Item Types
        private List<Integer> it;
        // Stats Types
        private List<Integer> st;
        // Rarity Types
        private List<Integer> rt;
        // Parts - Minified items
        private List<FsseItem.Minified> p;

        public ExtendedMinified(final FsseSet set, final List<FsseItem.Minified> parts) {
            this.i = set.getId();
            this.n = set.getName();
            this.l = set.getMinLevel();
            this.is = new FsseStatisticsMinified(set.getItemStatistics());
            this.ss = new FsseStatisticsMinified(set.getSetStatistics());
            this.p = parts;

            // Add part types and part stats as list to relax complexity on frontend for filtering
            this.it = parts.stream().map(FsseItem.Minified::getT).collect(Collectors.toList());
            this.st = new ArrayList<>();
            this.rt = new ArrayList<>();

            if (this.is.getAt() != null || this.ss.getAt() != null) {
                this.st.add(StatType.ATTACK.getId());
            }
            if (this.is.getDe() != null || this.ss.getDe() != null) {
                this.st.add(StatType.DEFENSE.getId());
            }
            if (this.is.getAr() != null || this.ss.getAr() != null) {
                this.st.add(StatType.ARMOR.getId());
            }
            if (this.is.getDa() != null || this.ss.getDa() != null) {
                this.st.add(StatType.DAMAGE.getId());
            }
            if (this.is.getH() != null || this.ss.getH() != null) {
                this.st.add(StatType.HP.getId());
            }
            if (this.is.getX() != null || this.ss.getX() != null) {
                this.st.add(StatType.XP_GAIN.getId());
            }
            if (this.is.getS() != null || this.ss.getS() != null) {
                this.st.add(StatType.STAMINA.getId());
            }
            if (this.is.getSg() != null || this.ss.getSg() != null) {
                this.st.add(StatType.STAMINA_GAIN.getId());
            }
            if (this.is.getG() != null || this.ss.getG() != null) {
                this.st.add(StatType.GOLD_GAIN.getId());
            }

            for (final FsseItem.Minified item : this.p) {
                if(!this.rt.contains(item.getR())){
                    this.rt.add(item.getR());
                }
            }

            // Sort itemTypes and statsTypes so we can do easy array compare in frontend
            Collections.sort(it);
            Collections.sort(st);
            Collections.sort(rt);
        }

        public BigInteger getI() {
            return i;
        }

        public String getN() {
            return n;
        }

        public BigInteger getL() {
            return l;
        }

        public FsseStatisticsMinified getIs() {
            return is;
        }

        public FsseStatisticsMinified getSs() {
            return ss;
        }

        public List<FsseItem.Minified> getP() {
            return p;
        }

        public List<Integer> getIt() {
            return it;
        }

        public List<Integer> getSt() {
            return st;
        }

        public List<Integer> getRt() {
            return rt;
        }
    }
}
