package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;

import org.apache.commons.lang.StringUtils;

/**
 * using http://en.wikipedia.org/wiki/ISO_3166-2
 */
public enum Country {

	AT(R.string.country_at, "austria"),
	BE(R.string.country_be, "belgium"),
	BG(R.string.country_bg, "bulgaria"),
	CH(R.string.country_ch, "switzerland"),
	CY(R.string.country_cy, "cyprus"),
	CZ(R.string.country_cz, "czechrepublic"),
	DE(R.string.country_de, "germany"),
	DK(R.string.country_dk, "denmark"),
	EE(R.string.country_ee, "estonia"),
	ES(R.string.country_es, "spain"),
	FI(R.string.country_fi, "finland"),
	FR(R.string.country_fr, "france"),
	GR(R.string.country_gr, "greece"),
	HR(R.string.country_hr, "croatia"),
	HU(R.string.country_hu, "hungary"),
	IE(R.string.country_ie, "ireland"),
	IS(R.string.country_is, "iceland"),
	IT(R.string.country_it, "italy"),
	LT(R.string.country_lt, "lithuania"),
	LU(R.string.country_lu, "luxembourg"),
	LV(R.string.country_lv, "latvia"),
	MK(R.string.country_mk, "macedonia"),
	MT(R.string.country_mt, "malta"),
	NL(R.string.country_nl, "netherlands"),
	NO(R.string.country_no, "norway"),
	PL(R.string.country_pl, "poland"),
	PT(R.string.country_pt, "portugal"),
	RO(R.string.country_ro, "romania"),
	RS(R.string.country_rs, "serbia"),
	RU(R.string.country_ru, "russia"),
	SE(R.string.country_se, "sweden"),
	SI(R.string.country_si, "slovenia"),
	SK(R.string.country_sk, "slovakia"),
	TR(R.string.country_tr, "turkey"),
	UA(R.string.country_ua, "ukraine"),
	UK(R.string.country_uk, "unitedkingdom"),
	
	EU(R.string.country_eu, "europe");

	private final String hardcoded;
	public final int resourceId;

	private Country(int resource, String hardcoded) {
		this.hardcoded = hardcoded;
		this.resourceId = resource;
	}

	public static Country safeValueOf(String string) {
		if (StringUtils.isNotBlank(string)) {
			string = StringUtils.replaceChars(string, " _", "");
			string = StringUtils.lowerCase(string);
			for (Country c : values()) {
				if (c.hardcoded.equals(string)) {
					return c;
				}
			}
		}
		return null;
	}

}
