package com.dayton.oneinthechamber.tasks;

import com.dayton.oneinthechamber.OITC;
import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.utils.Message;
import org.bukkit.scheduler.BukkitRunnable;

public class StartCountdown extends BukkitRunnable {

    private int timeUntilStart;
    private Arena arena;

    public StartCountdown(Arena arena, int timeUntilStart) {
        this.arena = arena;
        this.timeUntilStart = timeUntilStart;
        runTaskTimer(OITC.plugin, 0, 20);
    }

    public void run() {
        if (timeUntilStart <= 0) {
            cancel();
            arena.messageAll("arena-started");
            arena.start();
        } else {
            if (timeUntilStart % 10 == 0 || timeUntilStart <= 5) {
                arena.messageAll("arena-starting", Message.makePlaceholder("time", timeUntilStart));
            }
            timeUntilStart--;
        }
    }

}
