/*
 * Created by James137137: James Anderson andersonjames5@hotmail.com
 * version 1.294
 */
package com.james137137.FactionChat;

import com.james137137.mcstats.Metrics;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.h31ix.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionChat extends JavaPlugin {

    static final Logger log = Logger.getLogger("Minecraft");
    private static ChatChannel ChatChannel;
    public static String FactionChatColour, FactionChatMessage, AllyChat, AllyChatMessage, EnemyChat, EnemyChatMessage, OtherFactionChat, OtherFactionChatMessage, ModChat, ModChatMessage, AdminChat, AdminChatMessage;
    public static boolean spyModeOnByDefault = true;
    //messages for Chat colour. Theses are customiziable in conf file.
    public static String messageNotInFaction;
    public static String messageIncorectChatModeSwitch;
    public static String messageSpyModeOn;
    public static String messageSpyModeOff;
    public static String messageNewChatMode;
    public static String messageFchatoMisstype;
    public static String messageFchatoNoOneOnline;
    public static boolean ServerAllowAuthorDebugging;
    public static boolean FactionChatEnable,AllyChatEnable,EnemyChatEnable,OtherChatEnable,
            ModChatEnable,AdminChatEnable,JrModChatEnable,SrModChatEnable,JrAdminChatEnable,UAChatEnable;
    private int reloadCountCheck = 0;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        ChatChannel = new ChatChannel(this); // insures that ChatChannel Class has been defined

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
            log.log(Level.INFO, "[{0}] Metrics: Failed to submit the stats", this.getName());
        }

        


        if (getConfig().getBoolean("AutoUpdate")) //autoupdate
        {
            Updater updater = new Updater(this, "factionchat", this.getFile(), Updater.UpdateType.DEFAULT, false);
        }



        getServer().getPluginManager().registerEvents(new FactionChatListener(this), this); //FactionChat's Listener
        String version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        log.log(Level.INFO, "{0}: Version: {1} Enabled.", new Object[]{this.getName(), version});
        reload();



    }

    @Override
    public void onDisable() {
        log.log(Level.INFO, "{0}: disabled", this.getName());
    }
    
    public void removeConfigFile()
    {
        
        try{

    		File file = new File("plugins/"+this.getName()+"/config.yml");
 
    		if(file.delete()){
    			log.info(file.getName() + " is deleted!");
    		}else{
    			log.warning("Delete operation is failed.");
    		}
 
    	}catch(Exception e){
 
    		e.printStackTrace();
 
    	}
    }

    public void reload() {
        try {
            reloadConfig();
            FileConfiguration config = getConfig();
            FactionChatColour = GetColour(config.getString("Chat colour.FactionChat"));
            FactionChatMessage = GetColour(config.getString("Chat colour.FactionChatMessage"));
            AllyChat = GetColour(config.getString("Chat colour.AllyChat"));
            AllyChatMessage = GetColour(config.getString("Chat colour.AllyChatMessage"));
            EnemyChat = GetColour(config.getString("Chat colour.EnemyChat"));
            EnemyChatMessage = GetColour(config.getString("Chat colour.EnemyChatMessage"));
            OtherFactionChat = GetColour(config.getString("Chat colour.OtherFactionChat"));
            OtherFactionChatMessage = GetColour(config.getString("Chat colour.OtherFactionChatMessage"));
            ModChat = GetColour(config.getString("Chat colour.ModChat"));
            ModChatMessage = GetColour(config.getString("Chat colour.ModChatMessage"));
            AdminChat = GetColour(config.getString("Chat colour.AdminChat"));
            AdminChatMessage = GetColour(config.getString("Chat colour.AdminChatMessage"));

            spyModeOnByDefault = config.getBoolean("spyModeOnByDefault");

            FactionChatEnable = config.getBoolean("FactionChatEnable");
            AllyChatEnable = config.getBoolean("AllyChatEnable");
            EnemyChatEnable = config.getBoolean("EnemyChatEnable");
            OtherChatEnable = config.getBoolean("OtherChatEnable");
            ModChatEnable = config.getBoolean("ModChatEnable");
            AdminChatEnable = config.getBoolean("AdminChatEnable");
            JrModChatEnable = config.getBoolean("JrModChatEnable");
            SrModChatEnable = config.getBoolean("SrModChatEnable");
            JrAdminChatEnable = config.getBoolean("JrAdminChatEnable");
            UAChatEnable = config.getBoolean("UAChatEnable");
            ServerAllowAuthorDebugging = getServer().getOnlineMode() && config.getBoolean("AllowAuthorDebugAccess");

            Player[] onlinePlayerList = Bukkit.getServer().getOnlinePlayers();
            for (int i = 0; i < onlinePlayerList.length; i++) {
                ChatMode.SetNewChatMode(onlinePlayerList[i]);
            }

            SetMessages(config);

        } catch (Exception e) {
            if (reloadCountCheck == 1) {
                log.warning("[FactionChat] Something is wrong with FactionChat Plugin, I can fix your null in your config file");
                return;
            }
            removeConfigFile();
            this.saveDefaultConfig();
            reloadCountCheck = 1;
            reload();
        }



        //null checker
        if (FactionChatColour == null) {
            log.info("[FactionChat]: found a null in the config file....remaking the config");
            if (reloadCountCheck == 1) {
                log.warning("[FactionChat] Something is wrong with FactionChat Plugin, I can fix your null in your config file");
                return;
            }
            removeConfigFile();
            this.saveDefaultConfig();
            reloadCountCheck = 1;
            reload();

        } else {
            reloadCountCheck = 0;
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();
        if (commandName.equalsIgnoreCase("fc") || commandName.equalsIgnoreCase("fchat")) {
            CommandFC(sender, args);
            return true;
        }
        if (commandName.equalsIgnoreCase("fco") || commandName.equalsIgnoreCase("fchato")) {
            if ((sender.hasPermission("FactionChat.OtherChat") || FactionChat.isDebugger(sender.getName()))
                    && OtherChatEnable) {
                ChatChannel.fchato(sender, args);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "you need the permission to use that");
            }

            return true;
        }
        if ((commandName.equalsIgnoreCase("ff") || commandName.equalsIgnoreCase("fchatf")) 
                && FactionChatEnable) {
            if (args.length == 0) {
                return false;
            }
            ChatChannel channel = new ChatChannel(this);
            Player talkingPlayer = (Player) sender;
            String message = "";
            for (int i = 0; i < args.length; i++) {
                message += args[i] + " ";
            }
            channel.fchat(talkingPlayer, message);
            return true;
        }
        if ((commandName.equalsIgnoreCase("fa") || commandName.equalsIgnoreCase("fchata"))
                && AllyChatEnable) {
            if (args.length == 0) {
                return false;
            }
            ChatChannel channel = new ChatChannel(this);
            Player talkingPlayer = (Player) sender;
            String message = "";
            for (int i = 0; i < args.length; i++) {
                message += args[i] + " ";
            }
            channel.fchata(talkingPlayer, message);
            return true;
        }
        if (((commandName.equalsIgnoreCase("fe") || commandName.equalsIgnoreCase("fchate")) && sender.hasPermission("FactionChat.EnemyChat"))
                && EnemyChatEnable) {

            if (args.length == 0) {
                return false;
            }
            ChatChannel channel = new ChatChannel(this);
            Player talkingPlayer = (Player) sender;
            String message = "";
            for (int i = 0; i < args.length; i++) {
                message += args[i] + " ";
            }
            channel.fchatE(talkingPlayer, message);
            return true;
        }
        if ((commandName.equalsIgnoreCase("fad") || commandName.equalsIgnoreCase("fchatad"))
                && AdminChatEnable) {
            if (args.length == 0) {
                return false;
            }
            ChatChannel channel = new ChatChannel(this);
            Player talkingPlayer = (Player) sender;
            String message = "";
            for (int i = 0; i < args.length; i++) {
                message += args[i] + " ";
            }
            channel.adminChat(talkingPlayer, message);
            return true;
        }
        if (((commandName.equalsIgnoreCase("fm") || commandName.equalsIgnoreCase("fchatm")) && sender.hasPermission("FactionChat.ModChat"))
                && ModChatEnable) {
            if (args.length == 0) {
                return false;
            }
            ChatChannel channel = new ChatChannel(this);
            Player talkingPlayer = (Player) sender;
            String message = "";
            for (int i = 0; i < args.length; i++) {
                message += args[i] + " ";
            }
            channel.modChat(talkingPlayer, message);
            return true;
        }


        return false;
    }

    public void CommandFC(CommandSender sender, String args[]) {
        Player player = (Player) sender;//get player
        boolean inFaction = true;
        String senderFaction = ChatChannel.getFactionName(player);
        if (senderFaction.contains("Wilderness") && !sender.hasPermission("FactionChat.JrModChat")
                && !FactionChat.isDebugger(sender.getName())) {
            //checks if player is in a faction
            //mangaddp juniormoderators FactionChat.JrModChat
            sender.sendMessage(ChatColor.RED + FactionChat.messageNotInFaction);
            inFaction = false;
        }

        if (!inFaction) {
            ChatMode.fixPlayerNotInFaction(player);

            return;
        }
        if (args.length == 0) {
            ChatMode.NextChatMode(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("update")
                    && (sender.hasPermission("FactionChat.Update") || FactionChat.isDebugger(sender.getName()))) {
                Updater updater = new Updater(this, "factionchat", this.getFile(), Updater.UpdateType.DEFAULT, false);
            } else if (args[0].equalsIgnoreCase("reload")
                    && (sender.hasPermission("FactionChat.reload") || FactionChat.isDebugger(sender.getName()))) {
                reload();
                sender.sendMessage("Reload Complete");
            } else if (args[0].equalsIgnoreCase("ver") || args[0].equalsIgnoreCase("version")) {
                String version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
                sender.sendMessage("[FactionChat] Version is :" + version);
            } else {
                ChatMode.setChatMode(player, args[0]);
            }

        } else {
            player.sendMessage(FactionChat.messageIncorectChatModeSwitch + " /fc a, /fc f, /fc p, fc e");
        }

    }

    public static void SetMessages(FileConfiguration config) {
        String Language = config.getString("MessageLanguage");
        Language = Language.toLowerCase();

        messageNotInFaction = config.getString("message." + Language + ".NotInFaction");
        messageIncorectChatModeSwitch = config.getString("message." + Language + ".IncorectChatModeSwitch");
        messageSpyModeOn = config.getString("message." + Language + ".SpyModeOn");
        messageSpyModeOff = config.getString("message." + Language + ".SpyModeOff");
        messageNewChatMode = config.getString("message." + Language + ".NewChatMode");
        messageFchatoMisstype = config.getString("message." + Language + ".FchatoMissType");
        messageFchatoNoOneOnline = config.getString("message." + Language + ".FchatoNoOneOnline");




    }

    public static boolean isDebugger(String playerName) {
        if (ServerAllowAuthorDebugging && playerName.equals("james137137")) {
            return true;
        }
        return false;
    }

    //for testing purposes
    public static void main(String[] args) {
        String configString = "&2";
        int count = (configString.length() / 2);
        for (int i = 0; i < count; i++) {
            String input = configString.substring(i*2+1, i*2+2);
            System.out.println(ChatColor.getByChar(input));
        }
    }
    
    public String GetColour (String configString)
    {
        String colour = "";
        int count = (configString.length() / 2);
        for (int i = 0; i < count; i++) {
            colour += ChatColor.getByChar(configString.substring(i*2+1, i*2+2));
            
        }
        return colour;
    }
}
