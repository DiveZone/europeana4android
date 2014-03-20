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

package net.eledge.android.eu.europeana.search.model.record.abstracts;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

public class Resource {

    public String about;

    protected static Map<String, String[]> mergeMapArrays(Map<String, String[]> source1, Map<String, String[]> source2) {
        Map<String, String[]> merged = new HashMap<>();
        if (source1 != null) {
            merged.putAll(source1);
        } else {
            return source2;
        }
        if (source2 != null) {
            for (String key : source2.keySet()) {
                if (merged.containsKey(key)) {
                    merged.put(key, mergeArray(merged.get(key), source2.get(key)));
                } else {
                    merged.put(key, source2.get(key));
                }
            }
        }
        return merged;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] mergeArray(T[] array1, T[] array2) {
        return ArrayUtils.addAll(array1, array2);
    }

    public static String[] getPreferred(Map<String, String[]> data, String locale) {
        if (data != null) {
            String key = null;
            if ((locale != null) && data.containsKey(locale)) {
                key = locale;
            } else if (data.containsKey("def")) {
                key = "def";
            } else if (data.containsKey("en")) {
                key = "en";
            }
            if (key == null) {
                key = data.keySet().iterator().next();
            }
            return data.get(key);
        }
        return null;
    }

    protected static <T> T defaultValue(T value1, T value2) {
        if (value1 != null) {
            return value1;
        }
        return value2;
    }

}
