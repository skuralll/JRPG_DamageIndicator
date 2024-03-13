package com.skuralll.jrpg_damageindicator.indicator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import com.skuralll.jrpg_damageindicator.packet.PacketHandler
import com.skuralll.jrpg_damageindicator.packet.metadata.ITextDisplayMetadata
import com.skuralll.jrpg_damageindicator.packet.packets.IPacketDestroyEntity
import com.skuralll.jrpg_damageindicator.packet.packets.IPacketSetEntityMetadata
import com.skuralll.jrpg_damageindicator.packet.packets.IPacketSpawnEntity
import net.kyori.adventure.text.Component
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class Indicator(private val packetHandler: PacketHandler, private val player: Player, private val vector: Vector) :
    Vector(vector.x, vector.y, vector.z) {

    // tick count after spawning
    private var tick = AtomicInteger(0)

    // Entity ID counter
    companion object {
        val ENTITY_COUNTER = AtomicInteger(-Random().nextInt())
    }

    // Entity ID
    val entityId = ENTITY_COUNTER.getAndDecrement()

    // whether to continue updating
    private var _alive = true
    val alive: Boolean
        get() = _alive

    // show(spawn) entity on client side
    fun show() {
        packetHandler.sendPacket(player, IPacketSpawnEntity(entityId, UUID.randomUUID(), EntityType.TEXT_DISPLAY, this).build())
        sendMetadata()
    }

    fun update() {
        if (!player.isOnline) {
            _alive = false
            return;
        }
        val now = tick.incrementAndGet()
        if (now > 40) {
            _alive = false
            return
        }
    }

    // hide(remove) entity on client side
    fun hide() {
        packetHandler.sendPacket(player, IPacketDestroyEntity(listOf(entityId)).build())
    }

    private fun sendMetadata() {
        val metadata = ITextDisplayMetadata(
            brightness = Display.Brightness(15, 15),
            billboard = Display.Billboard.CENTER,
            textComponent = Component.text("Hello, world!")
        ).build()
        packetHandler.sendPacket(player, IPacketSetEntityMetadata(entityId, metadata).build())
    }

}