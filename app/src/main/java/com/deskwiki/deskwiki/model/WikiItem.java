package com.deskwiki.deskwiki.model;

import java.util.Date;
import java.util.Objects;

/**
 * Model object to handle the query search result
 * Created by dibyenduroy on 3/4/15.
 */
public class WikiItem {
    public QueryItem query;

    public class QueryItem{

        public SearchItem[] search;

        public class SearchItem {
            int ns;
            public String title;
            public Date timestamp;

        }
    }

}
