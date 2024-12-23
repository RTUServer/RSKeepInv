package kr.rtuserver.keepinv.listeners;

import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.utility.compatible.ItemCompat;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import kr.rtuserver.keepinv.RSKeepInv;
import kr.rtuserver.keepinv.config.KeepInventoryConfig;
import kr.rtuserver.keepinv.manager.StatusManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemInteract extends RSListener<RSKeepInv> {

    private final KeepInventoryConfig config;
    private final StatusManager manager;

    public ItemInteract(RSKeepInv plugin) {
        super(plugin);
        this.config = plugin.getKeepInventoryConfig();
        this.manager = plugin.getStatusManager();
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent e) {
        if (!List.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK).contains(e.getAction())) return;
        if (config.isAutoProtect()) return;
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItem();
        if (itemStack != null) {
            if (ItemCompat.to(itemStack).equalsIgnoreCase(config.getItem())) {
                PlayerChat chat = PlayerChat.of(getPlugin());
                if (manager.getMap().getOrDefault(player.getUniqueId(), false)) {
                    chat.announce(player, getMessage().get(player, "alreadyUsed"));
                } else {
                    manager.activate(player.getUniqueId());
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    chat.announce(player, getMessage().get(player, "useItem"));
                }
            }
        }
    }
}
