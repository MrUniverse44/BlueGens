package me.blueslime.bluegens.modules.listeners.api;

import me.blueslime.bluegens.modules.sells.PossibleItemSell;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SellEvent extends Event {
    private List<PossibleItemSell> sellList = new ArrayList<>();
    private final Player player;
    private static final HandlerList handlerList = new HandlerList();

    public SellEvent(List<PossibleItemSell> sellList, Player player) {
        this.sellList.addAll(sellList);
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public void setSellList(List<PossibleItemSell> sellList) {
        this.sellList = sellList;
    }

    /**
     * Performs the given action for each item of this current event.
     * <p>
     *
     * @implSpec
     * <p>The default implementation behaves as if:
     * <pre>{@code
     *     for (T t : this)
     *         action.accept(t);
     * }</pre>
     *
     * @param consumer The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     */
    public void processItems(Consumer<? super PossibleItemSell> consumer) throws NullPointerException {
        sellList.forEach(consumer);
    }

    public List<PossibleItemSell> getSellList() {
        return sellList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Player getPlayer() {
        return player;
    }
}
