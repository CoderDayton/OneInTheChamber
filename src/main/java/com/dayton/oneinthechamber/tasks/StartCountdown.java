package com.dayton.oneinthechamber.tasks;

import com.dayton.oneinthechamber.OneInTheChamber;
import com.dayton.oneinthechamber.core.Arena;
import org.bukkit.scheduler.BukkitRunnable;

public class StartCountdown extends BukkitRunnable {

    private int timeUntilStart;
    private Arena arena;

    public StartCountdown(Arena arena, int timeUntilStart) {
        this.arena = arena;
        this.timeUntilStart = timeUntilStart;
        runTaskTimer(OneInTheChamber.plugin, 0, 20);
    }

    public void run() {
        if (timeUntilStart <= 0) {
            cancel();
            arena.messageAll(OneInTheChamber.prefix + "§6Game has been started!");
            arena.start();
        } else {
            if (timeUntilStart % 10 == 0 || timeUntilStart <= 5) {
                arena.messageAll(OneInTheChamber.prefix + "§6Game starting in §c" + timeUntilStart + " §6second(s).");
            }
            timeUntilStart--;
        }
    }

}