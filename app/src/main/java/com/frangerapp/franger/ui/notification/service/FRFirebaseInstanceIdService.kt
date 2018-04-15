package com.frangerapp.franger.ui.notification.service

import com.franger.mobile.logger.FRLogger
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FRFirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        FRLogger.msg("Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //TODO
        //        sendRegistrationToServer(refreshedToken);
    }
}
