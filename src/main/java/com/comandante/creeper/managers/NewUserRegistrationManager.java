package com.comandante.creeper.managers;


import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Optional;
import org.jboss.netty.channel.MessageEvent;

public class NewUserRegistrationManager {

    private final PlayerManager playerManager;

    public NewUserRegistrationManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void handle(CreeperSession session, MessageEvent e) {
        switch (session.getState()) {
            case newUserPromptedForUsername:
                if (setDesiredUsername(session, e)) {
                    promptForDesirePassword(session, e);
                }
                break;
            case newUserPromptedForPassword:
                createUser(session, e);
                break;
        }
    }

    public void newUserRegistrationFlow(CreeperSession session, MessageEvent e) {
        e.getChannel().write("desired username: ");
        session.setState(CreeperSession.State.newUserPromptedForUsername);
    }

    private boolean setDesiredUsername(CreeperSession session, MessageEvent e) {
        PlayerMetadata playerMetadata = playerManager.getPlayerMetadata(new Player((String) e.getMessage()).getPlayerId());
        if (playerMetadata != null) {
            e.getChannel().write("Username is in use.\r\n");
            newUserRegistrationFlow(session, e);
            return false;
        }
        session.setUsername(Optional.of((String)e.getMessage()));
        return true;
    }

    private void promptForDesirePassword(CreeperSession session, MessageEvent e) {
        e.getChannel().write("desired password: ");
        session.setState(CreeperSession.State.newUserPromptedForPassword);
    }

    private void createUser(CreeperSession session, MessageEvent e) {
        session.setPassword(Optional.of((String) e.getMessage()));
        PlayerMetadata playerMetadata = new PlayerMetadata(session.getUsername().get(), session.getPassword().get(), new Player(session.getUsername().get()).getPlayerId());
        playerManager.savePlayerMetadata(playerMetadata);
        e.getChannel().write("User created.\r\n");
        session.setState(CreeperSession.State.newUserRegCompleted);
    }

}
