package com.fsse.parser.scheduler;

import com.fsse.model.FsItem;
import com.fsse.parser.service.DatabaseOutputService;
import com.fsse.parser.service.ItemService;
import com.fsse.parser.service.JsonOutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
@Component
public class ItemParseScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemParseScheduler.class);

    private final ItemService itemService;
    private final JsonOutputService jsonOutputService;
    private final DatabaseOutputService databaseOutputService;

    public ItemParseScheduler(final ItemService itemService, final JsonOutputService jsonOutputService, final DatabaseOutputService databaseOutputService) {
        this.itemService = itemService;
        this.jsonOutputService = jsonOutputService;
        this.databaseOutputService = databaseOutputService;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */6 * * *")
    public void start() {
        LOGGER.info("Item parsing started...");
        final List<FsItem> fsItems = itemService.parseItems();
        LOGGER.info("Items parsed.");

        LOGGER.info("Persisting JSON files...");
        jsonOutputService.persistToOutputFolder(fsItems);
        LOGGER.info("JSON files persisted.");

        LOGGER.info("Persisting FS Items in database...");
        databaseOutputService.persistToDatabase(fsItems);
        LOGGER.info("FS Items persisted.");
    }
}
