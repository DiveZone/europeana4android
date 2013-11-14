package net.eledge.android.eu.europeana.search;

import android.app.Activity;

import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.search.task.RecordTask;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RecordController {

	public final static RecordController _instance = new RecordController();

	private String currentRecordId;
    private boolean currentRecordSelected = false;

	public RecordObject record;

	private RecordTask mRecordTask;

	public Map<String, TaskListener<RecordObject>> listeners = new HashMap<>();

	public void readRecord(Activity activity, String id) {
		if (StringUtils.isNotBlank(id)) {
			if ( (record != null) && StringUtils.equals(id, record.about)) {
				// don't load the same record twice but do notify listeners!;
				activity.runOnUiThread(new ListenerNotifier<>(listeners.values(), record));
				return;
			}
			record = null;
			currentRecordId = id;
            currentRecordSelected = false;
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

    public boolean isCurrentRecordSelected() {
        return currentRecordSelected;
    }

    public void setCurrentRecordSelected(boolean selected) {
        currentRecordSelected = selected;
    }

	public void registerListener(Class<?> clazz, TaskListener<RecordObject> listener) {
		listeners.put(clazz.getName(), listener);
	}

	public void unregister(Class<?> clazz) {
		listeners.remove(clazz.getName());
	}

	public String getPortalUrl() {
		return UriHelper.getPortalRecordUrl(currentRecordId);
	}

}
