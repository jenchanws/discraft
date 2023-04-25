package littlechasiu.discraft

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class DiscordListener : ListenerAdapter() {
  override fun onMessageReceived(event: MessageReceivedEvent) {
    if (event.author.isBot) {
      return
    }

    DiscordEvents.CHAT_MESSAGE.invoker()
      .onMessageReceived(event.author, event.message)
  }

  override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
    DiscordEvents.SLASH_COMMAND.invoker().onSlashCommand(event.name, event)
  }
}
