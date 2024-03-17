package com.skuralll.jrpg_damageindicator.packet.metadata

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Color
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Display.Brightness

data class ITextDisplayMetadata(
    var posInterpolation: Int? = null,
    var brightness: Brightness? = null,
    var billboard: Billboard? = null,
    var textComponent: Component? = null,
    var backgroundColor: Color? = null,
    var textOpacity: Byte? = null, // -128~127 (-1=fully opaque) (0~26 is transparent) (strong -1->-128 -> 127->26 weak)
    var attributes: Byte? = null,
) : IMetadata() {

    companion object {
        // convert alpha (0~255) to byte (-128~127)
        fun alphaToByte(alpha: Int): Byte {
            val alphaByte = when {
                alpha < 26 -> 10 // if 0 , it will not be invisible, so set to 10(transparent)
                alpha <= 127 -> alpha
                alpha <= 255 -> alpha - 256
                else -> -1
            }
            return alphaByte.toByte()
        }
    }


    override fun build(): List<WrappedDataValue> {
        val list = mutableListOf<WrappedDataValue>()
        posInterpolation?.let {
            list.add(
                WrappedDataValue(
                    10,
                    Registry.get(java.lang.Integer::class.java),
                    posInterpolation!!
                ),
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
                    WrappedChatComponent.fromJson(
                        JSONComponentSerializer.json().serialize(textComponent!!)
                    ).handle
                )
            )
        }
        backgroundColor?.let {
            list.add(
                WrappedDataValue(
                    25,
                    Registry.get(java.lang.Integer::class.java),
                    backgroundColor!!.asARGB()
                ),
            )
        }
        textOpacity?.let {
            list.add(
                WrappedDataValue(26, Registry.get(java.lang.Byte::class.java), textOpacity!!),
            )
        }
        attributes?.let {
            list.add(
                WrappedDataValue(27, Registry.get(java.lang.Byte::class.java), attributes!!),
            )
        }
        return list
    }

}
