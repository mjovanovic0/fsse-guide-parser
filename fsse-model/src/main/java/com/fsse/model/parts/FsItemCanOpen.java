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

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
@Entity
@Table(name = "FS_ITEM_CAN_OPEN")
@GenericGenerator(name = "FS_ITEM_CAN_OPEN_SEQ", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { //
                @Parameter(name = "sequence_name", value = "FS_ITEM_CAN_OPEN_SEQ"), //
                @Parameter(name = "initial_value", value = "1"), //
                @Parameter(name = "increment_size", value = "1") //
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FsItemCanOpen {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FS_ITEM_CAN_OPEN_SEQ")
    private Integer id;

    @JsonIgnore
    @ManyToOne(targetEntity = FsItem.class)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private FsItem item;

    @Column(name = "CHEST_NAME")
    private String chestName;

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

    public String getChestName() {
        return chestName;
    }

    public void setChestName(String chestName) {
        this.chestName = chestName;
    }
}
