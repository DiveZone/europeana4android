package net.eledge.android.eu.europeana.search.model.enums;

import org.apache.commons.lang.StringUtils;

public enum Right {

    CC_ZERO("http://creativecommons.org/publicdomain/zero", "CC0", "u"),

    CC_BY("http://creativecommons.org/licenses/by/", "CC BY", "on"),

    CC_BY_SA("http://creativecommons.org/licenses/by-sa/", "CC BY-SA", "ont"),

    CC_BY_NC_SA("http://creativecommons.org/licenses/by-nc-sa/", "CC BY-NC-SA", "onqp"),

    CC_BY_ND("http://creativecommons.org/licenses/by-nd/", "CC BY-ND", "onr"),

    CC_BY_NC("http://creativecommons.org/licenses/by-nc/", "CC BY-NC", "onq"),

    CC_BY_NC_ND("http://creativecommons.org/licenses/by-nc-nd/", "CC BY-NC-ND", "onqr"),

    NOC("http://creativecommons.org/publicdomain/mark/", "Public Domain marked", "v"),

    EU_RR_F("http://www.europeana.eu/rights/rr-f/", "Free Access - Rights Reserved", "x"),

    EU_RR_P("http://www.europeana.eu/rights/rr-p/", "Paid Access - Rights Reserved", "x"),

    EU_RR_R("http://www.europeana.eu/rights/rr-r/", "Restricted Access - Rights Reserved", "x"),

    EU_ORPHAN("http://www.europeana.eu/rights/test-orphan-work-test/", "Orphan Work", "w"),

    EU_U("http://www.europeana.eu/rights/unknown/", "Unknown copyright status", "w");

    private String url = null;
    private String rightsText = null;
    private String fontIcon = null;

    /**
     * Constructor for method
     *
     * @param url        Url associated with the rights for the object
     * @param rightsText Text associated with the rights
     * @param fontIcon   Icon associated with the rights
     */
    private Right(String url, String rightsText, String fontIcon) {
        this.url = url;
        this.rightsText = rightsText;
        this.fontIcon = fontIcon;
    }

    /**
     * Returns the full Url associated with the rights
     *
     * @return Full url associated with the rights
     */
    String getUrl() {
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

    public String getFontIcon() {
        return fontIcon;
    }

    public static Right safeValueByUrl(String url) {
        if (StringUtils.isNotBlank(url)) {
            for (Right option : Right.values()) {
                if (url.contains(option.getUrl())) {
                    return option;
                }
            }
        }
        return null;
    }

}
