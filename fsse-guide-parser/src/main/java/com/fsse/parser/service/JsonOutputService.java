package com.fsse.parser.service;

import com.fsse.model.FsItem;

import java.util.List;

public interface JsonOutputService {

    void persistToOutputFolder(final List<FsItem> items);
}
