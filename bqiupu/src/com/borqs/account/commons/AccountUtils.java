/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.account.commons;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Pair;

/**
 * Helper class to access to Android account manager for Borqs account
 * 
 * @author b251
 * 
 */
public final class AccountUtils {
    // Global configuration, DO NOT MDOIFY
    public static final String BORQS_ACCOUNT_TYPE = "com.borqs";

    public static final String INTENT_ACTION_ACCOUNT_LOGIN = "com.borqs.intent.action.ACCOUNT_LOGIN";
	static final String FEATURE_ACCOUNT_SERVER_HOST = "account_server_host";
	static final String FEATURE_SYNC_SERVER_HOST = "sync_server_host";

    public static final String CONFIG_ACCOUNT_SERVER_HOST = FEATURE_ACCOUNT_SERVER_HOST;
    public static final String CONFIG_SYNC_SERVER_HOST = FEATURE_SYNC_SERVER_HOST;

    // key of user data in system after account login, be able to
    // access by API getUserData().
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_SESSION = "borqs_session";
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_NICK_NAME = "borqs_nick_name";
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_UID = "borqs_uid";
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_SCREEN_NAME = "borqs_screen_name";
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_ERROR = "borqs_account_error";
    public static final String BORQS_ACCOUNT_LOGIN_GUID = "borqs_account_login_guid";

    public static final String OPTIONS_FORCE_RELOGIN = "force_relogin";
    /*
     * Key for profile's phones value is string as schema
     * <number>:<type>,<number>:<type>, for example, "139109111:2,62000312:4",
     * type refer to ContactsContract.CommonDataKinds.Phone you'd better use
     * Profile.getPhones() instead of the key directly
     */
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_PROFILE_PHONES = "borqs_conact_phones";

    /**
     * Key for profile's EMails value is string as schema
     * <email>:<type>,<email>:<type> for example, "a@gmail.com:2,b@borqs.com:3",
     * type refer to ContactsContract.CommonDataKinds.Email you'd better use
     * Profile.getEMails() instead of the key directly
     */
    public static final String BORQS_ACCOUNT_OPTIONS_KEY_PROFILE_EMAILS = "borqs_conact_emails";

    public static final String DEFAULT_AUTH_TYPE = "com.borqs.service";

    // Operation for the account
    private static final String OP_GET_BORQS_ID_BY_CONTACT = "op_get_borqs_id_by_contact";
    private static final String OP_GET_USER_DATA = "op_get_user_data";
    private static final String OP_SET_USER_DATA = "op_set_user_data";
    private static final String ARGUMENT = "arg";

    /**
     * Helper function to check if there is authenticator for account type
     * 'com.borqs'
     * 
     * @return true - Ok for borqs account login/register by uniform ui false-
     *         The application need to do login/register itself
     */
    public static boolean isBorqsAccountServicePreloaded(Context context) {
        AuthenticatorDescription[] authenticators = AccountManager.get(context)
                .getAuthenticatorTypes();
        for (AuthenticatorDescription au : authenticators) {
            if (au != null && BORQS_ACCOUNT_TYPE.equals(au.type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Request to add a account into system by the default login ui. Require
     * "android.permission.MANAGE_ACCOUNTS"
     * 
     * @param activity
     * @featureId the application id assigned by Borqs server,can be null
     * @options options.putString(AccountUtils.CONFIG_ACCOUNT_SERVER_HOST, "http://apitest.borqs.com/"); 
     */
    public static void requestAccountSignin(Activity activity,
            String featureId, Bundle options,
            AccountManagerCallback<Bundle> callback) {
        // android.permission.MANAGE_ACCOUNTS
        AccountManager.get(activity).addAccount(BORQS_ACCOUNT_TYPE, null, /* authTokenType */
                null==featureId?null:new String[] { featureId }, /* requiredFeatures */
        options, /* addAccountOptions */
        activity, callback, null /* handler */);
    }
       
    /**
     * Request user to login the account to update session.
     * Require "android.permission.MANAGE_ACCOUNTS" * 
     * @options options.putString(AccountUtils.CONFIG_ACCOUNT_SERVER_HOST, "http://apitest.borqs.com/"); 
    
     * @param activity
     */
    public static void requestUpdateCredential(Activity activity, Bundle options, AccountManagerCallback<Bundle> callback){        
        Account account = new Account(getAccountId(activity), BORQS_ACCOUNT_TYPE);
        AccountManager.get(activity).updateCredentials(
                account, DEFAULT_AUTH_TYPE, options, activity, callback, null);
    }    
    /**
     * Remove the Borqs account from system. Require
     * "android.permission.MANAGE_ACCOUNTS"
     * 
     * @param context
     *            - Android context
     */
    public static void removeAccount(Context context) {
        AccountManager am = AccountManager.get(context);

        Account[] accounts = am.getAccountsByType(BORQS_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            return;
        }
        am.removeAccount(accounts[0], null, null);
    }

    /**
     * Retrieve the account Id/Name in the system. Require
     * "android.permission.GET_ACCOUNTS"
     * 
     * @param context
     * @return borqs account id, null if no borqs account
     */
    public static String getAccountId(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType(
                BORQS_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            return null;
        }

        return accounts[0].name;
    }

    /**
     * Common interface to retrieve the user data by key. Require
     * "android.permission.AUTHENTICATE_ACCOUNTS"
     * 
     * @param context
     * @param key
     *            - Key of user data
     * @return
     */
    public static String getUserData(Context context, String key) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException();
        }

        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(BORQS_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            return null;
        }
        return am.getUserData(accounts[0], key);
    }

    /**
     * Set user data for the account. require
     * "android.permission.AUTHENTICATE_ACCOUNTS"
     * 
     * @param context
     * @param key
     *            - Key of the user data
     * @param value
     *            - value in string
     */
    public static void setUserData(Context context, String key, String value) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException();
        }

        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(BORQS_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            return;
        }
        am.setUserData(accounts[0], key, value);
    }

