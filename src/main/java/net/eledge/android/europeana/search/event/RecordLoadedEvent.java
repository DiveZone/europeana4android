package net.eledge.android.europeana.search.event;

import net.eledge.android.europeana.search.model.record.RecordObject;

public class RecordLoadedEvent {

    public final RecordObject result;

    public RecordLoadedEvent(RecordObject result) {
        this.result = result;
    }
}
