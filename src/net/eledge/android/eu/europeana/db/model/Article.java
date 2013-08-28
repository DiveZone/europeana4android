package net.eledge.android.eu.europeana.db.model;

import java.net.URL;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import android.provider.BaseColumns;

@Entity(name="blog_articles")
public class Article implements BaseColumns {
	
	@Id
	@Column(name=_ID)
	public String guid;
	
	public String title;
	public String description;
	public Date pubDate;
	public String author;
	public URL url;
	
}
