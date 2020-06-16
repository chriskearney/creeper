package com.comadante.creeper.cclient;

import com.google.common.eventbus.Subscribe;
import com.terminal.emulator.JediEmulator;
import com.terminal.ui.ResetEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class BasicAuthStringSupplier implements Supplier<Optional<String>>, JediEmulator.NonControlCharListener {

    private Optional<String> basicAuthHash = Optional.empty();

    @Override
    public void processNonControlChar(String nonControlCharacters) {
        if (!basicAuthHash.isPresent()) {
            if (nonControlCharacters.startsWith("AUTH - ")) {
                basicAuthHash = Optional.ofNullable(nonControlCharacters.split(" - ")[1]);
            }
        }
    }

    @Override
    public Optional<String> get() {
        return basicAuthHash;
    }

    public void reset() {
        this.basicAuthHash = Optional.empty();
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        reset();
    }
}
