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

package net.eledge.android.europeana.search.model.searchresults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.europeana.search.model.enums.DocType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    public String id;

    public String[] title = new String[]{"Invalid title..."};

    public DocType type;

    public String[] edmPreview;

    public void setType(String type) {
        this.type = DocType.safeValueOf(type);
    }
}
