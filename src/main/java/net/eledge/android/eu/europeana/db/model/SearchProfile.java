package net.eledge.android.eu.europeana.db.model;

import android.provider.BaseColumns;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="search_profile")
public class SearchProfile implements BaseColumns {

    public SearchProfile() {
    }

    public SearchProfile(String name, String facets) {
        this.name = name;
        this.facets = facets;
    }

    @Id
    @Column(name=_ID)
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
