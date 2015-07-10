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

package net.eledge.android.europeana.db.setup;

import android.content.Context;

import net.eledge.android.europeana.Config;
import net.eledge.android.europeana.db.model.BlogArticle;
import net.eledge.android.toolkit.db.SQLiteSetup;
import net.eledge.android.toolkit.db.annotations.Model;

@Model(version = Config.DB_VERSION, name = Config.DB_NAME, entities = {BlogArticle.class})
public class DatabaseSetup extends SQLiteSetup {

    public DatabaseSetup(Context context) {
        super(context, DatabaseSetup.class);
    }

}
