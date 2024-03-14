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


class Indicator(
    private val packetHandler: PacketHandler,
    private val player: Player,
    private val vector: Vector
) :
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

    // Entity metadata
    private var prevMetadata = ITextDisplayMetadata()
    var metadata = ITextDisplayMetadata(
        posInterpolation = 1,
        brightness = Display.Brightness(15, 15),
        billboard = Display.Billboard.CENTER,
        textComponent = Component.text("-10.5"),
        backgroundColor = Color.fromARGB(0, 0, 0, 0),
        textOpacity = 0.toByte()
    )

    // show(spawn) entity on client side
    fun show() {
        packetHandler.sendPacket(
            player,
            IPacketSpawnEntity(entityId, UUID.randomUUID(), EntityType.TEXT_DISPLAY, this).build()
        )
        updateMetadata()
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
            (now <= 20) -> TODO("Not yet implemented")
            (now < 276) -> {
                metadata.textOpacity = ITextDisplayMetadata.alphaToByte(now - 20)
                metadata.textComponent = Component.text("${metadata.textOpacity!!.toByte()}")
            }

            else -> _alive = false
        }
        updatePosition()
        updateMetadata()
    }

    // hide(remove) entity on client side
    fun hide() {
        packetHandler.sendPacket(player, IPacketDestroyEntity(listOf(entityId)).build())
    }

    // update metadata and send packet to client
    private fun updateMetadata() {
        if (prevMetadata != metadata) {
            packetHandler.sendPacket(
                player,
                IPacketSetEntityMetadata(entityId, metadata.build()).build()
            )
            prevMetadata = metadata.copy()
        }
    }

    // update position and send packet to client
    private fun updatePosition() {
        if (prevPos != this) {
            packetHandler.sendPacket(
                player,
                IPacketUpdateEntityPosition(entityId, prevPos, this, false).build()
            )
            prevPos = this.vector.clone()
        }
    }

}