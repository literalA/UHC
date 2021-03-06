/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.HardcoreHeartsModule
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.health;

import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.autorespawn.AutoRespawnModule;
import gg.uhc.uhc.modules.events.ModuleDisableEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HardcoreHeartsModule extends Module implements Listener {

    protected static final short PLANT_TYPE_ID = 4;
    protected static final String ICON_NAME = "Hardcore Hearts";

    protected final AutoRespawnModule respawnModule;

    public HardcoreHeartsModule(AutoRespawnModule respawnModule) {
        setId("HardcoreHearts");

        this.respawnModule = respawnModule;
        this.icon.setType(Material.DOUBLE_PLANT);
        this.icon.setDurability(PLANT_TYPE_ID);
        this.icon.setWeight(ModuleRegistry.CATEGORY_HEALTH);
        this.icon.setDisplayName(ICON_NAME);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        // make sure to enable the respawn module
        respawnModule.enable();

        if (!respawnModule.isEnabled()) {
            throw new InvalidConfigurationException(
                    "Error enabling the respawn module. The respawn module is required to run hardcore hearts"
            );
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new HardcoreHeartsListener());

        this.icon.setLore(messages.getRawStrings("lore"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ModuleDisableEvent event) {
        if (event.getModule() == respawnModule) event.setCancelled(true);
    }

    class HardcoreHeartsListener extends PacketAdapter {

        HardcoreHeartsListener() {
            super(HardcoreHeartsModule.this.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event.getPacketType().equals(PacketType.Play.Server.LOGIN)) {
                event.getPacket().getBooleans().write(0, true);
            }
        }
    }
}
