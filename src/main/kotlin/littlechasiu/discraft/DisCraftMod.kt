package littlechasiu.discraft

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object DisCraftMod {
  val LOGGER: Logger = LogManager.getLogger("DisCraft")
}

fun init() {
  try {
    DisCraft.loadConfig()
    DisCraft.initDiscord()
    DisCraft.initHandler()
  } catch (e: Exception) {
    DisCraftMod.LOGGER.error("DisCraft failed to load")
    e.printStackTrace()
    return
  }
}
