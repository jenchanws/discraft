package littlechasiu.discraft

import littlechasiu.discraft.DiscordEvents.ChatMessage
import littlechasiu.discraft.DiscordEvents.SlashCommand
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object DiscordEvents {
  fun interface SlashCommand {
    fun onSlashCommand(command: String, event: SlashCommandInteractionEvent)
  }

  fun interface ChatMessage {
    fun onMessageReceived(user: User, message: Message)
  }

  val SLASH_COMMAND: Event<SlashCommand> =
    EventFactory.createArrayBacked(SlashCommand::class.java) { listeners ->
      SlashCommand { command: String, event: SlashCommandInteractionEvent ->
        listeners.forEach { it.onSlashCommand(command, event) }
      }
    }

  val CHAT_MESSAGE: Event<ChatMessage> =
    EventFactory.createArrayBacked(ChatMessage::class.java) { listeners ->
      ChatMessage { user, message ->
        listeners.forEach { it.onMessageReceived(user, message) }
      }
    }
}
