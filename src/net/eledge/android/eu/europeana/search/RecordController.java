package net.eledge.android.eu.europeana.search;


public class RecordController {

	public final static RecordController instance = new RecordController();
	
	public String getPortalUrl() {
//		try {
//			return UriHelper.createPortalUrl(terms.toArray(new String[terms
//					.size()]));
//		} catch (UnsupportedEncodingException e) {
			return "http://europeana.eu";
//		}
	}
	
}
