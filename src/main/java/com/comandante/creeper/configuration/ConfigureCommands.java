package com.comandante.creeper.configuration;

import com.comandante.creeper.command.CreeperCommandRegistry;
import com.comandante.creeper.command.commands.*;
import com.comandante.creeper.command.commands.admin.*;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.bank.commands.*;
import com.comandante.creeper.merchant.lockers.GetCommand;
import com.comandante.creeper.merchant.lockers.LockerCommandRegistry;
import com.comandante.creeper.merchant.lockers.PutCommand;
import com.comandante.creeper.merchant.lockers.QueryCommand;
import com.comandante.creeper.merchant.playerclass_selector.PlayerClassCommandRegistry;
import com.comandante.creeper.merchant.questgiver.QuestGiverCommandRegistry;

public class ConfigureCommands {

    public static CreeperCommandRegistry creeperCommandRegistry;

    public static BankCommandRegistry bankCommandRegistry;

    public static void configureBankCommands(GameManager gameManager) {
        bankCommandRegistry = new BankCommandRegistry(new com.comandante.creeper.merchant.bank.commands.UnknownCommand(gameManager));
        bankCommandRegistry.addCommand(new AccountQueryCommand(gameManager));
        bankCommandRegistry.addCommand(new DepositCommand(gameManager));
        bankCommandRegistry.addCommand(new WithdrawalCommand(gameManager));
        bankCommandRegistry.addCommand(new DoneCommand(gameManager));
    }

    public static LockerCommandRegistry lockerCommandRegistry;

    public static void configureLockerCommands(GameManager gameManager) {
        lockerCommandRegistry = new LockerCommandRegistry(new com.comandante.creeper.merchant.lockers.UnknownCommand(gameManager));
        lockerCommandRegistry.addCommand(new PutCommand(gameManager));
        lockerCommandRegistry.addCommand(new GetCommand(gameManager));
        lockerCommandRegistry.addCommand(new QueryCommand(gameManager));
        lockerCommandRegistry.addCommand(new com.comandante.creeper.merchant.lockers.DoneCommand(gameManager));
    }

    public static PlayerClassCommandRegistry playerClassCommandRegistry;

    public static void configurePlayerClassSelector(GameManager gameManager) {
       playerClassCommandRegistry = new PlayerClassCommandRegistry(gameManager);
    }

    public static QuestGiverCommandRegistry questGiverCommandRegistry;

    public static void configureQuestGiver(GameManager gameManager) {
        questGiverCommandRegistry = new QuestGiverCommandRegistry(gameManager);
        questGiverCommandRegistry.addCommand(new com.comandante.creeper.merchant.questgiver.LeaveCommand(null, gameManager));
    }


