# DisCraft

![Available for Fabric](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/522a26e2c91cbafab79ddbbc6d942f1720249a8c/fabric.svg)
[![Requires Fabric API](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/1fc7a8dfba76460bedba7fa0b5516b917815e39c/fabric-api.svg)](https://modrinth.com/mod/fabric-api)
[![Requires Fabric Kotlin](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/1fc7a8dfba76460bedba7fa0b5516b917815e39c/fabric-kotlin.svg)](https://modrinth.com/mod/fabric-language-kotlin)

[![Available on GitHub](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/0dbefc2fcbec362d14f1689acb807183ceffdbe1/github.svg)](https://github.com/jenchanws/discraft)

<!--
[![Available on Modrinth](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/0dbefc2fcbec362d14f1689acb807183ceffdbe1/modrinth.svg)](https://modrinth.com/mod/discraft)
-->

[![Find me on Discord](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/0dbefc2fcbec362d14f1689acb807183ceffdbe1/discord.svg)](https://smp.littlechasiu.com/discord)

A Discord chat bridge for Fabric, using the [Placeholder API](https://placeholders.pb4.eu). Send messages in-game and have them relayed to Discord, and vice-versa. Also supports Discord mentions, emotes, and attachments.

### Configuration

DisCraft can be configured by editing `discraft.json` in your server's config directory.

```json
{
  "token": "DISCORD_BOT_TOKEN",
  "webhook": "DISCORD_WEBHOOK_URL",
  "server": 241543903,
  "chat_channel": 241543903,

  // Formats for messages displayed after certain server events.
  // Supports Placeholder API's Simplified Text Format.
  // All non-message events are displayed as embeds on Discord.

  "server_started": ":white_check_mark: Server started",
  "server_stopped": ":octagonal_sign: Server stopped",
  "player_join": "%player:name% joined the game",
  "player_leave": "%player:name% left the game",

  "advancement_goal": "%player:name% has reached the goal %disc:advancement%!",
  "advancement_task": "%player:name% has made the advancement %disc:advancement%!",
  "advancement_challenge": "%player:name% has completed the challenge %disc:advancement%!",

  "discord_to_mc": "<aqua>[DC] %disc:nickname%</aqua>: %disc:message%",
  "discord_to_mc_reply": "<aqua>[DC] %disc:nickname%</aqua> <gray>(reply to</gray> %disc:reply_to%<gray>)</gray>: %disc:message%",

  // URL to obtain the player's icon from
  "avatar_url": "https://crafatar.com/avatars/%s?overlay"
}
```
