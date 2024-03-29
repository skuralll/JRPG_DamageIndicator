package com.skuralll.jrpg_damageindicator.indicator

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import kotlin.math.floor

enum class DamageType(
    private val icon: String,
    private val iconColor: TextColor,
    private val damageColor: TextColor
) {
    DIRECT_NORMAL("", TextColor.color(255, 255, 255), TextColor.color(255, 255, 255)),
    DIRECT_CRITICAL("", TextColor.color(255, 170, 0), TextColor.color(255, 170, 0)),
    ARROW_NORMAL("\uD83C\uDFF9", TextColor.color(255, 255, 255), TextColor.color(255, 255, 255)),
    ARROW_CRITICAL("\uD83C\uDFF9", TextColor.color(255, 170, 0), TextColor.color(255, 170, 0)),
    TRIDENT_NORMAL("\uD83E\uDDF9", TextColor.color(255, 255, 255), TextColor.color(255, 255, 255)),
    FIRED_TICK("\uD83D\uDD25", TextColor.color(255, 85, 85), TextColor.color(255, 85, 85)),
    POISON_TICK("\uD83E\uDEE7", TextColor.color(0, 170, 0), TextColor.color(0, 170, 0)),
    HARMED_DAMAGE("\uD83D\uDCA5", TextColor.color(170, 0, 0), TextColor.color(170, 0, 0)),
    EXPLOSION(
        "\uD83D\uDCA3",
        TextColor.color(170, 170, 170),
        TextColor.color(170, 170, 170)
    );

    // generate text component for indicator
    fun toTextComponent(damage: Double): Component {
        return Component.text(icon).color(iconColor)
            .append(Component.text("⋖ ${floor(damage * 10.0) / 10.0} ⋗").color(damageColor))
    }
}