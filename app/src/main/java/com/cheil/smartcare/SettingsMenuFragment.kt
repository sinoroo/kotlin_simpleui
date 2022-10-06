package com.cheil.smartcare

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsMenuFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}