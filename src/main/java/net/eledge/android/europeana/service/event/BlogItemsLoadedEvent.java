package net.eledge.android.europeana.service.event;

import net.eledge.android.europeana.db.model.BlogArticle;

import java.util.List;

public class BlogItemsLoadedEvent {

    public final List<BlogArticle> articles;

    public BlogItemsLoadedEvent(List<BlogArticle> articles) {
        this.articles = articles;
    }

}
