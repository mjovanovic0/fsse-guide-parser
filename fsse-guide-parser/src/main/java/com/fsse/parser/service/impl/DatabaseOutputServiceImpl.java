package com.fsse.parser.service.impl;

import com.fsse.model.FsItem;
import com.fsse.parser.dao.FsItemRepository;
import com.fsse.parser.service.DatabaseOutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseOutputServiceImpl implements DatabaseOutputService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseOutputServiceImpl.class);

    private final FsItemRepository fsItemRepository;

    public DatabaseOutputServiceImpl(final FsItemRepository fsItemRepository) {
        this.fsItemRepository = fsItemRepository;
    }

    @Override
    public void persistToDatabase(final List<FsItem> fsItems) {
        LOGGER.info("Persisting FS items into database...");
        for (final FsItem fsItem : fsItems) {
            try {
                fsItemRepository.save(fsItem);
            } catch (final Exception e) {
                LOGGER.warn("Fail to persist FS Item {}", fsItem.getId(), e);
            }
        }
    }
}
