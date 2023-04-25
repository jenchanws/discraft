package littlechasiu.discraft

import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders
import eu.pb4.placeholders.api.TextParserUtils
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.advancements.FrameType
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import java.awt.Color

object Handler {
  fun redirectEvents() {
    ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
      PlayerEvents.CONNECTED.invoker().onConnected(handler.player, server)
    }
    ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
      PlayerEvents.DISCONNECTED.invoker().onDisconnected(handler.player, server)
    }
  }

  private val startColor = Color.GREEN
  private val stopColor = Color.RED
  private val joinColor = Color.GREEN
  private val leaveColor = Color.RED
  private val dieColor = Color.BLACK
  private val advancementColor = Color.YELLOW

  private val minecraftMention = Regex("""@([^ ]+)""")
  private val discordMention = Regex("""\\<@(\d+)\\>""")
  private val discordEmote = Regex("""\\<a?:(.+?):\d+\\>""")
  private val brackets = Regex("""\[.+?] """)

  fun registerHandlers(dc: Discord, config: DisCraft.ConfigData) {
    ServerLifecycleEvents.SERVER_STARTED.register { server ->
      config.serverStartedMessage?.let { msg ->
        dc.sendEmbedMessage(msg, PlaceholderContext.of(server), startColor)
      }
    }

    ServerLifecycleEvents.SERVER_STOPPING.register { server ->
      config.serverStoppedMessage?.let { msg ->
        dc.sendEmbedMessage(msg, PlaceholderContext.of(server), stopColor)
      }
      dc.shutdown()
    }

    PlayerEvents.CONNECTED.register { player, _ ->
      config.playerJoinMessage?.let { msg ->
        dc.sendEmbedMessage(
          player,
          msg,
          PlaceholderContext.of(player),
          joinColor
        )
      }
    }

    PlayerEvents.DISCONNECTED.register { player, _ ->
      config.playerLeaveMessage?.let { msg ->
        dc.sendEmbedMessage(
          player,
          msg,
          PlaceholderContext.of(player),
          leaveColor
        )
      }
    }

    PlayerEvents.ADVANCEMENT_MADE.register { player, adv ->
      adv.display?.let { disp ->
        val titleFormat = when (disp.frame) {
          FrameType.GOAL -> config.goalAchievedMessage
          FrameType.TASK -> config.taskAchievedMessage
          FrameType.CHALLENGE -> config.challengeAchievedMessage
          else -> null
        }

        titleFormat?.let { format ->
          val title = disp.title
          val description = disp.description.string

          val titleText = Placeholders.parseText(
            Placeholders.parseText(
              TextParserUtils.formatText(format),
              PlaceholderContext.of(player)
            ),
            Placeholders.PLACEHOLDER_PATTERN,
            mutableMapOf("disc:advancement" to title)
          )

          dc.sendEmbedMessage(
            player, titleText.string, description, advancementColor
          )
        }
      }
    }

    PlayerEvents.PLAYER_DIED.register { player, msg ->
      config.playerDeathMessage?.let { _ ->
        var deathMsg = brackets.replace(msg.string, "")
        if (deathMsg.endsWith(".")) {
          deathMsg = deathMsg.substring(0, deathMsg.length - 1)
        }

        val deathText = Component.literal(deathMsg)

        dc.sendEmbedMessage(
          player, deathText, PlaceholderContext.of(player), dieColor
        )
      }
    }

    ServerMessageEvents.CHAT_MESSAGE.register { msg, sender, _ ->
      var text = msg.serverContent().string
      text = minecraftMention.replace(text) { mr ->
        dc.userWithName(mr.groups[1]!!.value)?.let { user ->
          user.nickname?.let { "<@!${user.id}>" } ?: run { "<@${user.id}>" }
        } ?: run { mr.groups[0]!!.value }
      }

      // TODO: Markdown parsing

      dc.sendPlayerMessage(sender, sender.name, Component.literal(text))
    }

    DiscordEvents.CHAT_MESSAGE.register { _, msg ->
      val channelId = msg.channel.idLong
      if (channelId == config.chatChannelId) {
        val server: MinecraftServer =
          FabricLoader.getInstance().gameInstance as MinecraftServer

        // TODO: Markdown parsing

        val username = msg.member!!.effectiveName
        var text = msg.contentRaw.replace("<", "\\<").replace(">", "\\>")

        text = discordMention.replace(text) { mr ->
          "<aqua>@${dc.nameForUser(mr.groups[1]!!.value)}</aqua>"
        }
        text = discordEmote.replace(text) { mr ->
          "<orange>:${mr.groups[1]!!.value}:</orange>"
        }

        var formatStr = config.discordToMinecraftFormat

        val placeholders = mutableMapOf(
          "disc:nickname" to Component.literal(username),
          "disc:message" to TextParserUtils.formatText(
            if (text == "") {
              "<dark_gray>(no text)</dark_gray>"
            } else {
              text
            }
          )
        )

        val replyTo = msg.referencedMessage
        if (replyTo?.member != null) {
          if (config.discordToMinecraftReplyFormat != null) {
            formatStr = config.discordToMinecraftReplyFormat
          }

          placeholders["disc:reply_to"] =
            Component.literal(replyTo.member!!.effectiveName)
        }

        var content = Placeholders.parseText(
          TextParserUtils.formatText(formatStr),
          Placeholders.PLACEHOLDER_PATTERN,
          placeholders
        ).copy()

        for (sticker in msg.stickers) {
          content.append(Component.literal(" "))
          content.append(
            TextParserUtils.formatText("<orange>:${sticker.name}:</orange>")
          )
        }

        for (att in msg.attachments) {
          content.append(Component.literal(" "))
          content.append(
            TextParserUtils.formatText(
              "<url:'${att.url}'><aqua><underline>[${att.fileName}]<r>"
            )
          )
        }

        server.playerList.broadcastSystemMessage(content, false)
      } else if (channelId == config.consoleChannelId) {
        // TODO: Console command execution
      }
    }

    DiscordEvents.SLASH_COMMAND.register { cmd, event -> null }
  }
}
