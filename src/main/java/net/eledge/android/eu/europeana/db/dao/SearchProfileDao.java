package net.eledge.android.eu.europeana.db.dao;

import net.eledge.android.eu.europeana.db.model.SearchProfile;
import net.eledge.android.eu.europeana.db.setup.DatabaseSetup;
import net.eledge.android.toolkit.db.abstracts.Dao;

public class SearchProfileDao extends Dao<SearchProfile> {

	public SearchProfileDao(DatabaseSetup helper) {
		super(SearchProfile.class, helper);
	}
	
}
