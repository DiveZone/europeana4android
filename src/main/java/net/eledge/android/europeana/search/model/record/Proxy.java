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

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Proxy extends Resource {

    public Map<String, String[]> dcContributor;

    public Map<String, String[]> dcCreator;

    public Map<String, String[]> dcDate;

    public Map<String, String[]> dcDescription;

    public Map<String, String[]> dcFormat;

    public Map<String, String[]> dcIdentifier;

    public Map<String, String[]> dcRights;

    public Map<String, String[]> dcSource;

    public Map<String, String[]> dcSubject;

    public Map<String, String[]> dcTitle;

    public Map<String, String[]> dcType;

    public Map<String, String[]> dctermsCreated;

    public Map<String, String[]> dctermsExtent;

    public Map<String, String[]> dctermsIsPartOf;

    public Map<String, String[]> dctermsIssued;

    public Map<String, String[]> dctermsMedium;

    public DocType edmType;

    public void setEdmType(String type) {
        this.edmType = DocType.safeValueOf(type);
    }

    public static void normalize(RecordObject object) {
        Proxy merged = new Proxy();
        if (object.proxies != null) {
            for (Proxy source : object.proxies) {
                merged.dcContributor = mergeMapArrays(merged.dcContributor, source.dcContributor);
                merged.dcCreator = mergeMapArrays(merged.dcCreator, source.dcCreator);
                merged.dcDate = mergeMapArrays(merged.dcDate, source.dcDate);
                merged.dcDescription = mergeMapArrays(merged.dcDescription, source.dcDescription);
                merged.dcFormat = mergeMapArrays(merged.dcFormat, source.dcFormat);
                merged.dcIdentifier = mergeMapArrays(merged.dcIdentifier, source.dcIdentifier);
                merged.dcRights = mergeMapArrays(merged.dcRights, source.dcRights);
                merged.dcSource = mergeMapArrays(merged.dcSource, source.dcSource);
                merged.dcSubject = mergeMapArrays(merged.dcSubject, source.dcSubject);
                merged.dcTitle = mergeMapArrays(merged.dcTitle, source.dcTitle);
                merged.dcType = mergeMapArrays(merged.dcType, source.dcType);

                merged.dctermsCreated = mergeMapArrays(merged.dctermsCreated, source.dctermsCreated);
                merged.dctermsExtent = mergeMapArrays(merged.dctermsExtent, source.dctermsExtent);
                merged.dctermsIsPartOf = mergeMapArrays(merged.dctermsIsPartOf, source.dctermsIsPartOf);
                merged.dctermsIssued = mergeMapArrays(merged.dctermsIssued, source.dctermsIssued);
                merged.dctermsMedium = mergeMapArrays(merged.dctermsMedium, source.dctermsMedium);

                merged.edmType = defaultValue(merged.edmType, source.edmType);
            }
        }
        object.type = defaultValue(object.type, merged.edmType);
        object.proxy = merged;
    }

}
