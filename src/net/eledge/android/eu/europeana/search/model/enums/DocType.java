package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.StringUtils;

public enum DocType {
	TEXT(R.string.doctype_text, ")"), 
	IMAGE(R.string.doctype_image, "["), 
	SOUND(R.string.doctype_sound, "]"), 
	VIDEO(R.string.doctype_video, "("), 
	_3D(R.string.doctype_3d, "{");

	public int resourceId;
	public String icon;

	private DocType(int resourceId, String icon) {
		this.resourceId = resourceId;
		this.icon = icon;
	}

	public static DocType safeValueOf(String[] strings) {
		if (StringArrayUtils.isNotBlank(strings)) {
			return safeValueOf(strings[0]);
		}
		return null;
	}

	public static DocType safeValueOf(String string) {
		if (StringUtils.isNotBlank(string)) {
			for (DocType t : values()) {
				if (t.toString().equalsIgnoreCase(string)) {
					return t;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return StringUtils.stripStart(super.toString(), "_");
	}

}
