package com.comandante.creeper.server;

import com.comandante.creeper.CreeperEntry;
import com.comandante.creeper.fight.FightResults;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.server.command.Command;
import com.google.common.base.Optional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreeperSession {

    private Optional<String> username = Optional.absent();
    private Optional<String> password = Optional.absent();
    private boolean isAuthed = false;
    private Optional<Future<FightResults>> activeFight = Optional.absent();
    private AtomicBoolean isAbleToDoAbility = new AtomicBoolean(false);
    private Optional<CreeperEntry<UUID, Command>> grabMultiLineInput = Optional.absent();
    private Optional<CreeperEntry<Merchant, Command>> grabMerchant = Optional.absent();
    private final long sessionCreationTimestamp = System.currentTimeMillis();
    private String lastMessage;

    State state;

    public enum State {
        promptedForPassword,
        promptedForUsername,
        newUserPromptedForUsername,
        newUserPromptedForPassword,
        newUserRegCompleted,
        authed
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getSessionCreationTimestamp() {
        return sessionCreationTimestamp;
    }

    public String getPrettyDate() {
        return  new SimpleDateFormat("d MMM yyyy, hh:mm aaa").format(new Date(getSessionCreationTimestamp()));
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

    public Optional<CreeperEntry<UUID, Command>> getGrabMultiLineInput() {
        return grabMultiLineInput;
    }

    public void setGrabMultiLineInput(Optional<CreeperEntry<UUID, Command>> grabMultiLineInput) {
        this.grabMultiLineInput = grabMultiLineInput;
    }

    public AtomicBoolean getIsAbleToDoAbility() {
        return isAbleToDoAbility;
    }

    public void setIsAbleToDoAbility(AtomicBoolean isAbleToDoAbility) {
        this.isAbleToDoAbility = isAbleToDoAbility;
    }

    public Optional<CreeperEntry<Merchant, Command>> getGrabMerchant() {
        return grabMerchant;
    }

    public void setGrabMerchant(Optional<CreeperEntry<Merchant, Command>> grabMerchant) {
        this.grabMerchant = grabMerchant;
    }
}
