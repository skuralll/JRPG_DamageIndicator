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
import com.skuralll.jrpg_damageindicator.packet.packets.IPacketUpdateEntityPosition
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class Indicator(private val packetHandler: PacketHandler, private val player: Player, private val vector: Vector) :
    Vector(vector.x, vector.y, vector.z) {

    // previous position
    private var prevPos = clone()

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
        // kill if player is offline
        if (!player.isOnline) {
            _alive = false
            return;
        }
        val now = tick.incrementAndGet()
        // update process
        when {
            (now > 40) -> _alive = false
            else -> {
                y += 0.1;
            }
        }
        updatePosition()
    }

    // hide(remove) entity on client side
    fun hide() {
        packetHandler.sendPacket(player, IPacketDestroyEntity(listOf(entityId)).build())
    }

    // send metadata packet to client
    private fun sendMetadata() {
        val metadata = ITextDisplayMetadata(
            posInterpolation = 1,
            brightness = Display.Brightness(15, 15),
            billboard = Display.Billboard.CENTER,
            textComponent = Component.text("Hello, world!"),
            backgroundColor = Color.fromARGB(255, 255, 0, 0),
            textOpacity = 127.toByte()
        ).build()
        packetHandler.sendPacket(player, IPacketSetEntityMetadata(entityId, metadata).build())
    }

    // send move packet to client
    private fun updatePosition() {
        if (prevPos == this) return
        packetHandler.sendPacket(player, IPacketUpdateEntityPosition(entityId, prevPos, this, false).build())
        prevPos = this.vector.clone()
    }

}