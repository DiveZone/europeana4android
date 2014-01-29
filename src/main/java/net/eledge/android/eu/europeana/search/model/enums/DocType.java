/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.eu.europeana.search.model.enums;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.toolkit.StringArrayUtils;

import org.apache.commons.lang.StringUtils;

public enum DocType {
    TEXT(R.string.doctype_text, ")"),
    IMAGE(R.string.doctype_image, "["),
    SOUND(R.string.doctype_sound, "]"),
    VIDEO(R.string.doctype_video, "("),
    _3D(R.string.doctype_3d, "{");

    public final int resourceId;
    public final String icon;

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
