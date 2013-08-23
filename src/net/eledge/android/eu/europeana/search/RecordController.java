package net.eledge.android.eu.europeana.search;

import java.util.HashMap;
import java.util.Map;

import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.eu.europeana.search.task.RecordTask;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.json.JsonParser;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;

public class RecordController {

	public final static RecordController _instance = new RecordController();

	public final JsonParser<Record> jsonParser;

	private String currentRecordId;

	public Record record;

	private RecordTask mRecordTask;

	public Map<String, TaskListener<Record>> listeners = new HashMap<String, TaskListener<Record>>();

	private RecordController() {
		// Singleton
		jsonParser = new JsonParser<Record>(Record.class);
	}

	public void readRecord(Activity activity, String id) {
		if (StringUtils.isNotBlank(id)) {
			if ( (record != null) && StringUtils.equals(id, record.id)) {
				// don't load the same record twice but do notify listeners!;
				activity.runOnUiThread(new ListenerNotifier<Record>(listeners.values(), record));
				return;
			}
			record = null;
			currentRecordId = id;
			if (mRecordTask != null) {
				mRecordTask.cancel(true);
			}
			mRecordTask = new RecordTask(activity);
			mRecordTask.execute(id);
		}
	}

	public String getCurrentRecordId() {
		return currentRecordId;
	}

	public void registerListener(Class<?> clazz, TaskListener<Record> listener) {
		listeners.put(clazz.getName(), listener);
	}

	public void unregister(Class<?> clazz) {
		listeners.remove(clazz.getName());
	}

	public String getPortalUrl() {
		return UriHelper.getPortalRecordUrl(currentRecordId);
	}

}
