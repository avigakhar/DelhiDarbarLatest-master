package com.app.delhidarbar.utils;

import com.app.delhidarbar.Beens;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController  {

    private static RealmController instance;
    private Realm realm;

    private RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance() {

        if (instance == null) {
            instance = new RealmController();
        }

        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the realm istance
    public void refresh() {
        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        Beens.realmId = 0;
    }

    //find all objects in the Book.class
    public RealmResults<RealmProduct> getAll () {
        return realm.where(RealmProduct.class).findAll();
    }

    public int getCount () {
        return getAll().size();
    }
}
