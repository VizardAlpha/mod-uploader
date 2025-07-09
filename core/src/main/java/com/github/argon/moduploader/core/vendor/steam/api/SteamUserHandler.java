package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.*;

import java.io.Closeable;

/**
 * For interacting with the Steam user
 */
public class SteamUserHandler implements Closeable {

    private final com.codedisaster.steamworks.SteamUser steamUser;

    public SteamUserHandler() {
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
