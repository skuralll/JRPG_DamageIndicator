package com.skuralll.jrpg_damageindicator.packet.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.collections.IntegerMap
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.UUID

class IPacketDestroyEntity(var entityIds: List<Int>) :
    IPacket() {

    override fun build(): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        packet.intLists.write(0, entityIds)
        return packet
    }

}