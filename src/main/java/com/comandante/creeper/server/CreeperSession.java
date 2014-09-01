package com.comandante.creeper.server;

import com.comandante.creeper.fight.FightResults;
import com.google.common.base.Optional;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreeperSession {

    private Optional<String> username = Optional.absent();
    private Optional<String> password = Optional.absent();
    private boolean isAuthed = false;
    private Optional<Future<FightResults>> activeFight = Optional.absent();
    private AtomicBoolean isAbleToDoAbility = new AtomicBoolean(false);

    State state;

    public enum State {
        promptedForPassword,
        promptedForUsername,
        newUserPromptedForUsername,
        newUserPromptedForPassword,
        newUserRegCompleted,
        authed
    }

    public Optional<Future<FightResults>> getActiveFight() {
        return activeFight;
    }

    public void setActiveFight(Optional<Future<FightResults>> activeFight) {
        this.activeFight = activeFight;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public void setUsername(Optional<String> username) {
        this.username = username;
    }

    public Optional<String> getPassword() {
        return password;
    }

    public void setPassword(Optional<String> password) {
        this.password = password;
    }

    public boolean isAuthed() {
        return isAuthed;
    }

    public void setAuthed(boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setIsAbleToDoAbility(boolean b) {
        this.isAbleToDoAbility.set(b);
    }

    public boolean IsAbleToDoAbility() {
        return this.isAbleToDoAbility.get();
    }
}
