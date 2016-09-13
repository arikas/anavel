package com.anasion.anavel.myapplication;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Vostro on 13/09/2016.
 */
public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
    }

    private void registerToken(String token) {
        TokenData.getInstance(this).saveToken(token);
    }
}
