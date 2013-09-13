package net.eledge.android.eu.europeana.gui.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.eledge.android.eu.europeana.Preferences;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.db.dao.BlogArticleDao;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.eu.europeana.gui.adapter.BlogAdapter;
import net.eledge.android.eu.europeana.tools.RssReader;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HomeBlogFragment extends Fragment implements TaskListener<List<BlogArticle>> {

	private ListView mListView;

	private BlogAdapter mBlogAdapter;

	private BlogArticleDao mBlogArticleDao;
	
	private RssReader mRssReaderTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBlogAdapter = new BlogAdapter(getActivity(), new ArrayList<BlogArticle>());

		boolean doupdate = false;
		SharedPreferences settings = getActivity().getSharedPreferences(Preferences.BLOG, 0);
		long time = settings.getLong(Preferences.BLOG_LASTUPDATE, -1);
		if (time == -1) {
			doupdate = true;
		} else {
			Calendar timelimit = Calendar.getInstance();
			timelimit.add(Calendar.HOUR, -24);
			Calendar lastUpdate = Calendar.getInstance();
			lastUpdate.setTime(new Date(time));
			if (timelimit.after(lastUpdate)) {
				doupdate = true;
			}
		}
		if (doupdate) {
			mRssReaderTask = new RssReader(getActivity(), this);
			mRssReaderTask.execute(UriHelper.URL_BLOGFEED);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.fragment_home_blog, null);
		mListView = (ListView) root.findViewById(R.id.fragment_home_blog_listview);
		mListView.setAdapter(mBlogAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BlogArticle article = mBlogAdapter.getItem(position);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.guid));
				startActivity(browserIntent);
			}
		});
		loadFromDatabase();

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (mRssReaderTask != null) {
			mRssReaderTask.cancel(true);
		}
		super.onDestroy();
	}

	@Override
	public void onTaskFinished(List<BlogArticle> articles) {
		if (articles != null) {
			showArticles(articles);
			mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(getActivity()));
			mBlogArticleDao.deleteAll();
			mBlogArticleDao.store(articles);
			mBlogArticleDao.close();
			SharedPreferences settings = getActivity().getSharedPreferences(Preferences.BLOG, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(Preferences.BLOG_LASTUPDATE, new Date().getTime());
			editor.commit();
		}
	}

	private void loadFromDatabase() {
		mBlogArticleDao = new BlogArticleDao(new DatabaseSetup(getActivity()));
		List<BlogArticle> articles = mBlogArticleDao.findAll();
		mBlogArticleDao.close();
		showArticles(articles);
	}
	
	private void showArticles(List<BlogArticle> articles) {
		if (articles != null) {
			mBlogAdapter.clear();
			for (BlogArticle article: articles) {
				mBlogAdapter.add(article);
			}
			mBlogAdapter.notifyDataSetChanged();
		}
	}
}
