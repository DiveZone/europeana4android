package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.StringUtils;

public enum DocType {
	TEXT(R.drawable.type_text), 
	IMAGE(R.drawable.type_image), 
	SOUND(R.drawable.type_sound),
	VIDEO(R.drawable.type_video), 
	_3D(R.drawable.type_3d);

	public int icon;

	private DocType(int icon) {
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
