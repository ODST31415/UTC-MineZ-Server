package snoop.pi.killmsg;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static class globals{
		
		public static boolean dialogue=false;
		
	}
	
	@Override
	public void onEnable(){
		getLogger().info("Enabled!");
		getServer().getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable(){
		getLogger().info("Disabled!");
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Player p = event.getEntity().getPlayer();
		Player k = event.getEntity().getKiller();
		event.setDeathMessage(ChatColor.YELLOW + k.getName() + ChatColor.RED + " MURDERED " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " with a "+ ChatColor.YELLOW + k.getItemInHand() +"!");
	}
}