/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
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

package net.eledge.android.toolkit;

import android.util.SparseArray;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringArrayUtils {

    public static boolean isNotBlank(String[] array) {
        return ((array != null) && (array.length > 0)) && StringUtils.join(array).trim().length() > 0;
    }

    public static boolean isBlank(String[] array) {
        return !isNotBlank(array);
    }

    public static boolean areAllBlank(String[]... arrays) {
        boolean allBlank = true;
        for (String[] array : arrays) {
            allBlank &= isBlank(array);
        }
        return allBlank;
    }

    public static String[] toArray(String... items) {
        return items;
    }

    public static String[] toArray(List<String> list) {
        if (list != null) {
            return list.toArray(new String[list.size()]);
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    public static String[] toArray(SparseArray<List<String>> sparseArray) {
        List<String> list = new ArrayList<>();
        if ((sparseArray != null) && (sparseArray.size() > 0)) {
            for (int i = 0; i < sparseArray.size(); i++) {
                list.addAll(sparseArray.valueAt(i));
            }
        }
        return list.toArray(new String[list.size()]);
    }

}
