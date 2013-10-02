package net.eledge.android.eu.europeana.gui.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.toolkit.gui.GuiUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BlogAdapter extends ArrayAdapter<BlogArticle> {
	
	private LayoutInflater inflater;
	
	DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
	
	public BlogAdapter(Context context, List<BlogArticle> articles) {
		super(context, 0, articles);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ArticleViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_home_blog, parent, false);
			holder = new ArticleViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_title);
			holder.content = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_text);
			holder.author = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_author);
			holder.date = (TextView) convertView.findViewById(R.id.listitem_home_blog_textview_date);
			
			convertView.setTag(holder);
		} else {
			holder = (ArticleViewHolder) convertView.getTag();
		}
		
		BlogArticle article = getItem(position);
		holder.title.setText(article.title);
		holder.content.setText(article.description);
		holder.author.setText(GuiUtils.format(getContext(), R.string.fragment_home_blog_posted, article.author));
		holder.date.setText(formatter.format(article.pubDate));
		
		return convertView;
	}

	private class ArticleViewHolder {
		TextView title = null;
		TextView content = null;
		TextView author = null;
		TextView date = null;
	}
	
}
