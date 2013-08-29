package net.eledge.android.eu.europeana.db.setup;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.toolkit.db.SQLiteSetup;
import net.eledge.android.toolkit.db.annotations.Model;
import android.content.Context;

@Model(version=Config.DB_VERSION, name=Config.DB_NAME, entities={BlogArticle.class})
public class DatabaseSetup extends SQLiteSetup {
	
	public DatabaseSetup(Context context) {
		super(context, DatabaseSetup.class);
	}

}
