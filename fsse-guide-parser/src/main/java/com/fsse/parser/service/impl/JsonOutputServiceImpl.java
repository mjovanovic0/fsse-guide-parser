package com.fsse.parser.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsse.mapper.FsItemToFsseSetMapper;
import com.fsse.mapper.FsToFsseItemMapper;
import com.fsse.model.FsItem;
import com.fsse.model.attribute.ItemType;
import com.fsse.model.fsse.FsseItem;
import com.fsse.model.fsse.FsseSet;
import com.fsse.parser.service.JsonOutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JsonOutputServiceImpl implements JsonOutputService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonOutputServiceImpl.class);

    private static final File FOLDER;
    static {
        final String outputFolder = System.getenv("FSSE_OUTPUT_FOLDER");
        if (outputFolder != null) {
            FOLDER = new File(outputFolder);
            LOGGER.info("Using configured output folder at: '{}'.", outputFolder);
        } else {
            try {
                FOLDER = File.createTempFile("fsse-", "-temp");
            } catch (final IOException e) {
                throw new IllegalArgumentException("Fail to create output folder.", e);
            }
            LOGGER.warn("Output folder not configured. Using temporary folder at: {}", FOLDER.getAbsolutePath());
        }
    }

    private static final File FS_ITEMS_FILE = new File(FOLDER, "items.json");
    private static final File FSSE_ITEMS_FILE = new File(FOLDER, "fsse-items.json");
    private static final File FSSE_ITEMS_MINIFIED_FILE = new File(FOLDER, "fsse-items.min.json");
    private static final File FSSE_SETS_FILE = new File(FOLDER, "fsse-sets.json");
    private static final File FSSE_SETS_MINIFIED_FILE = new File(FOLDER, "fsse-sets.min.json");
    private static final File FSSE_SETS_EXTENDED_MINIFIED_FILE = new File(FOLDER, "fsse-sets-extended.min.json");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final FsToFsseItemMapper FS_TO_FSSE_ITEM_MAPPER = new FsToFsseItemMapper();
    private static final FsItemToFsseSetMapper FS_ITEM_TO_FSSE_SET_MAPPER = new FsItemToFsseSetMapper();
    private static final String JSON_ARRAY_START = "[\n  ";
    private static final String JSON_ARRAY_END = "\n]";
    private static final String JSON_ARRAY_ITEM_SEPARATOR = ",\n  ";

    @Override
    public void persistToOutputFolder(final List<FsItem> fsItems) {
        // Convert FsItem to FSSEItem for FSSE_ITEMS_FILE
        LOGGER.info("Converting FS item list to FSSE item list...");
        final List<FsseItem> fsseItems = FS_TO_FSSE_ITEM_MAPPER.from(fsItems);

        // Convert FsItems to FSSESet list
        LOGGER.info("Converting FS item list to FSSE set list...");
        final List<FsseSet> fsseSets = FS_ITEM_TO_FSSE_SET_MAPPER.from(fsItems);

        // Convert FSSEItem list to FSSEItemMinified list for FSSE_ITEMS_MINIFIED_FILE
        LOGGER.info("Converting FSSE item list to minified FSSE item list...");
        final List<FsseItem.Minified> fsseItemsMinified = fsseItems.stream()
                .filter(item -> ItemType.getWearableItemTypes().contains(item.getType()))
                .map(FsseItem.Minified::new)
                .collect(Collectors.toList());

        // Convert FSSESet list to FSSESetMinified list for FSSE_ITEMS_MINIFIED_FILE
        LOGGER.info("Converting FSSE set list to minified FSSE set list...");
        final List<FsseSet.Minified> fsseSetsMinified = fsseSets.stream()
                .map(FsseSet.Minified::new)
                .collect(Collectors.toList());

        // Convert FSSESet list to FSSESetMinified list for FSSE_ITEMS_MINIFIED_FILE
        LOGGER.info("Converting FSSE set list to minified FSSE set list...");
        final List<FsseSet.ExtendedMinified> fsseSetsExtendedMinified = fsseSets.stream()
                .map(set -> new FsseSet.ExtendedMinified(set, set.getParts().stream().map(itemId -> new FsseItem.Minified(FS_TO_FSSE_ITEM_MAPPER.from(fsItems.stream().filter(i -> i.getId().equals(itemId)).findFirst().get()))).collect(Collectors.toList())))
                .collect(Collectors.toList());

        // Create output folder if does not exists
        if (!FOLDER.exists()) {
            final boolean isCreated = FOLDER.mkdirs();
            if (!isCreated) {
                LOGGER.warn("Fail to create folder at: '{}'", FOLDER.getAbsolutePath());
            }
        }

        LOGGER.info("Persisting all as JSON in output folder...");
        persistToJsonFile(FS_ITEMS_FILE, fsItems);
        persistToJsonFile(FSSE_ITEMS_FILE, fsseItems);
        persistToJsonFile(FSSE_ITEMS_MINIFIED_FILE, fsseItemsMinified);
        persistToJsonFile(FSSE_SETS_FILE, fsseSets);
        persistToJsonFile(FSSE_SETS_MINIFIED_FILE, fsseSetsMinified);
        persistToJsonFile(FSSE_SETS_EXTENDED_MINIFIED_FILE, fsseSetsExtendedMinified);

        LOGGER.info("Persisting all as JSON in output folder completed.");
    }

    private <T> void persistToJsonFile(final File outputFile, final List<T> items) {
        final StringBuilder sb = new StringBuilder(JSON_ARRAY_START);
        for (final Object item : items) {
            try {
                sb.append(OBJECT_MAPPER.writeValueAsString(item)).append(JSON_ARRAY_ITEM_SEPARATOR);
            } catch (final JsonProcessingException e) {
                LOGGER.error("Fail to convert to JSON item: '{}'", item);
            }
        }
        sb.setLength(sb.length() - JSON_ARRAY_ITEM_SEPARATOR.length());
        sb.append(JSON_ARRAY_END);

        try {
            Files.write(outputFile.toPath(), sb.toString().getBytes());
        } catch (final IOException e) {
            LOGGER.error("Fail to persist final JSON file to disk.", e);
        }
    }
}
