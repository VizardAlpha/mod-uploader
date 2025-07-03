package com.github.argon.moduploader.vendor.steam;

import com.codedisaster.steamworks.*;

import java.io.Closeable;

/**
 * For interacting with the Steam user
 */
public class SteamUser implements Closeable {

    private final com.codedisaster.steamworks.SteamUser steamUser;

    public SteamUser() {
        steamUser = new com.codedisaster.steamworks.SteamUser(new Callback());
    }

    public SteamID getSteamID() {
        return steamUser.getSteamID();
    }

    @Override
    public void close() {
        steamUser.dispose();
    }

    private static class Callback implements SteamUserCallback {
        @Override
        public void onAuthSessionTicket(SteamAuthTicket authTicket, SteamResult result) {

        }

        @Override
        public void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID ownerSteamID) {

        }

        @Override
        public void onMicroTxnAuthorization(int appID, long orderID, boolean authorized) {

        }

        @Override
        public void onEncryptedAppTicket(SteamResult result) {

        }
    }
}
