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

package net.eledge.android.eu.europeana.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Place extends Resource {

    public Map<String, String[]> prefLabel;

    public Double latitude;

    public Double longitude;

    public static void normalize(RecordObject object) {
        Place merged = new Place();
        if (object.places != null) {
            for (Place source : object.places) {
                merged.prefLabel = mergeMapArrays(merged.prefLabel, source.prefLabel);
                merged.latitude = defaultValue(merged.latitude, source.latitude);
                merged.longitude = defaultValue(merged.longitude, source.longitude);
            }
        }
        object.place = merged;
    }

}
