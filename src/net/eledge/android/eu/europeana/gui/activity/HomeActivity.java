package net.eledge.android.eu.europeana.gui.activity;

import java.util.ArrayList;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.SuggestionAdapter;
import net.eledge.android.eu.europeana.gui.dialog.AboutDialog;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.Suggestion;
import net.eledge.android.toolkit.async.listener.TaskListener;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class HomeActivity extends Activity implements TaskListener<Suggestion[]>, OnItemClickListener {

	private SuggestionAdapter mSuggestionsAdaptor;

	private EditText mEditTextQuery;
	private GridView mGridViewSuggestions;

	private SearchController searchController = SearchController._instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		searchController.suggestionPagesize = getResources().getInteger(R.integer.home_suggestions_pagesize);

		mSuggestionsAdaptor = new SuggestionAdapter(this, new ArrayList<Suggestion>());

		mGridViewSuggestions = (GridView) findViewById(R.id.activity_home_gridview_suggestions);
		mGridViewSuggestions.setAdapter(mSuggestionsAdaptor);
		mGridViewSuggestions.setOnItemClickListener(this);

		mEditTextQuery = (EditText) findViewById(R.id.activity_home_edittext_query);
		mEditTextQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_SEARCH) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					performSearch(v.getText().toString());
					return true;
				}
				return false;
			}
		});
		mEditTextQuery.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 2) {
					if (mGridViewSuggestions.isShown()) {
						mSuggestionsAdaptor.clear();
						mSuggestionsAdaptor.notifyDataSetChanged();
					}
					searchController.suggestions(HomeActivity.this, s.toString());
				} else {
					onTaskFinished(null);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// ignore
			}
			@Override
			public void afterTextChanged(Editable s) {
				// ignore
			}
		});
		
//		Button button = (Button) findViewById(R.id.activity_home_button_testrecord);
//		button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String recordid = "/2021604/8ACD8560BCB47678B719C23AA1CB560182917A12";
//				final Intent intent = new Intent(HomeActivity.this, RecordActivity.class);
//				intent.setAction(Intent.ACTION_VIEW);
//				intent.putExtra(RecordActivity.RECORD_ID, recordid);
//				HomeActivity.this.startActivity(intent);
//			}
//		});

	}
	
	private void performSearch(String query) {
		final Intent intent = new Intent(this, SearchActivity.class);
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);
		this.startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Suggestion suggestion = mSuggestionsAdaptor.getItem(position);
		performSearch(suggestion.query);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			try {
				Dialog dialog = new AboutDialog(this, (EuropeanaApplication) getApplication(), getPackageManager().getPackageInfo(getPackageName(), 0));
				dialog.show();
			} catch (NameNotFoundException e) {
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onTaskFinished(Suggestion[] suggestions) {
		mSuggestionsAdaptor.clear();
		if ((suggestions != null) && (suggestions.length > 0)) {
			mSuggestionsAdaptor.addAll(suggestions);
			mSuggestionsAdaptor.notifyDataSetChanged();
			mGridViewSuggestions.setVisibility(View.VISIBLE);
		} else {
			mGridViewSuggestions.setVisibility(View.GONE);
		}
	}

}
