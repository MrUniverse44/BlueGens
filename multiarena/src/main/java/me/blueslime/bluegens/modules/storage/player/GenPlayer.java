package me.blueslime.bluegens.modules.storage.player;

import me.blueslime.bluegens.BlueGens;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GenPlayer {

    private Data data = Data.empty();

    private final UUID uuid;

    public GenPlayer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public GenPlayer(Data data, Player player) {
        this.uuid = player.getUniqueId();
        this.data = data;
    }
    public String getName() {
        Player player = getBukkitPlayer();

        if (player == null) {
            return "";
        }

        return player.getName();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Data getData() {
        return data;
    }

    public Player getBukkitPlayer() {
        BlueGens plugin = BlueGens.getPlugin(BlueGens.class);

        return plugin.getServer().getPlayer(uuid);
    }

    public String getId() {
        return uuid.toString().replace("-", "");
    }


    public static class Data {
        private int coins;

        public Data(int coins) {
            this.coins = coins;
        }

        public int getCoins() {
            return coins;
        }

        public void reset() {
            coins  = 0;
        }

        public static Data empty() {
            return new Data(0);
        }
    }

}