    /**
     * Get the Borqs-ID for a contacts
     * 
     * @param context
     * @param contactId
     *            the contact ID from Contacts provider
     * @return Borqs-ID or null if the contact is not Borqs contact
     */
    public static String getBorqsIdByContact(Context context, long contactId) {

        return queryBorqsIDByContact(context, "" + contactId);
    }

    // For Contacts provider
    private static final String ACCOUNT_COLUMN_ID = RawContacts._ID;
    private static final String ACCOUNT_COLUMN_CONTACT_ID = RawContacts._ID;
    private static final String ACCOUNT_COLUMN_BORQS_ID = RawContacts.SYNC4;
    private static final String[] ACCOUNT_BORQS_ID_PROJECTION = new String[] {
            ACCOUNT_COLUMN_ID, ACCOUNT_COLUMN_CONTACT_ID,
            ACCOUNT_COLUMN_BORQS_ID };

    private static String queryBorqsIDByContact(Context context,
            String contactId) {
        if (TextUtils.isEmpty(contactId)) {
            return null;
        }

        Cursor cursor = context.getContentResolver().query(
                ContentUris.withAppendedId(RawContacts.CONTENT_URI,
                        Long.valueOf(contactId)), ACCOUNT_BORQS_ID_PROJECTION,
                null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return cursor.getString(cursor
                            .getColumnIndexOrThrow(ACCOUNT_COLUMN_BORQS_ID));
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * return the Borqs account
     * 
     * @param context
     * @return null if no Borqs account
     */
    public static Account getBorqsAccount(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        if (accounts != null) {
            for (Account account : accounts) {
                if (BORQS_ACCOUNT_TYPE.equals(account.type)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * get my profile
     * 
     * @param context
     *            application context
     * @return Profile object
     */
    public static Profile getProfile(Context context) {
        return Profile.create(context);
    }

    // Profile data class, maybe move to a separate file later
    public static final class Profile {
        private Context mContext;

        /**
         * get the phones of the profile
         * 
         * @return list of pair of <Number, Type>, type refer to
         *         ContactsContract.CommonDataKinds.Phone
         */
        public List<Pair<String, Integer>> getPhones() {
            return getPairAttribute(mContext,
                    BORQS_ACCOUNT_OPTIONS_KEY_PROFILE_PHONES);
        }

        /**
         * get the EMails of the profile
         * 
         * @return list of pair of <EMail, Type>, type refer to
         *         ContactsContract.CommonDataKinds.Email
         */
        public List<Pair<String, Integer>> getEMails() {
            return getPairAttribute(mContext,
                    BORQS_ACCOUNT_OPTIONS_KEY_PROFILE_EMAILS);
        }

        private static Profile create(Context context) {
            return new Profile(context);
        }

        private Profile(Context context) {
            mContext = context;
        }

        // helper function
        private List<Pair<String, Integer>> getPairAttribute(Context context,
                String key) {
            String phones = getUserData(context, key);
            if (TextUtils.isEmpty(phones)) {
                return Collections.emptyList();
            }

            LinkedList<Pair<String, Integer>> list = new LinkedList<Pair<String, Integer>>();
            String[] phoneList = phones.split(",");
            for (String pair : phoneList) {
                String[] phone_type = pair.split(":");
                list.add(Pair.create(phone_type[0],
                        Integer.valueOf(phone_type[1])));
            }
            return list;
        }
    }
}
