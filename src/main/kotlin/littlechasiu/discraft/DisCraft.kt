package littlechasiu.discraft

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.fabricmc.loader.api.FabricLoader
import java.nio.charset.StandardCharsets
import java.nio.file.Files

object DisCraft {
  //  val MOD_ID: String = "discraft"
  private val GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create()
  private var dc: Discord? = null

  data class ConfigData(
    @SerializedName("token")
    val token: String,
    @SerializedName("webhook")
    val webhookUrl: String,

    @SerializedName("server")
    val guildId: Long,
    @SerializedName("chat_channel")
    val chatChannelId: Long,
    @SerializedName("console_channel")
    val consoleChannelId: Long,
    @SerializedName("linked_role")
    val linkedRoleId: Long?,

    @SerializedName("server_started")
    val serverStartedMessage: String?,
    @SerializedName("server_stopped")
    val serverStoppedMessage: String?,

    @SerializedName("player_join")
    val playerJoinMessage: String?,
    @SerializedName("player_leave")
    val playerLeaveMessage: String?,
    @SerializedName("player_die")
    val playerDeathMessage: String?,

    @SerializedName("advancement_goal")
    val goalAchievedMessage: String?,
    @SerializedName("advancement_task")
    val taskAchievedMessage: String?,
    @SerializedName("advancement_challenge")
    val challengeAchievedMessage: String?,

    @SerializedName("avatar_url")
    val avatarUrl: String,

    @SerializedName("server_name")
    val serverName: String,

    @SerializedName("discord_to_mc")
    val discordToMinecraftFormat: String,
    @SerializedName("discord_to_mc_reply")
    val discordToMinecraftReplyFormat: String?,
  )

  private var _config: ConfigData? = null

  private fun clearConfig() {
    _config = null
  }


fun loadConfig() {
    DisCraftMod.LOGGER.info("Loading config file")

    try {
      val configFile =
        FabricLoader.getInstance().configDir.resolve("discraft.json")

      if (Files.exists(configFile)) {
        val json: String = Files.readString(configFile, StandardCharsets.UTF_8)
        _config = GSON.fromJson(json, ConfigData::class.java)
        DisCraftMod.LOGGER.info("[DC] CONFIG: $_config")

        _config?.token ?: run {
          DisCraftMod.LOGGER.error("Discord token not specified")
          throw RuntimeException()
        }
      } else {
        DisCraftMod.LOGGER.error("Config file does not exist")
        clearConfig()
      }
    } catch (e: Exception) {
      DisCraftMod.LOGGER.error("Error loading config!")
      e.printStackTrace()

      clearConfig()
      throw e
    }
  }

  fun initDiscord() {
    DisCraftMod.LOGGER.info("Initializing Discord")

    dc = Discord(
      JDABuilder.createDefault(_config!!.token)
        .enableIntents(
          GatewayIntent.GUILD_MESSAGES,
          GatewayIntent.GUILD_WEBHOOKS,
          GatewayIntent.GUILD_MEMBERS,
          GatewayIntent.MESSAGE_CONTENT)
        .setStatus(OnlineStatus.ONLINE)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .addEventListeners(DiscordListener())
        .build(),
      _config!!)
  }

  fun initHandler() {
    DisCraftMod.LOGGER.info("Initializing handlers")

    Handler.redirectEvents()
    Handler.registerHandlers(dc!!, _config!!)
  }

  fun reload() {
    try {
      clearConfig()
      loadConfig()
      initDiscord()
      initHandler()
    } catch (e: Exception) {
      DisCraftMod.LOGGER.error("DisCraft failed to reload")
    }
  }
}
