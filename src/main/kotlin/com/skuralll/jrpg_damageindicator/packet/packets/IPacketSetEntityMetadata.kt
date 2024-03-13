package com.skuralll.jrpg_damageindicator.packet.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.collections.IntegerMap
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.UUID

class IPacketSetEntityMetadata(var entityId: Int, var metadata: List<WrappedDataValue>) :
    IPacket() {

    override fun build(): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, entityId)
        packet.dataValueCollectionModifier.write(0, metadata)
        return packet
    }

}