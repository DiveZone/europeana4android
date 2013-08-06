package net.eledge.android.eu.europeana.search;

import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.toolkit.json.JsonParser;


public class RecordController {

	public final static RecordController instance = new RecordController();
	
	private final JsonParser<Record> jsonParser;
	
	public RecordController() {
		jsonParser = new JsonParser<Record>(Record.class);
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
