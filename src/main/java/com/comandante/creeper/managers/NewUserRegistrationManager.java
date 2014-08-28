package com.comandante.creeper.managers;


import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.model.StatsBuilder;
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
        session.setUsername(Optional.of((String) e.getMessage()));
        return true;
    }

    private void promptForDesirePassword(CreeperSession session, MessageEvent e) {
        e.getChannel().write("desired password: ");
        session.setState(CreeperSession.State.newUserPromptedForPassword);
    }

    private void createUser(CreeperSession session, MessageEvent e) {
        String password = (String) e.getMessage();
        if (password.length() < 8) {
            e.getChannel().write("Passwords must be at least 8 characters.\r\n");
            newUserRegistrationFlow(session, e);
            return;
        }
        session.setPassword(Optional.of(password));
        PlayerMetadata playerMetadata = new PlayerMetadata(session.getUsername().get(), session.getPassword().get(), new Player(session.getUsername().get()).getPlayerId(), new StatsBuilder().setStrength(7).setWillpower(8).setAim(6).setAgile(5).setArmorRating(4).setMeleSkill(10).setHealth(100).setWeaponRatingMin(10).setWeaponRatingMax(20).setNumberweaponOfRolls(1).createStats());
        playerManager.savePlayerMetadata(playerMetadata);
        e.getChannel().write("User created.\r\n");
        session.setState(CreeperSession.State.newUserRegCompleted);
    }

}
