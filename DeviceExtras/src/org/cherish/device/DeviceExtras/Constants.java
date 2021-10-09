/*
 * Copyright (C) 2018-2021 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cherish.device.DeviceExtras;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceManager;

import android.media.AudioManager;

public class Constants {

    public static final String EXTRA_SLIDER_USAGE = "usage";
    public static final String EXTRA_SLIDER_ACTIONS = "actions";

    public static final int MODE_TOTAL_SILENCE = 600;
    public static final int MODE_ALARMS_ONLY = 601;
    public static final int MODE_PRIORITY_ONLY = 602;
    public static final int MODE_NONE = 603;
    public static final int MODE_VIBRATE = 604;
    public static final int MODE_RING = 605;
    public static final int MODE_SILENT = 620;
    public static final int MODE_FLASHLIGHT_ON = 621;
    public static final int MODE_FLASHLIGHT_OFF = 622;
    public static final int MODE_FLASHLIGHT_BLINK = 623;
    public static final int MODE_BRIGHTNESS_BRIGHT = 630;
    public static final int MODE_BRIGHTNESS_DARK = 631;
    public static final int MODE_BRIGHTNESS_AUTO = 632;
    public static final int MODE_ROTATION_AUTO = 640;
    public static final int MODE_ROTATION_0 = 641;
    public static final int MODE_ROTATION_90 = 642;
    public static final int MODE_ROTATION_270 = 643;

    // Holds <preference_key> -> <proc_node> mapping
    public static final Map<String, String> sBooleanNodePreferenceMap = new HashMap<>();
    public static final Map<String, String> sStringNodePreferenceMap = new HashMap<>();
}
