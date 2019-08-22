package com.fsse.parser.service.impl;

import com.fsse.model.FsItem;
import com.fsse.parser.Constants;
import com.fsse.parser.model.Header;
import com.fsse.parser.service.GuideParserService;
import com.fsse.parser.util.ParseUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
@Component
public class GuideParserServiceImpl implements GuideParserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuideParserServiceImpl.class);

    @Override
    public int getAllItemPages() {
        try {
            LOGGER.info("Fetching item pages count");

            final Document doc = Jsoup.connect(Constants.Guide.ITEMS_LIST).get();
            final Elements lastChilds = doc.select(".small a:last-child");
            final Element firstLastChild = lastChilds.get(0);

            // index.php?cmd=items&index=580&search_name=&search_level_min=&search_level_max=&search_type=-1&search_stat=-1&search_enhancement=-1&search_rarity=-1&search_set_id=-1&search_is_set=0&sort_by=1
            final String lastPageUrl = firstLastChild.attr("href");
            final String lastPageIndex = StringUtils.substringBetween(lastPageUrl, "index=", "&");

            final int lastPageIndexNumber = ParseUtil.toInt(lastPageIndex, "no item", Header.FIRST_HEADER, "Page Count");

            // Link: 'Last' is same number as last page, to not parse it twice, skip last page
            return lastPageIndexNumber - 1;
        } catch (final IOException e) {
            LOGGER.error("Error occurred while fetching page count.", e);
        }
        return 0;
    }

    @Async
    @Override
    public void parseListItemPage(final CompletableFuture<List<String>> result, final int page) {
        try {
            final List<String> itemUrls = new ArrayList<>();

            final Document doc = Jsoup.connect(Constants.Guide.itemsListPage(page)).get();
            final Elements itemLinks = doc.select("td[height='20'] a");
            for (final Element itemLink : itemLinks) {
                final String itemUrl = itemLink.attr("href");
                final String itemId = StringUtils.substringBetween(itemUrl, "item_id=", "&");

                itemUrls.add(itemId);
            }

            result.complete(itemUrls);
        } catch (IOException e) {
            result.complete(new ArrayList<>());
            LOGGER.error("Error occurred while grabbing urls for items on page " + page, e);
        }
    }

    @Async
    @Override
    public void parseItemPage(final CompletableFuture<FsItem> result, final String itemId) {
        try {
            final FsItem fsItem = new FsItem();
            final Document doc = fetchDocument(Constants.Guide.itemUrl(itemId));

            ParseUtil.parseItem(doc, fsItem, itemId);

            result.complete(fsItem);
        } catch (Exception e) {
            result.complete(null);
            LOGGER.error("Error occurred while parsing items with id " + itemId, e);
        }
    }

    private Document fetchDocument(final String url) {
        Document doc = null;
        while (doc == null) {
            try {
                doc = Jsoup.connect(url).get();
            } catch (final SocketTimeoutException e){
                // Ignore SocketTimeout as we will retry
            } catch (final IOException e) {
                LOGGER.warn("Fail to connect and fetch document at: '{}'.", url);
            }
        }
        return doc;
    }
}
