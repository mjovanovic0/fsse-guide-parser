package com.fsse.parser.service.impl;

import com.fsse.model.FsItem;
import com.fsse.parser.service.GuideParserService;
import com.fsse.parser.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
@Component
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final GuideParserService guideParserService;

    public ItemServiceImpl(final GuideParserService guideParserService) {
        this.guideParserService = guideParserService;
    }

    @Override
    public List<FsItem> parseItems() {
        // Get total pages to parse
        final int pages = guideParserService.getAllItemPages();
        LOGGER.info("Total of {} pages to parse", pages);

        // Get all item ids from all pages to parse
        final List<String> itemIds = getItemIds(pages);
        LOGGER.info("Total of {} FS Items found for parse", itemIds.size());

        // Parse each item id and obtain list of items for FS_ITEMS_FILE
        final List<FsItem> fsItems = getItems(itemIds).stream().sorted(Comparator.comparing(FsItem::getId)).collect(Collectors.toList());
        LOGGER.info("Resolved total of {} fsItems", fsItems.size());

        // Check and validate that there are no duplicates parsed
        LOGGER.info("Detecting duplicated records...");
        final Map<BigInteger, FsItem> itemMap = new HashMap<>();
        for (final FsItem fsItem : fsItems) {
            if (itemMap.containsKey(fsItem.getId())) {
                LOGGER.error("Duplicate FS Item detected. Item ID: '{}'", fsItem);
            } else {
                itemMap.put(fsItem.getId(), fsItem);
            }
        }

        LOGGER.info("Parsing FS Items completed!");

        return fsItems;
    }

    private List<String> getItemIds(final int pages) {
        final List<String> itemIds = new ArrayList<>();

        // Dispatch async requests
        final List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int page = 0; page <= pages; page++) {
            final CompletableFuture<List<String>> result = new CompletableFuture<>();
            guideParserService.parseListItemPage(result, page);
            futures.add(result);
        }

        // Combine all futures
        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            // Wait for all futures to finish
            allFutures.get();

            // Add all item urls
            itemIds.addAll(futures.stream().map(CompletableFuture::join).flatMap(Collection::stream).collect(Collectors.toList()));
        } catch (final Exception e) {
            LOGGER.error("Error occurred while fetching items urls", e);
        }
        return itemIds;
    }

    private List<FsItem> getItems(final List<String> itemIds) {
        final List<FsItem> items = new ArrayList<>();

        // Dispatch async requests
        final List<CompletableFuture<FsItem>> futures = new ArrayList<>();
        for (final String itemId : itemIds) {
            final CompletableFuture<FsItem> result = new CompletableFuture<>();
            guideParserService.parseItemPage(result, itemId);
            futures.add(result);
        }

        // Combine all futures
        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            // Wait for all futures to finish
            allFutures.get();

            // Add all item to final list
            items.addAll(futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        } catch (final Exception e) {
            LOGGER.error("Error occurred while fetching items urls", e);
        }
        return items;
    }
}
