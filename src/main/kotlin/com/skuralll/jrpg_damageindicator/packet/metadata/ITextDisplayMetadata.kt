package com.skuralll.jrpg_damageindicator.packet.metadata

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Color
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Display.Brightness

class ITextDisplayMetadata(
    var posInterpolation: Int? = null,
    var brightness: Brightness? = null,
    var billboard: Billboard? = null,
    var textComponent: Component? = null,
    var backgroundColor: Color? = null,
    var textOpacity: Byte? = null, // 0~127 (-1=fully opaque) (-128~128...?)
) : IMetadata() {

    override fun build(): List<WrappedDataValue> {
        val list = mutableListOf<WrappedDataValue>()
        posInterpolation?.let {
            list.add(
                WrappedDataValue(10, Registry.get(java.lang.Integer::class.java), posInterpolation!!),
            )
        }
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
        textComponent?.let {
            list.add(
                WrappedDataValue(
                    23,
                    Registry.getChatComponentSerializer(false),
                    WrappedChatComponent.fromJson(JSONComponentSerializer.json().serialize(textComponent!!)).handle
                )
            )
        }
        backgroundColor?.let {
            list.add(
                WrappedDataValue(25, Registry.get(java.lang.Integer::class.java), backgroundColor!!.asARGB()),
            )
        }
        textOpacity?.let {
            list.add(
                WrappedDataValue(26, Registry.get(java.lang.Byte::class.java), textOpacity!!),
            )
        }
        return list
    }

}
