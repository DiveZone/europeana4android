package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;

import org.apache.commons.lang.StringUtils;

public enum Language {

	BG(R.string.locale_bg),
	CA(R.string.locale_ca),
	CS(R.string.locale_cs),
	DA(R.string.locale_da),
	DE(R.string.locale_de),
	EL(R.string.locale_le),
	EN(R.string.locale_en),
	ES(R.string.locale_es),
	ET(R.string.locale_et),
	EU(R.string.locale_eu),
	FI(R.string.locale_fi),
	FR(R.string.locale_fr),
	HU(R.string.locale_hu),
	IT(R.string.locale_it),
	LE(R.string.locale_le),
	NL(R.string.locale_nl),
	NO(R.string.locale_no),
	PL(R.string.locale_pl),
	PT(R.string.locale_pt),
	RO(R.string.locale_ro),
	RU(R.string.locale_ru),
	SK(R.string.locale_sk),
	SR(R.string.locale_sr),
	SV(R.string.locale_sv),

	MUL(R.string.locale_mul);

	public int resourceId;

	private Language(int resourceId) {
		this.resourceId = resourceId;
	}

	public static Language safeValueOf(String name) {
		for (Language langauge : values()) {
			if (StringUtils.equalsIgnoreCase(langauge.toString(), name)) {
				return langauge;
			}
		}
		return null;
	}

}
