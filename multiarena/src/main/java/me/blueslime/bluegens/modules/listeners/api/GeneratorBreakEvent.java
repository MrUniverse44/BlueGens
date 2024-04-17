package me.blueslime.bluegens.modules.listeners.api;

import me.blueslime.bluegens.modules.generators.generator.Generator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GeneratorBreakEvent extends Event {
    private final Generator generator;
    private final Player owner;
    private static final HandlerList handlerList = new HandlerList();

    public GeneratorBreakEvent(Generator generator, Player owner) {
        this.generator = generator;
        this.owner = owner;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Generator getCurrentGenerator() {
        return generator;
    }
    public Player getOwner() {
        return owner;
    }
}
