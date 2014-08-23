package com.comandante.creeper.command;

import com.comandante.creeper.command.commands.Command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandService {

    private final ExecutorService executorService;

    public CommandService() {
        this.executorService = Executors.newFixedThreadPool(1);
    }

    public void processCommand(Command command) {
        executorService.submit(command);
    }

}
