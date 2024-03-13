package com.skuralll.jrpg_damageindicator

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import com.skuralll.jrpg_damageindicator.packet.PacketHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Color
import org.bukkit.plugin.java.JavaPlugin

class JRPGDamageIndicator : JavaPlugin() {

    // Packet Handler
    private val packetHandler: PacketHandler by lazy {
        PacketHandler()
    }

    // IndicatorController
    private val indicatorController: IndicatorController by lazy {
        IndicatorController(this, packetHandler)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener(indicatorController), this)
    }

    override fun onDisable() {

    }
}
