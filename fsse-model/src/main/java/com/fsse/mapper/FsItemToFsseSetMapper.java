package com.fsse.mapper;

import com.fsse.model.FsItem;
import com.fsse.model.attribute.ItemType;
import com.fsse.model.fsse.FsseSet;
import com.fsse.model.parts.FsItemSetStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FsItemToFsseSetMapper implements Mapper<FsItem, FsseSet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsItemToFsseSetMapper.class);

    @Override
    public FsseSet from(FsItem source) {
        throw new IllegalStateException("Only list conversion is allowed.");
    }

    @Override
    public List<FsseSet> from(final List<FsItem> sources) {
        final List<FsItem> wearableItems = sources.stream()
                .sorted(Comparator.comparing(FsItem::getId))
                .filter(item -> ItemType.getWearableItemTypes().contains(item.getType()))
                .collect(Collectors.toList());

        final Map<BigInteger, FsseSet> setMap = new TreeMap<>();
        for (final FsItem fsItem : wearableItems) {
            if (fsItem.getSetStatistics() != null) {
                FsItemSetStatistics setStatistics = fsItem.getSetStatistics();
                FsseSet set = safeGet(setMap, setStatistics.getSetId());

                set.setName(setStatistics.getSetName());
                if (set.getMinLevel().intValue() < fsItem.getLevel().intValue()) {
                    set.setMinLevel(fsItem.getLevel());
                }

                set.getItemStatistics().setAttack(set.getItemStatistics().getAttack().add(fsItem.getStatistics().getAttack()));
                set.getItemStatistics().setDefense(set.getItemStatistics().getDefense().add(fsItem.getStatistics().getDefense()));
                set.getItemStatistics().setArmor(set.getItemStatistics().getArmor().add(fsItem.getStatistics().getArmor()));
                set.getItemStatistics().setDamage(set.getItemStatistics().getDamage().add(fsItem.getStatistics().getDamage()));
                set.getItemStatistics().setHp(set.getItemStatistics().getHp().add(fsItem.getStatistics().getHp()));
                set.getItemStatistics().setXpGain(set.getItemStatistics().getXpGain().add(fsItem.getStatistics().getXpGain()));
                set.getItemStatistics().setStamina(set.getItemStatistics().getStamina().add(fsItem.getStatistics().getStamina()));
                set.getItemStatistics().setStaminaGain(set.getItemStatistics().getStaminaGain().add(fsItem.getStatistics().getStaminaGain()));
                set.getItemStatistics().setGoldGain(set.getItemStatistics().getGoldGain().add(fsItem.getStatistics().getGoldGain()));

                set.setSetStatistics(setStatistics);

                if (!set.getParts().contains(fsItem.getId())) {
                    set.getParts().add(fsItem.getId());
                } else {
                    LOGGER.warn("Duplicate item in set. Set ID: {} Item ID: {}", set.getId(), fsItem.getId());
                }
            }
        }

        return new ArrayList<>(setMap.values());
    }

    private FsseSet safeGet(final Map<BigInteger, FsseSet> setMap, final BigInteger setId) {
        if (!setMap.containsKey(setId)) {
            final FsseSet set = new FsseSet();
            set.setId(setId);
            setMap.put(setId, set);
        }
        return setMap.get(setId);
    }
}
