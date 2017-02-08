package co.avvoire.utc;

import co.avvoire.utc.MainPlugin;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class UTCBattle
implements Listener {
    private Plugin p;
    private HashMap<UUID, UUID> slimeball = new HashMap();

    public UTCBattle(MainPlugin plugin) {
        this.p = plugin;
    }
///Flashgrenade throwing
    @EventHandler
    public void flashGrenade(PlayerInteractEvent e) {
    	///Declarations
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        final World world = player.getWorld();
        ///Test for item in hand
        if (!(item.getType() == Material.SLIME_BALL && (e.getAction() == (Action.RIGHT_CLICK_AIR) || e.getAction() == (Action.RIGHT_CLICK_BLOCK)))) return;
        ///Apply initial effects to slimeball
        Location el = player.getEyeLocation();
        final Item flash = e.getPlayer().getWorld().dropItem(el, new ItemStack(Material.SLIME_BALL));
        player.setItemInHand(null);
        flash.setPickupDelay(Integer.MAX_VALUE);
        flash.setVelocity(el.getDirection());
        
        ///Schedules pop
        BukkitRunnable runnable = new BukkitRunnable(){

                public void run() {
                    if (flash.isOnGround() || world.getBlockAt(flash.getLocation()).getType() != Material.AIR) {
                        Player[] arrplayer = Bukkit.getServer().getOnlinePlayers();
                        int n = arrplayer.length;
                        int n2 = 0;
                        while (n2 < n) {
                            Player others = arrplayer[n2];
                            if (others.getLocation().distance(flash.getLocation()) < 4.0) {
///Doesn't slow                                others.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
                                others.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2));
                            }
                            ++n2;
                        }
                        flash.remove();
                        this.cancel();
                    }
                }
            };
            runnable.runTaskTimer(this.p, 0, 2);
            e.setCancelled(true);
            }

    public void stopScheduler(int taskId) {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }

    @EventHandler
    public void enderGrenade(ProjectileHitEvent e) {
        Projectile ender = e.getEntity();
        Location le = ender.getLocation();
        World world = e.getEntity().getWorld();
        if (ender instanceof EnderPearl) {
            ender.getWorld().playEffect(le, Effect.SMOKE, 31);
            world.playSound(le, Sound.EXPLODE, 10.0f, 1.0f);
            Player[] arrplayer = Bukkit.getServer().getOnlinePlayers();
            int n = arrplayer.length;
            int n2 = 0;
            while (n2 < n) {
                Player others = arrplayer[n2];
                if (others.getLocation().distance(le) < 4.0) {
                    Location play = others.getLocation();
                    Vector vec = this.getPushback(le, play);
                    vec.normalize();
                    others.setVelocity(vec.multiply(-1));
                    if (others.getLocation().distance(le) < 2.0) {
                        if (others.getHealth() < 6.0) {
                            others.setHealth(others.getHealth() - others.getHealth());
                        } else {
                            others.setHealth(others.getHealth() - 6.0);
                        }
                    } else {
                        others.setHealth(others.getHealth() - 3.0);
                    }
                }
                ++n2;
            }
        }
    }

    public Vector getPushback(Location origin, Location player) {
        double deltaX = (origin.getX() - player.getX()) / 10.0;
        double deltaZ = (origin.getZ() - player.getZ()) / 10.0;
        Vector v = new Vector(deltaX, -0.03, deltaZ);
        return v;
    }

    @EventHandler
    public void stopTeleport(PlayerTeleportEvent e) {
        if (e.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            e.setCancelled(true);
        }
    }

    ///@EventHandler
    ///public void dropSlime(PlayerDropItemEvent e) {
        ///if (e.getItemDrop().getItemStack().getType() == Material.SLIME_BALL) {
            ///this.slimeball.put(e.getItemDrop().getUniqueId(), e.getPlayer().getUniqueId());
        ///}
    ///}

///    @EventHandler
///    public void stopPickup(PlayerPickupItemEvent e) {
///        if (e.getItem().getItemStack().getType() == Material.SLIME_BALL) {
///            if (!this.slimeball.containsKey(e.getItem().getUniqueId())) {
///                e.setCancelled(true);
///            } else {
///                this.slimeball.remove(e.getItem().getUniqueId());
///            }
///        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (!(this.InAir(e.getHook().getLocation(), e.getPlayer().getWorld()) || e.getState() != PlayerFishEvent.State.IN_GROUND && e.getState() != PlayerFishEvent.State.FAILED_ATTEMPT)) {
            Vector vec = this.getPull(e.getHook().getLocation(), e.getPlayer().getLocation());
            System.out.println(vec.toString());
            e.getPlayer().setVelocity(vec.multiply(2));
        }
    }

    public boolean InAir(Location l, World w) {
        int z;
        int y;
        int x = l.getBlockX();
        if (w.getBlockAt(x, y = l.getBlockY(), z = l.getBlockZ()).getType() == Material.AIR && w.getBlockAt(x, y - 1, z).getType() == Material.AIR && w.getBlockAt(x, y, z + 1).getType() == Material.AIR && w.getBlockAt(x + 1, y, z).getType() == Material.AIR) {
            return true;
        }
        return false;
    }

    public Vector getPull(Location origin, Location player) {
        double deltaX = origin.getX() - player.getX();
        double deltaY = origin.getY() - player.getY();
        double deltaZ = origin.getZ() - player.getZ();
        if (Math.abs(deltaY) < 2.0) {
            deltaX = 0.0;
            deltaY = 0.0;
            deltaZ = 0.0;
        }
        Vector v = new Vector(deltaX, deltaY, deltaZ);
        v.normalize();
        return v;
    }

    @EventHandler
    public void useSugar(PlayerInteractEvent e) {
        if (e.getPlayer().getItemInHand().getType() == Material.SUGAR && e.getAction() == Action.RIGHT_CLICK_AIR) {
            final Player player = e.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 800, 1));
            player.getInventory().setItemInHand(new ItemStack(Material.AIR));
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.p, new Runnable(){

                @Override
                public void run() {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                }
            }, 800);
        }
    }

}

