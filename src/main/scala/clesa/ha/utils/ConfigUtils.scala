package clesa.ha.utils

import java.io.File

import com.typesafe.config.{ConfigFactory, Config}

object ConfigUtils {

  val fallbackConfigResourceLocation = "fallback.conf"
  val primaryConfigDefaultPath = "/opt/clesa/primary.conf"

  def createConfigFromPath(configPath: String): Config = ConfigFactory.parseFile(new File(configPath))

  def createConfigFromPathWithDefaultConfig(configPath: String): Config =
    createConfigFromPath(configPath).withFallback(ConfigFactory.load(fallbackConfigResourceLocation))

  def createConfigFromDefaultPaths(): Config = createConfigFromPathWithDefaultConfig(primaryConfigDefaultPath)
}
