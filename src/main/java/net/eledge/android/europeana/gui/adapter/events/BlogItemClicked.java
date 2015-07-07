package net.eledge.android.europeana.gui.adapter.events;

import net.eledge.android.europeana.db.model.BlogArticle;

public class BlogItemClicked {

    public final BlogArticle article;

    public BlogItemClicked(BlogArticle article) {
        this.article = article;
    }
}
