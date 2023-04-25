package littlechasiu.discraft

import littlechasiu.discraft.PlayerEvents.AdvancementMade
import littlechasiu.discraft.PlayerEvents.Connected
import littlechasiu.discraft.PlayerEvents.Death
import littlechasiu.discraft.PlayerEvents.Disconnected
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.advancements.Advancement
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object PlayerEvents {
  fun interface Connected {
    fun onConnected(player: ServerPlayer, server: MinecraftServer)
  }

  fun interface Disconnected {
    fun onDisconnected(player: ServerPlayer, server: MinecraftServer)
  }

  fun interface AdvancementMade {
    fun onAdvancementMade(player: ServerPlayer, advancement: Advancement)
  }

  fun interface Death {
    fun onPlayerDeath(player: ServerPlayer, deathMessage: Component)
  }

  val CONNECTED: Event<Connected> =
    EventFactory.createArrayBacked(Connected::class.java) { listeners ->
      Connected { player, server ->
        listeners.forEach { it.onConnected(player, server) }
      }
    }

  val DISCONNECTED: Event<Disconnected> =
    EventFactory.createArrayBacked(Disconnected::class.java) { listeners ->
      Disconnected { player, server ->
        listeners.forEach { it.onDisconnected(player, server) }
      }
    }

  @JvmField
  val ADVANCEMENT_MADE: Event<AdvancementMade> =
    EventFactory.createArrayBacked(AdvancementMade::class.java) { listeners ->
      AdvancementMade { player, advancement ->
        listeners.forEach { it.onAdvancementMade(player, advancement) }
      }
    }

  @JvmField
  val PLAYER_DIED: Event<Death> =
    EventFactory.createArrayBacked(Death::class.java) { listeners ->
      Death { player, deathMessage ->
        listeners.forEach { it.onPlayerDeath(player, deathMessage) }
      }
    }
}
