package net.eledge.android.eu.europeana.db.dao;

import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.toolkit.db.abstracts.Dao;

public class BlogArticleDao extends Dao<BlogArticle> {
	
	public BlogArticleDao(DatabaseSetup helper) {
		super(BlogArticle.class, helper);
	}
	
}
