package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.StringUtils;

public enum DocType {
	TEXT(")"), 
	IMAGE("["), 
	SOUND("]"),
	VIDEO("("), 
	_3D("{");

	public String icon;

	private DocType(String icon) {
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
