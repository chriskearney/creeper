package com.comandante.creeper.command;


import com.comandante.creeper.command.commands.CommandService;
import com.comandante.creeper.command.commands.GossipCommand;
import com.comandante.creeper.command.commands.MovementCommand;
import com.comandante.creeper.command.commands.SayCommand;
import com.comandante.creeper.command.commands.TellCommand;
import com.comandante.creeper.command.commands.UnknownCommand;
import com.comandante.creeper.command.commands.WhoCommand;
import com.comandante.creeper.command.commands.WhoamiCommand;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.server.CreeperSession;
import org.jboss.netty.channel.MessageEvent;

public class DefaultCommandHandler {

    private final GameManager gameManager;
    private final CommandService commandService;

    public DefaultCommandHandler(GameManager gameManager, CommandService commandService) {
        this.gameManager = gameManager;
        this.commandService = commandService;
    }

    public void handle(MessageEvent e, CreeperSession creeperSession) {
        String originalMessage = (String) e.getMessage();
        String rootCommand = originalMessage.split(" ")[0].toLowerCase();
        String playerId = new Player(creeperSession.getUsername().get()).getPlayerId();
        if (GossipCommand.validTriggers.contains(rootCommand)){
            GossipCommand gossipCommand = new GossipCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(gossipCommand);
        }
        else if (MovementCommand.validTriggers.contains(rootCommand)){
            MovementCommand movementCommand = new MovementCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(movementCommand);
        }
        else if (SayCommand.validTriggers.contains(rootCommand)){
            SayCommand sayCommand = new SayCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(sayCommand);
        }
        else if (TellCommand.validTriggers.contains(rootCommand)){
            TellCommand tellCommand = new TellCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(tellCommand);
        }
        else if (WhoCommand.validTriggers.contains(rootCommand)){
            WhoCommand whoCommand = new WhoCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(whoCommand);
        }
        else if (WhoamiCommand.validTriggers.contains(rootCommand)){
            WhoamiCommand whoamiCommand = new WhoamiCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(whoamiCommand);
        } else {
            UnknownCommand unknownCommand = new UnknownCommand(playerId, gameManager, originalMessage);
            commandService.processCommand(unknownCommand);
        }
    }
}

