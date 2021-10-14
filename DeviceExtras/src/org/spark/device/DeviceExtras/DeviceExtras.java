/*
* Copyright (C) 2016 The OmniROM Project
* Copyright (C) 2021 The sparkos X Project
* Copyright (C) 2018 The Xiaomi-SDM660 Project
* Copyright (C) 2018-2021 crDroid Android Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.spark.device.DeviceExtras;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import java.util.Arrays;

import org.spark.device.DeviceExtras.Constants;
import org.spark.device.DeviceExtras.doze.DozeSettingsActivity;
import org.spark.device.DeviceExtras.FileUtils;
import org.spark.device.DeviceExtras.R;

public class DeviceExtras extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = DeviceExtras.class.getSimpleName();

    public static final String KEY_SETTINGS_PREFIX = "device_setting_";

    public static final String KEY_CATEGORY_DISPLAY = "display";

    public static final String KEY_DOZE = "advanced_doze_settings";
    public static final String KEY_DC_SWITCH = "dc";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_AUTO_HBM_SWITCH = "auto_hbm";
    public static final String KEY_AUTO_HBM_THRESHOLD = "auto_hbm_threshold";
    public static final String KEY_HBM_INFO = "hbm_info";


    public static final String KEY_DCI_SWITCH = "dci";

    private static TwoStatePreference mAutoHBMSwitch;
    private static TwoStatePreference mDCModeSwitch;
    private static TwoStatePreference mHBMModeSwitch;

    private Preference mDozeSettings;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        addPreferencesFromResource(R.xml.main);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        Context context = this.getContext();


        boolean display = false;

        // DozeSettings Activity
        mDozeSettings = (Preference)findPreference(KEY_DOZE);
        mDozeSettings.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), DozeSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        // DC-Dimming
        display = display | isFeatureSupported(context, R.bool.config_deviceSupportsDCdimming);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsDCdimming)) {
            mDCModeSwitch = (TwoStatePreference) findPreference(KEY_DC_SWITCH);
            mDCModeSwitch.setEnabled(DCModeSwitch.isSupported(this.getContext()));
            mDCModeSwitch.setChecked(DCModeSwitch.isCurrentlyEnabled(this.getContext()));
            mDCModeSwitch.setOnPreferenceChangeListener(new DCModeSwitch());
        }
        else {
            findPreference(KEY_DC_SWITCH).setVisible(false);
        }

        // HBM
        display = display | isFeatureSupported(context, R.bool.config_deviceSupportsHBM);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsHBM)) {
            mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
            mHBMModeSwitch.setEnabled(HBMModeSwitch.isSupported(getContext()));
            mHBMModeSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(DeviceExtras.KEY_HBM_SWITCH, false));
            mHBMModeSwitch.setOnPreferenceChangeListener(this);
        }
        else {
            findPreference(KEY_HBM_SWITCH).setVisible(false);
        }

        // AutoHBM
        display = display | isFeatureSupported(context, R.bool.config_deviceSupportsAutoHBM);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsAutoHBM)) {
            mAutoHBMSwitch = (TwoStatePreference) findPreference(KEY_AUTO_HBM_SWITCH);
            mAutoHBMSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(DeviceExtras.KEY_AUTO_HBM_SWITCH, false));
            mAutoHBMSwitch.setOnPreferenceChangeListener(this);
        }
        else {
            findPreference(KEY_AUTO_HBM_SWITCH).setVisible(false);
            findPreference(KEY_AUTO_HBM_THRESHOLD).setVisible(false);
            findPreference(KEY_HBM_INFO).setVisible(false);
        }

        if (!display) {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_DISPLAY));
        }

    }

    private static boolean isFeatureSupported(Context ctx, int feature) {
        try {
            return ctx.getResources().getBoolean(feature);
        }
        // TODO: Replace with proper exception type class
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isHBMModeService(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceExtras.KEY_HBM_SWITCH, false);
    }

    public static boolean isAUTOHBMEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceExtras.KEY_AUTO_HBM_SWITCH, false);
    }


    private void registerPreferenceListener(String key) {
        Preference p = findPreference(key);
        p.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        if (isFeatureSupported(this.getContext(), R.bool.config_deviceSupportsHBM)) {
            mHBMModeSwitch.setChecked(HBMModeSwitch.isCurrentlyEnabled(this.getContext()));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAutoHBMSwitch) {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefChange.putBoolean(KEY_AUTO_HBM_SWITCH, enabled).commit();
            FileUtils.enableService(getContext());
            return true;
        } else if (preference == mHBMModeSwitch) {
            Boolean enabled = (Boolean) newValue;
            FileUtils.writeValue(HBMModeSwitch.getFile(getContext()), enabled ? "5" : "0");
            Intent hbmIntent = new Intent(this.getContext(),
                    org.spark.device.DeviceExtras.HBMModeService.class);
            if (enabled) {
                this.getContext().startService(hbmIntent);
            } else {
                this.getContext().stopService(hbmIntent);
            }
            return true;
          }
     return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences
        for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            String node = Constants.sBooleanNodePreferenceMap.get(pref);
            if (FileUtils.isFileReadable(node)) {
                String curNodeValue = FileUtils.readOneLine(node);
                b.setChecked(curNodeValue.equals("1"));
                b.setOnPreferenceChangeListener(this);
            } else {
                removePref(b);
            }
        }
        for (String pref : Constants.sStringNodePreferenceMap.keySet()) {
            ListPreference l = (ListPreference) findPreference(pref);
            if (l == null) continue;
            String node = Constants.sStringNodePreferenceMap.get(pref);
            if (FileUtils.isFileReadable(node)) {
                l.setValue(FileUtils.readOneLine(node));
                l.setOnPreferenceChangeListener(this);
            } else {
                removePref(l);
            }
        }
    }

    private void removePref(Preference pref) {
        PreferenceGroup parent = pref.getParent();
        if (parent == null) {
            return;
        }
        parent.removePreference(pref);
        if (parent.getPreferenceCount() == 0) {
            removePref(parent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
