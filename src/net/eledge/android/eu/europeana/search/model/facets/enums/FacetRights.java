package net.eledge.android.eu.europeana.search.model.facets.enums;

import net.eledge.android.toolkit.StringUtils;

public enum FacetRights {
	
	CC_ZERO("http://creativecommons.org/publicdomain/zero", "CC0", "icon-cczero"),

	CC_BY("http://creativecommons.org/licenses/by/", "CC BY", "icon-cc icon-by"),

	CC_BY_SA("http://creativecommons.org/licenses/by-sa/", "CC BY-SA", "icon-cc icon-by icon-sa"),

	CC_BY_NC_SA("http://creativecommons.org/licenses/by-nc-sa/", "CC BY-NC-SA", "icon-cc icon-by icon-nceu icon-sa"),

	CC_BY_ND("http://creativecommons.org/licenses/by-nd/", "CC BY-ND", "icon-cc icon-by icon-nd"),

	CC_BY_NC("http://creativecommons.org/licenses/by-nc/", "CC BY-NC", "icon-cc icon-by icon-nceu"),

	CC_BY_NC_ND("http://creativecommons.org/licenses/by-nc-nd/", "CC BY-NC-ND", "icon-cc icon-by icon-nceu icon-nd"),

	NOC("http://creativecommons.org/publicdomain/mark/", "Public Domain marked", "icon-pd"),

	EU_RR_F("http://www.europeana.eu/rights/rr-f/", "Free Access - Rights Reserved", "icon-copyright"),

	EU_RR_P("http://www.europeana.eu/rights/rr-p/", "Paid Access - Rights Reserved", "icon-copyright"),

	EU_RR_R("http://www.europeana.eu/rights/rr-r/", "Restricted Access - Rights Reserved", "icon-copyright"),

	EU_ORPHAN("http://www.europeana.eu/rights/test-orphan-work-test/", "Orphan Work", "icon-unknown"),

	EU_U("http://www.europeana.eu/rights/unknown/", "Unknown copyright status", "icon-unknown");

	private String url = null;
	private String rightsText = null;
	private String rightsIcon = null;

	/**
	 * Constructor for method
	 * 
	 * @param url
	 *            Url associated with the rights for the object
	 * @param rightsText
	 *            Text associated with the rights
	 * @param rightsIcon
	 *            Icon associated with the rights
	 */
	private FacetRights(String url, String rightsText, String rightsIcon) {
		this.url = url;
		this.rightsText = rightsText;
		this.rightsIcon = rightsIcon;
	}

	/**
	 * Returns the full Url associated with the rights
	 * 
	 * @return Full url associated with the rights
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Return text associated with the rights
	 * 
	 * @return text associated with the results
	 */
	public String getRightsText() {
		return rightsText;
	}

	/**
	 * Returns the url of the icon associated with the results
	 * 
	 * @return url of icon associated with the results
	 */
	public String getRightsIcon() {
		return rightsIcon;
	}

	public static FacetRights safeValueByUrl(String url) {
		if (StringUtils.isNotBlank(url)) {
			for (FacetRights option : FacetRights.values()) {
				if (url.contains(option.getUrl())) {
					return option;
				}
			}
		}
		return null;
	}

}
