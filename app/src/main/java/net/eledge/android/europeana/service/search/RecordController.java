/*
 * Copyright (c) 2013-2016 eLedge.net and the original author or authors.
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

package net.eledge.android.europeana.service.search;

import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.service.search.event.RecordLoadedEvent;
import net.eledge.android.europeana.service.search.model.record.RecordObject;
import net.eledge.android.europeana.tools.UriHelper;

import org.apache.commons.lang3.StringUtils;

public class RecordController {

  public final static RecordController _instance = new RecordController();
  public final static ApiTasks _tasks = ApiTasks.getInstance();

  private String currentRecordId;
  private boolean currentRecordSelected = false;

  public RecordObject record;

  public RecordController() {
    EuropeanaApplication.bus.register(this);
  }

  public void readRecord(String id) {
    if (StringUtils.isNotBlank(id)) {
      if ((record != null) && StringUtils.equals(id, record.about)) {
        // don't load the same record twice but do notify listeners!;
        EuropeanaApplication.bus.post(new RecordLoadedEvent(record));
        return;
      }
      record = null;
      currentRecordId = id;
      currentRecordSelected = false;
      _tasks.loadRecord(id);
    }
  }

  public String getCurrentRecordId() {
    return currentRecordId;
  }

  public boolean isCurrentRecordSelected() {
    return currentRecordSelected;
  }

  public void setCurrentRecordSelected(boolean selected) {
    currentRecordSelected = selected;
  }

  public String getPortalUrl() {
    return UriHelper.getPortalRecordUrl(currentRecordId);
  }

  @Subscribe
  public void OnRecordLoadedEvent(RecordLoadedEvent event) {
    if (StringUtils.equals(event.result.about, currentRecordId)) {
      // make sure it is the latest requested record...
      record = event.result;
    }
  }
}
