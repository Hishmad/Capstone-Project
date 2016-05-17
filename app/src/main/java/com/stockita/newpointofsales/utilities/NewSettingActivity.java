package com.stockita.newpointofsales.utilities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.util.Log;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;

import java.util.Currency;

/**
 * Class for the setting, so the user can make general settings to the app
 */
public class NewSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /**
         * For more simple coding, put each pref in to separate
         * fragment, and each fragment in separate container
         * so it will be easy for me to manege the state and or
         * the layout.
         */
        if (savedInstanceState == null) {

            /* Merchant */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container1, new MerchantName())
                    .commit();

            /* Tax id */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container2, new TaxId())
                    .commit();

            /* Tax rate */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container3, new TaxRate())
                    .commit();

            /* Discount rate */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container4, new DiscountRate())
                    .commit();

            /* Currency */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container5, new Currency())
                    .commit();

            /* Ownership status */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container6, new BossSetting())
                    .commit();

            /* Boss email address */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.setting_container7, new BossEmailSetting())
                    .commit();

        }

    }


    /**
     * The Merchant name
     */
    public static class MerchantName extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences mSharedPreferences;

        /**
         * Empty Constructor
         */
        public MerchantName() {

        }


        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_merchant_name);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_merchant_name));

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


            /* Check for the key for this specific tag */
            if (key.contains(getString(R.string.key_pref_merchant_name))) {

                /* Find the preference object pass the key as argument */
                Preference preference = findPreference(key);

                /* Cast the preference so we can work with the data */
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                /* If default then show the summary default */
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

            }


        }

        @Override
        public void onResume() {
            super.onResume();
            /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }


    /**
     * The tax id
     */
    public static class TaxId extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


        /* Member variable */
        private SharedPreferences mSharedPreferences;

        /**
         * Empty Constructor
         */
        public TaxId() {

        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_tax_id);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_tax_id));

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Check for the key for this specific tag */
            if (key.contains(getString(R.string.key_pref_tax_id))) {

                /* Find the preference object pass the key as argument */
                Preference preference = findPreference(key);

                /* Cast the preference so we can work with the data */
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                /* If default then show the summary default */
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

            }

        }


        @Override
        public void onResume() {
            super.onResume();

            /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            super.onPause();

            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }


    /**
     * The tax rate
     */
    public static class TaxRate extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        SharedPreferences mSharedPreferences;

        /**
         * Empty constructor
         */
        public TaxRate() {
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_tax_rate);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_tax_rate));

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Check for the key for this specific tag */
            if (key.contains(getString(R.string.key_pref_tax_rate))) {

                /* Find the preference object pass the key as argument */
                Preference preference = findPreference(key);

                /* Cast the preference so we can work with the data */
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                /* If default then show the summary default */
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

                /* Now check if the user type a numeric, if not then return 0 */
                String pref = sharedPreferences.getString(key, getString(R.string.pref_tax_id_default_value));
                if (!Utility.isThisStringNumeric(pref)) {
                    editTextPreference.setText("0");
                }

            }
        }


        @Override
        public void onResume() {
            super.onResume();

            /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            super.onPause();

            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }


    /**
     * The discount rate
     */
    public static class DiscountRate extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        SharedPreferences mSharedPreferences;

        /**
         * Empty constructor
         */
        public DiscountRate() {

        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_discount_rate);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_discount_rate));

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Check for the key for this specific tag */
            if (key.contains(getString(R.string.key_pref_discount_rate))) {

                /* Find the preference object pass the key as argument */
                Preference preference = findPreference(key);

                /* Cast the preference so we can work with the data */
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                /* If default then show the summary default */
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

                /* Now check if the user type a numeric, if not then return 0 */
                String pref = sharedPreferences.getString(key, getString(R.string.pref_tax_id_default_value));
                if (!Utility.isThisStringNumeric(pref)) {
                    editTextPreference.setText("0");
                }
            }

        }

        @Override
        public void onResume() {
            super.onResume();

            /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }


    /**
     * The currency
     */
    public static class Currency extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        SharedPreferences mSharedPreferences;

        /**
         * Empty constructor
         */
        public Currency() {

        }


        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_currency);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_currency));

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                        /* Find the preference pass the key as argument */
            Preference preference = findPreference(key);

            /* Check if it is a ListPreference */
            if (preference instanceof ListPreference) {

                /* Case the preference to ListPreference */
                ListPreference listPreference = (ListPreference) preference;

                /* Get the index of the value that changed in this list */
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, getString(R.string.default_currency)));
                if (prefIndex >= 0) {
                    /* Set the new value selected in the list */
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

        }


        @Override
        public void onResume() {
            super.onResume();

            /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            super.onPause();

            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }


    /**
     * The ownership status
     */
    public static class BossSetting extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        /* Member variable */
        private SharedPreferences mSharedPreferences;

        /**
         * Empty constructor
         */
        public BossSetting() {

        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_owner_status);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_ownership_status));
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Find the preference pass the key as argument */
            Preference preference = findPreference(key);

            /* Check if it is a ListPreference */
            if (preference instanceof ListPreference) {

                /* Case the preference to ListPreference */
                ListPreference listPreference = (ListPreference) preference;

                /* Get the index of the value that changed in this list */
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, Constant.VALUE_OWNER));
                if (prefIndex >= 0) {
                    /* Set the new value selected in the list */
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
        }


        @Override
        public void onResume() {
            super.onResume();

            /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            super.onPause();

            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }

    /**
     * The boss email address, if the ownership status is worker
     */
    public static class BossEmailSetting extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences mSharedPreferences;


        /**
         * Empty Constructor
         */
        public BossEmailSetting() {
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {


            /* Get the general preference layout */
            addPreferencesFromResource(R.xml.pref_general_boss_email);

            /* Initialize the SharedPreferences */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /* Assign listener */
            onSharedPreferenceChanged(mSharedPreferences, getString(R.string.key_pref_owner_email_address));

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Find the preference pass the key as argument */
            Preference preference = findPreference(key);

            /* Check if it is a ListPreference */
            if (preference instanceof EditTextPreference) {

                /* Cast */
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                /* Get the ownership status and check if he is not worker then set to null */
                String currentStatus = Utility.getAnyString(getActivity(), getString(R.string.key_pref_ownership_status), Constant.VALUE_OWNER);
                if (!currentStatus.contains(Constant.VALUE_JOB_STATUS)) {
                    editTextPreference.setText(Constant.VALUE_NULL_STRING);

                }

                /* Get the user typed email and validate the pattern, if false set to null */
                String sharedPref = sharedPreferences.getString(key, Constant.VALUE_NULL_STRING).replaceAll("\\s", "");
                if (currentStatus.contains(Constant.VALUE_JOB_STATUS) && !Utility.isEmailValid(sharedPref)) {
                    editTextPreference.setText(Constant.VALUE_NULL_STRING);
                }
            }
        }


        @Override
        public void onResume() {
            super.onResume();
                /* Register the listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            super.onPause();
            /* Unregister the listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }


    }

}
