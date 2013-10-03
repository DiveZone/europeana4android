package net.eledge.android.eu.europeana.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.record.RecordObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

    public boolean success;

    public RecordObject object;

}