    public static void configure(GameManager gameManager) {
        creeperCommandRegistry = new CreeperCommandRegistry(new com.comandante.creeper.command.commands.UnknownCommand(gameManager));
        creeperCommandRegistry.addCommand(new DropCommand(gameManager));
        creeperCommandRegistry.addCommand(new GossipCommand(gameManager));
        creeperCommandRegistry.addCommand(new InventoryCommand(gameManager));
        creeperCommandRegistry.addCommand(new FightKillCommand(gameManager));
        creeperCommandRegistry.addCommand(new LookCommand(gameManager));
        creeperCommandRegistry.addCommand(new MovementCommand(gameManager));
        creeperCommandRegistry.addCommand(new PickUpCommand(gameManager));
        creeperCommandRegistry.addCommand(new SayCommand(gameManager));
        creeperCommandRegistry.addCommand(new TellCommand(gameManager));
        creeperCommandRegistry.addCommand(new UseCommand(gameManager));
        creeperCommandRegistry.addCommand(new WhoamiCommand(gameManager));
        creeperCommandRegistry.addCommand(new WhoCommand(gameManager));
        creeperCommandRegistry.addCommand(new DescriptionCommand(gameManager));
        creeperCommandRegistry.addCommand(new TitleCommand(gameManager));
        creeperCommandRegistry.addCommand(new TagRoomCommand(gameManager));
        creeperCommandRegistry.addCommand(new SaveWorldCommand(gameManager));
        creeperCommandRegistry.addCommand(new BuildCommand(gameManager));
        creeperCommandRegistry.addCommand(new MapCommand(gameManager));
        creeperCommandRegistry.addCommand(new AreaCommand(gameManager));
        creeperCommandRegistry.addCommand(new HelpCommand(gameManager));
        creeperCommandRegistry.addCommand(new LootCommand(gameManager));
        creeperCommandRegistry.addCommand(new GoldCommand(gameManager));
        creeperCommandRegistry.addCommand(new InfoCommand(gameManager));
        creeperCommandRegistry.addCommand(new TeleportCommand(gameManager));
        creeperCommandRegistry.addCommand(new TalkCommand(gameManager));
        creeperCommandRegistry.addCommand(new EquipCommand(gameManager));
        creeperCommandRegistry.addCommand(new UnequipCommand(gameManager));
        creeperCommandRegistry.addCommand(new QuitCommand(gameManager));
        creeperCommandRegistry.addCommand(new GiveGoldCommand(gameManager));
        creeperCommandRegistry.addCommand(new NexusCommand(gameManager));
        creeperCommandRegistry.addCommand(new ColorsCommand(gameManager));
        creeperCommandRegistry.addCommand(new XpCommand(gameManager));
        creeperCommandRegistry.addCommand(new CastCommand(gameManager));
        creeperCommandRegistry.addCommand(new CountdownCommand(gameManager));
        creeperCommandRegistry.addCommand(new ReloadNpcsCommand(gameManager));
        creeperCommandRegistry.addCommand(new UsersCommand(gameManager));
        creeperCommandRegistry.addCommand(new SpawnCommand(gameManager));
        creeperCommandRegistry.addCommand(new ForageCommand(gameManager));
        creeperCommandRegistry.addCommand(new RecentChangesCommand(gameManager));
        creeperCommandRegistry.addCommand(new BounceIrcBotCommand(gameManager));
        creeperCommandRegistry.addCommand(new OpenCommand(gameManager));
        creeperCommandRegistry.addCommand(new RecentGossipCommand(gameManager));
        creeperCommandRegistry.addCommand(new NotablesCommand(gameManager));
        creeperCommandRegistry.addCommand(new NpcLocationCommand(gameManager));
        creeperCommandRegistry.addCommand(new TimeCommand(gameManager));
        creeperCommandRegistry.addCommand(new ShowCommand(gameManager));
        creeperCommandRegistry.addCommand(new CoolDownCommand(gameManager));
        creeperCommandRegistry.addCommand(new SystemInfo(gameManager));
        creeperCommandRegistry.addCommand(new SetCommand(gameManager));
        creeperCommandRegistry.addCommand(new DelCommand(gameManager));
        creeperCommandRegistry.addCommand(new OpCommand(gameManager));
        creeperCommandRegistry.addCommand(new KillTallyCommand(gameManager));
        creeperCommandRegistry.addCommand(new CompareCommand(gameManager));
        creeperCommandRegistry.addCommand(new CardsCommand(gameManager));
        creeperCommandRegistry.addCommand(new SpellsCommand(gameManager));
        creeperCommandRegistry.addCommand(new LeaveCommand(gameManager));
        creeperCommandRegistry.addCommand(new BackCommand(gameManager));
        creeperCommandRegistry.addCommand(new RecallCommand(gameManager));
        creeperCommandRegistry.addCommand(new ToggleChatCommand(gameManager));
        creeperCommandRegistry.addCommand(new LoadNpcCommand(gameManager));
        creeperCommandRegistry.addCommand(new LoadItemCommand(gameManager));
        creeperCommandRegistry.addCommand(new LoadMerchantCommand(gameManager));
        creeperCommandRegistry.addCommand(new RestartCommand(gameManager));
    }
}
