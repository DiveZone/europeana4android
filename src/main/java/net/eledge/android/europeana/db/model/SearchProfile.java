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

package net.eledge.android.europeana.db.model;

import android.provider.BaseColumns;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "search_profile")
public class SearchProfile implements BaseColumns {

    public SearchProfile() {
    }

    public SearchProfile(String name, String facets) {
        this.name = name;
        this.facets = facets;
    }

    @Id
    @Column(name = _ID)
    public Long id;

    @Column
    public String name;

    @Column
    public String facets;

    @Override
    public String toString() {
        return name;
    }

}
