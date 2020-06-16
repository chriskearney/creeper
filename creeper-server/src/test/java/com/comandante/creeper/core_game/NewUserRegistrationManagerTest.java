package com.comandante.creeper.core_game;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewUserRegistrationManagerTest {
    @Test
    public void isValidUsername() throws Exception {
        String username = StringUtils.repeat("*", 10);
        Assert.assertTrue(NewUserRegistrationManager.isValidUsername(username));

        String longUsername = StringUtils.repeat("W", NewUserRegistrationManager.MAX_USERNAME_LENGTH);
        Assert.assertTrue(NewUserRegistrationManager.isValidUsername(longUsername));

        String tooLongUsername = StringUtils.repeat("W", NewUserRegistrationManager.MAX_USERNAME_LENGTH + 1);
        Assert.assertFalse(NewUserRegistrationManager.isValidUsername(tooLongUsername));

        String tooShortUsername = StringUtils.repeat("W", NewUserRegistrationManager.MIN_USERNAME_LENGTH - 1);
        Assert.assertFalse(NewUserRegistrationManager.isValidUsername(tooShortUsername));

        String shortUsername = StringUtils.repeat("W", NewUserRegistrationManager.MIN_USERNAME_LENGTH);
        Assert.assertTrue(NewUserRegistrationManager.isValidUsername(shortUsername));
    }

}