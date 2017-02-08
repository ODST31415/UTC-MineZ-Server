/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package co.avvoire.utc;

import co.avvoire.utc.UTCBattle;
import java.io.PrintStream;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPlugin
extends JavaPlugin
implements Listener {
    public static Plugin plugin;

    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.getServer().getPluginManager().registerEvents((Listener)new UTCBattle(this), (Plugin)this);
        plugin = this;
        System.out.println("UTCBattle is now enabled!");
    }

    public void onDisable() {
        System.out.println("UTCBattle is disabled.");
    }
}

