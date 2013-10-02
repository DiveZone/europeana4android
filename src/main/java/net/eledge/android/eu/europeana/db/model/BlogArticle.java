package net.eledge.android.eu.europeana.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import android.provider.BaseColumns;

@Entity(name="blog_articles")
public class BlogArticle implements BaseColumns {
	
	@Id
	@Column(name=_ID)
	public Long id;
	
	@Column
	public String guid;
	
	@Column
	public String title;
	
	@Column
	public String description;
	
	@Column
	public Date pubDate;
	
	@Column
	public String author;
	
}
