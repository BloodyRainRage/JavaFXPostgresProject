package com.baddragon.mock;

import com.baddragon.database.Authenticator;

public class MockAuthenticator extends Authenticator {

    @Override
    public boolean loginIsCorrect(String login) {
        return true;
    }

    @Override
    public boolean checkLogPassPair(String login, String pass) {
        return true;
    }
}
