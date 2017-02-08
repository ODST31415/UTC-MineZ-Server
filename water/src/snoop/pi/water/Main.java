package snoop.pi.water;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_11_R1.ItemPotion;

public class Main extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable(){
		getLogger().info("Enabled!");
		getServer().getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable(){
		getLogger().info("Disabled!");
	}		
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void itemConsume(PlayerItemConsumeEvent e){
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		ItemMeta potion = new Potion(PotionType.WATER).toItemStack(1).getItemMeta();
		if (!(item.getItemMeta().equals(potion))) return;
		p.setLevel(20);
		}

	@EventHandler
	public void respawn(PlayerRespawnEvent e){
		Player p = e.getPlayer();
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                p.setLevel(20);
            }
        }, 1L);
	}
	@EventHandler
	public void drain(PlayerJoinEvent e){
		Player p = e.getPlayer();
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	p.setLevel(p.getLevel()-1);
            }
        }, 0L, 1800L);
		
	}
	@EventHandler
	public void messages(PlayerLevelChangeEvent e){
		Player p = e.getPlayer();
		if (p.getLevel()==20) p.sendMessage("§2All refreshed!");
		if (p.getLevel()==10) p.sendMessage("§aI'm a little bit thirsty.");
    	if (p.getLevel()==5) p.sendMessage("§eI'm thirsty.");
    	if (p.getLevel()==2) p.sendMessage("§cI should drink some shit or something...");
    	
    	if (p.getLevel()==0){

    	BukkitScheduler scheduler = getServer().getScheduler();
    	scheduler.scheduleSyncRepeatingTask(this,new Runnable(){

    		@Override
    		public void run(){
    			if (p.getLevel() != 0){ 
    				scheduler.cancelAllTasks();
    				return;
    			}
    			p.damage(2);
    			p.sendMessage("§4Oh Shit nigga, Drink up!");
    				
    		}
    	},0L, 50L);
    	}
    	
	}
}
