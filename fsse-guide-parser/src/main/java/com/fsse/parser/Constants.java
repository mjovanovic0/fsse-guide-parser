package com.fsse.parser;

/**
 * @author milan@jovanovich.net
 * @since 09/09/2017
 */
public final class Constants {
    private Constants() {
    }

    public static final class Cdn {
        public static final String URL = "https://cdn2.fallensword.com";

        private Cdn() {
        }

        public static String itemImage(final int itemId) {
            return URL + "/items/" + itemId + ".gif";
        }
    }

    public static final class Guide {
        public static final String URL = "https://guide.fallensword.com";
        public static final String ITEMS_LIST = URL + "/index.php?cmd=items";

        private Guide() {

        }

        public static String itemsListPage(int index) {
            return URL + "/index.php?cmd=items&index=" + index;
        }

        public static String itemUrl(final String itemId) {
            return URL + "/index.php?cmd=items&subcmd=view&item_id=" + itemId;
        }
    }

}
