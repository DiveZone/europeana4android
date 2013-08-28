package net.eledge.android.eu.europeana.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.model.Article;
import net.eledge.android.eu.europeana.gui.adapter.BlogAdapter;
import net.eledge.android.eu.europeana.tools.RssReader;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HomeBlogFragment extends Fragment implements TaskListener<List<Article>> {
	
	private ListView mListView;
	
	private BlogAdapter mBlogAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBlogAdapter = new BlogAdapter(getActivity(), new ArrayList<Article>());
		RssReader reader = new RssReader(getActivity(), this);
		reader.execute(UriHelper.URL_BLOGFEED);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.fragment_home_blog, null);
		mListView = (ListView) root.findViewById(R.id.fragment_home_blog_listview);
		mListView.setAdapter(mBlogAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Article article = mBlogAdapter.getItem(position);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.guid));
				startActivity(browserIntent);
			}
		});
		
		return root;
	}
	
	
	@Override
	public void onTaskFinished(List<Article> articles) {
		if (articles != null) {
			mBlogAdapter.clear();
			mBlogAdapter.addAll(articles);
			mBlogAdapter.notifyDataSetChanged();
		}
	}

}
