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

package net.eledge.android.europeana.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.europeana.search.model.enums.DocType;
import net.eledge.android.europeana.search.model.record.abstracts.Resource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordObject extends Resource {

    public DocType type;

    public String[] year;

    public String[] title;

    public transient Proxy proxy;

    public Proxy[] proxies;

    public transient Aggregation aggregation;

    public Aggregation[] aggregations;

    public transient Place place;

    public Place[] places;

    public EuropeanaAggregation europeanaAggregation;


    public void setType(String type) {
        this.type = DocType.safeValueOf(type);
    }

    public static RecordObject normalize(RecordObject object) {
        if (object != null) {
            Proxy.normalize(object);
            Aggregation.normalize(object);
            Place.normalize(object);
            EuropeanaAggregation.normalize(object);
        }
        return object;
    }

}
