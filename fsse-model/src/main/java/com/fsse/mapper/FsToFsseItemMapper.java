package com.fsse.mapper;

import com.fsse.model.FsItem;
import com.fsse.model.fsse.FsseItem;

public class FsToFsseItemMapper implements Mapper<FsItem, FsseItem> {
    @Override
    public FsseItem from(final FsItem source) {
        final FsseItem fsseItem = new FsseItem();
        fsseItem.setId(source.getId());
        fsseItem.setName(source.getName());
        fsseItem.setRarity(source.getRarity());
        fsseItem.setType(source.getType());
        fsseItem.setLevel(source.getLevel());
        fsseItem.setStatistics(source.getStatistics());
        fsseItem.setSetName(source.getSetStatistics() != null ? source.getSetStatistics().getSetName() : null);
        fsseItem.setEnhancements(source.getEnhancements());
        return fsseItem;
    }
}
