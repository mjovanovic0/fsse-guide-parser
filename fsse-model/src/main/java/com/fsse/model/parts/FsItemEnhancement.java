package com.fsse.model.parts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsse.model.FsItem;
import com.fsse.model.attribute.Enhancement;
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
import java.math.BigDecimal;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM_ENHANCEMENT")
@GenericGenerator(name = "FS_ITEM_ENHANCEMENT_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_ENHANCEMENT_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemEnhancement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_ENHANCEMENT_SEQ")
    private Integer id;

    @JsonIgnore
    @ManyToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Type(type = "com.fsse.hibernate.CustomEnumType", parameters = { //
            @Parameter(name = "enumClassName", value = "com.fsse.model.attribute.Enhancement"), //
            @Parameter(name = "enumPropertyName", value = "id"), //
            @Parameter(name = "type", value = "integer") //
    })
    @Column(name = "ENHANCEMENT")
    private Enhancement enhancement;

    @Column(name = "PERCENTAGE")
    private BigDecimal percentage;

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

    public Enhancement getEnhancement() {
        return enhancement;
    }

    public void setEnhancement(Enhancement enhancement) {
        this.enhancement = enhancement;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
