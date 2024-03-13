package com.skuralll.jrpg_damageindicator.packet.metadata

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Display.Brightness

class ITextDisplayMetadata(var brightness: Brightness? = null, var billboard: Billboard? = null, var textJson: String? = null) : IMetadata() {

    override fun build(): List<WrappedDataValue> {
        val list = mutableListOf<WrappedDataValue>()
        billboard?.let {
            val billboardValue = when (billboard) {
                Billboard.FIXED -> 0x00.toByte()
                Billboard.VERTICAL -> 0x01.toByte()
                Billboard.HORIZONTAL -> 0x02.toByte()
                Billboard.CENTER -> 0x03.toByte()
                else -> 0x00.toByte()
            }
            list.add(
                WrappedDataValue(15, Registry.get(java.lang.Byte::class.java), billboardValue),
            )
        }
        brightness?.let {
            val lightValue = (brightness!!.blockLight shl 4) or (brightness!!.skyLight shl 4)
            list.add(
                WrappedDataValue(16, Registry.get(java.lang.Integer::class.java), lightValue),
            )
        }
        textJson?.let {
            list.add(
                WrappedDataValue(
                    23,
                    Registry.getChatComponentSerializer(false),
                    WrappedChatComponent.fromJson(textJson).handle
                )
            )
        }
        return list
    }

}
/*
            val light = (15 shl 4) or (15 shl 4)
            WrappedDataValue(16, Registry.get(java.lang.Integer::class.java), light),
* */