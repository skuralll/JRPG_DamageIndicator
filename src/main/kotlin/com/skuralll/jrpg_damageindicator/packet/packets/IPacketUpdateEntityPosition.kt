package com.skuralll.jrpg_damageindicator.packet.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.collections.IntegerMap
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.UUID

class IPacketUpdateEntityPosition(var entityId: Int, var prevPosition: Vector, var newPosition: Vector, var onGround: Boolean? = false) :
    IPacket() {

    override fun build(): PacketContainer {
        val relPosition = newPosition.clone().subtract(prevPosition)
        val relPositionShort = relPosition.multiply(32 * 128)
        val packet = PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE)
        packet.integers.write(0, entityId)
        packet.shorts.write(0, relPositionShort.x.toInt().toShort())
        packet.shorts.write(1, relPositionShort.y.toInt().toShort())
        packet.shorts.write(2, relPositionShort.z.toInt().toShort())
        packet.booleans.write(0, onGround)
        return packet
    }

}