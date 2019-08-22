package com.fsse.parser.service;

import com.fsse.model.FsItem;

import java.util.List;

public interface DatabaseOutputService {
    void persistToDatabase(final List<FsItem> items);
}
