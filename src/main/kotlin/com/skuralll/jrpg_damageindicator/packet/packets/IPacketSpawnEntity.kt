package com.skuralll.jrpg_damageindicator.packet.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.UUID

class IPacketSpawnEntity(var entityId: Int, var uuid: UUID, var entityType: EntityType, var position: Vector) :
    IPacket() {

    override fun build(): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        packet.integers.write(0, entityId)
        packet.uuiDs.write(0, uuid)
        packet.entityTypeModifier.write(0, entityType)
        packet.doubles
            .write(0, position.x)
            .write(1, position.y)
            .write(2, position.z)
        return packet
    }

}