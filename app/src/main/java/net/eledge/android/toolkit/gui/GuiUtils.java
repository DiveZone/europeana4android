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

package net.eledge.android.toolkit.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class GuiUtils {

  public static void toast(@NonNull Context context, @StringRes int text) {
    Toast.makeText(context, text,
      Toast.LENGTH_SHORT).show();
  }

  public static void startTopActivity(@NonNull Context context,
                                      @NonNull Class<? extends Activity> clazz) {
    final Intent intent = new Intent(context, clazz);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  public static String getString(@NonNull Context context, @StringRes int resId) {
    return context.getResources().getString(resId);
  }

  public static int getInteger(@NonNull Context context, @IntegerRes int resId) {
    return context.getResources().getInteger(resId);
  }

  public static String format(@NonNull Context context, @StringRes int resId, Object... params) {
    return String.format(getString(context, resId), params);
  }
}
