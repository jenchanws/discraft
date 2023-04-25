package littlechasiu.discraft

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.AllowedMentions
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders
import eu.pb4.placeholders.api.TextParserUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.awt.Color

class Discord(private val jda: JDA, private val config: DisCraft.ConfigData) {
  private var webhook: WebhookClient = WebhookClient.withUrl(config.webhookUrl)
  private var server: Guild
  private var chatChannel: MessageChannel
  private var consoleChannel: MessageChannel
  private var linkedRole: Role?

  init {
    jda.awaitReady()

    server = jda.getGuildById(config.guildId) ?: run {
      DisCraftMod.LOGGER.error("[DC] Unable to find server with ID ${config.guildId}")
      throw RuntimeException("Unable to find Discord server")
    }

    chatChannel = server.getTextChannelById(config.chatChannelId) ?: run {
      DisCraftMod.LOGGER.error(
        "[DC] Unable to find channel with ID ${config.chatChannelId}, or it is not a text channel")
      throw RuntimeException("Unable to find Discord chat channel")
    }

    consoleChannel = server.getTextChannelById(config.consoleChannelId) ?: run {
      DisCraftMod.LOGGER.error(
        "[DC] Unable to find channel with ID ${config.consoleChannelId}, or it is not a text channel")
      throw RuntimeException("Unable to find Discord console channel")
    }

    linkedRole = config.linkedRoleId?.let {
      server.getRoleById(config.linkedRoleId) ?: run {
        DisCraftMod.LOGGER.error("[DC] Unable to find role with ID ${config.linkedRoleId}")
        null
      }
    }

    jda.presence.activity = Activity.playing(config.serverName)

    server.loadMembers()
  }

  fun shutdown() {
    jda.presence.activity = null
    jda.shutdown()
    webhook.close()
  }

  fun nameForUser(id: String): String {
    return server.getMemberById(id)!!.effectiveName
  }

  fun userWithName(name: String): Member? {
    val lst = server.getMembersByEffectiveName(name, true)
    if (lst.size != 1) {
      return null
    }

    return lst[0]
  }

  private fun sendToChatChannel(msg: MessageCreateData) {
    try {
      chatChannel.sendMessage(msg).queue()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun sendEmbedMessage(message: String, color: Color) {
    sendToChatChannel(
      MessageCreateBuilder().setEmbeds(
        EmbedBuilder()
        .setTitle(message)
        .setColor(color)
        .build()
      ).build())
  }

  private fun sendEmbedMessage(player: ServerPlayer, message: String, color: Color) {
    val avatarUrl = String.format(config.avatarUrl,
                                     player.stringUUID)

    sendToChatChannel(
      MessageCreateBuilder().setEmbeds(
        EmbedBuilder()
        .setAuthor(message, null, avatarUrl)
        .setColor(color)
        .build()
      ).build())
  }

  fun sendEmbedMessage(player: ServerPlayer, message: String,
                               submessage: String, color: Color) {
    val avatarUrl = String.format(config.avatarUrl,
                                     player.stringUUID)

    sendToChatChannel(
      MessageCreateBuilder().setEmbeds(
        EmbedBuilder()
        .setAuthor(message, null, avatarUrl)
        .setDescription(submessage)
        .setColor(color)
        .build()
      ).build())
  }

  private fun sendEmbedMessage(text: Component, ctx: PlaceholderContext,
                              color: Color) {
    sendEmbedMessage(
      Placeholders.parseText(text, ctx).string,
      color)
  }

  fun sendEmbedMessage(text: String, ctx: PlaceholderContext,
                              color: Color) {
    sendEmbedMessage(TextParserUtils.formatText(text), ctx, color)
  }

  fun sendEmbedMessage(player: ServerPlayer, text: Component,
                              ctx: PlaceholderContext, color: Color) {
    sendEmbedMessage(player, Placeholders.parseText(text, ctx).string, color)
  }

  fun sendEmbedMessage(player: ServerPlayer, text: String,
                              ctx: PlaceholderContext, color: Color) {
    sendEmbedMessage(player, TextParserUtils.formatText(text), ctx, color)
  }

  fun sendPlayerMessage(sender: ServerPlayer, name: Component, message: Component) {
    webhook.send(WebhookMessageBuilder()
                  .setAvatarUrl(
                    String.format(config.avatarUrl, sender.stringUUID))
                  .setUsername(name.string)
                  .setContent(message.string)
                  .setAllowedMentions(AllowedMentions()
                                      .withParseEveryone(false)
                                      .withParseRoles(false)
                                      .withParseUsers(true)).build())
  }
}
