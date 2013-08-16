package net.eledge.android.eu.europeana.search;

import java.util.HashMap;
import java.util.Map;

import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.eu.europeana.search.task.RecordTask;
import net.eledge.android.toolkit.StringUtils;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.json.JsonParser;


public class RecordController {

	public final static RecordController instance = new RecordController();
	
	public final JsonParser<Record> jsonParser;
	
	private String currentRecordId;
	
	private RecordTask mRecordTask;
	
	public Map<String, TaskListener<Record>> listeners = new HashMap<String, TaskListener<Record>>();
	
	private RecordController() {
		// Singleton
		jsonParser = new JsonParser<Record>(Record.class);
	}
	
	public void readRecord(TaskListener<Record> listener, String id) {
		if (StringUtils.isNotBlank(id)) {
			currentRecordId = id;
			if (mRecordTask != null) {
				mRecordTask.cancel(true);
			}
			mRecordTask = new RecordTask();
			mRecordTask.execute(id);
		}
	}
	
	public String getCurrentRecordId() {
		return currentRecordId;
	}
	
	public void registerListener(String tag, TaskListener<Record> listener) {
		listeners.put(tag, listener);
	}

	public String getPortalUrl() {
//		try {
//			return UriHelper.createPortalUrl(terms.toArray(new String[terms
//					.size()]));
//		} catch (UnsupportedEncodingException e) {
			return "http://europeana.eu";
//		}
	}
	
}
