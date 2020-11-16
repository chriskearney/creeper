package com.comandante.creeper.configuration;

import com.comandante.creeper.command.CreeperCommandRegistry;
import com.comandante.creeper.command.commands.BackCommand;
import com.comandante.creeper.command.commands.CardsCommand;
import com.comandante.creeper.command.commands.CastCommand;
import com.comandante.creeper.command.commands.ColorsCommand;
import com.comandante.creeper.command.commands.CompareCommand;
import com.comandante.creeper.command.commands.CoolDownCommand;
import com.comandante.creeper.command.commands.CountdownCommand;
import com.comandante.creeper.command.commands.DelCommand;
import com.comandante.creeper.command.commands.DropCommand;
import com.comandante.creeper.command.commands.EquipCommand;
import com.comandante.creeper.command.commands.FightKillCommand;
import com.comandante.creeper.command.commands.ForageCommand;
import com.comandante.creeper.command.commands.GoldCommand;
import com.comandante.creeper.command.commands.GossipCommand;
import com.comandante.creeper.command.commands.HelpCommand;
import com.comandante.creeper.command.commands.InventoryCommand;
import com.comandante.creeper.command.commands.KillTallyCommand;
import com.comandante.creeper.command.commands.LeaveCommand;
import com.comandante.creeper.command.commands.LookCommand;
import com.comandante.creeper.command.commands.LootCommand;
import com.comandante.creeper.command.commands.MapCommand;
import com.comandante.creeper.command.commands.MovementCommand;
import com.comandante.creeper.command.commands.NexusCommand;
import com.comandante.creeper.command.commands.OpCommand;
import com.comandante.creeper.command.commands.OpenCommand;
import com.comandante.creeper.command.commands.PickUpCommand;
import com.comandante.creeper.command.commands.QuestsCommand;
import com.comandante.creeper.command.commands.QuitCommand;
import com.comandante.creeper.command.commands.RecallCommand;
import com.comandante.creeper.command.commands.RecentChangesCommand;
import com.comandante.creeper.command.commands.RecentGossipCommand;
import com.comandante.creeper.command.commands.SayCommand;
import com.comandante.creeper.command.commands.SetCommand;
import com.comandante.creeper.command.commands.ShowCommand;
import com.comandante.creeper.command.commands.SpellsCommand;
import com.comandante.creeper.command.commands.TalkCommand;
import com.comandante.creeper.command.commands.TellCommand;
import com.comandante.creeper.command.commands.TimeCommand;
import com.comandante.creeper.command.commands.ToggleChatCommand;
import com.comandante.creeper.command.commands.UnequipCommand;
import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.command.commands.UsersCommand;
import com.comandante.creeper.command.commands.WhoCommand;
import com.comandante.creeper.command.commands.WhoamiCommand;
import com.comandante.creeper.command.commands.XpCommand;
import com.comandante.creeper.command.commands.admin.AreaCommand;
import com.comandante.creeper.command.commands.admin.BounceIrcBotCommand;
import com.comandante.creeper.command.commands.admin.BuildCommand;
import com.comandante.creeper.command.commands.admin.DescriptionCommand;
import com.comandante.creeper.command.commands.admin.GiveGoldCommand;
import com.comandante.creeper.command.commands.admin.InfoCommand;
import com.comandante.creeper.command.commands.admin.LoadItemCommand;
import com.comandante.creeper.command.commands.admin.LoadMerchantCommand;
import com.comandante.creeper.command.commands.admin.LoadNpcCommand;
import com.comandante.creeper.command.commands.admin.LoadQuoteFile;
import com.comandante.creeper.command.commands.admin.NotablesCommand;
import com.comandante.creeper.command.commands.admin.NpcLocationCommand;
import com.comandante.creeper.command.commands.admin.ReloadNpcsCommand;
import com.comandante.creeper.command.commands.admin.RestartCommand;
import com.comandante.creeper.command.commands.admin.SaveWorldCommand;
import com.comandante.creeper.command.commands.admin.SpawnCommand;
import com.comandante.creeper.command.commands.admin.SystemInfo;
import com.comandante.creeper.command.commands.admin.TagRoomCommand;
import com.comandante.creeper.command.commands.admin.TeleportCommand;
import com.comandante.creeper.command.commands.admin.TitleCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.bank.commands.AccountQueryCommand;
import com.comandante.creeper.merchant.bank.commands.BankCommandRegistry;
import com.comandante.creeper.merchant.bank.commands.DepositCommand;
import com.comandante.creeper.merchant.bank.commands.DoneCommand;
import com.comandante.creeper.merchant.bank.commands.WithdrawalCommand;
import com.comandante.creeper.merchant.lockers.GetCommand;
import com.comandante.creeper.merchant.lockers.LockerCommandRegistry;
import com.comandante.creeper.merchant.lockers.PutCommand;
import com.comandante.creeper.merchant.lockers.QueryCommand;
import com.comandante.creeper.merchant.playerclass_selector.PlayerClassCommandRegistry;
import com.comandante.creeper.merchant.questgiver.AcceptCommand;
import com.comandante.creeper.merchant.questgiver.CompleteCommand;
import com.comandante.creeper.merchant.questgiver.ListCommand;
import com.comandante.creeper.merchant.questgiver.QuestGiverCommandRegistry;
import com.comandante.creeper.merchant.questgiver.ReviewCommand;

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
        questGiverCommandRegistry.addCommand(new ReviewCommand(null, gameManager));
        questGiverCommandRegistry.addCommand(new ListCommand(null, gameManager));
        questGiverCommandRegistry.addCommand(new AcceptCommand(null, gameManager));
        questGiverCommandRegistry.addCommand(new CompleteCommand(null, gameManager));
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
        creeperCommandRegistry.addCommand(new QuestsCommand(gameManager));
        creeperCommandRegistry.addCommand(new LoadQuoteFile(gameManager));
    }
}
