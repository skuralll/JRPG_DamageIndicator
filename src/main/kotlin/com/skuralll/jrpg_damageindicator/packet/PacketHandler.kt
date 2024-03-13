package com.skuralll.jrpg_damageindicator.packet

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

class PacketHandler {

    // ProtocolLib
    private val protocolManager: ProtocolManager by lazy {
        ProtocolLibrary.getProtocolManager()
    }

    // send packet to player
    fun sendPacket(player: Player, container: PacketContainer) {
        try {
            protocolManager.sendServerPacket(player, container)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
