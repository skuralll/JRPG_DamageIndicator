package com.skuralll.jrpg_damageindicator.packet.packets

import com.comphenix.protocol.events.PacketContainer

abstract class IPacket {

    abstract fun build(): PacketContainer

}