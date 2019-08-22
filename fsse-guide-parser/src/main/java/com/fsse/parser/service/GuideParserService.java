package com.fsse.parser.service;

import com.fsse.model.FsItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
public interface GuideParserService {

    int getAllItemPages();

    void parseListItemPage(final CompletableFuture<List<String>> result, final int page);

    void parseItemPage(final CompletableFuture<FsItem> result, final String itemId);
}
