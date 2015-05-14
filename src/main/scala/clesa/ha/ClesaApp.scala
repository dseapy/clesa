package clesa.ha

import akka.actor.{Props, ActorSystem}
import clesa.ha.actors.{BroadcastActor, LinuxInputActor, HueActor}
import clesa.ha.utils.ConfigUtils

import scala.collection.JavaConversions._

object ClesaApp
  extends App {
  val primaryConfigPath = args.headOption.getOrElse(ConfigUtils.primaryConfigDefaultPath)
  val config = ConfigUtils.createConfigFromPathWithDefaultConfig(primaryConfigPath)
  val system = ActorSystem("ClesaActorSystem")
  val broadcastActor = system.actorOf(Props(classOf[BroadcastActor]))
  val hueConfigs = config.getConfigList("clesa.ha.actors.hue")
  val linuxInputConfigs = config.getConfigList("clesa.ha.actors.linux-input-event")
  val hueActors = hueConfigs.map(hConfig => system.actorOf(Props(classOf[HueActor], broadcastActor, hConfig)))
  val linuxInputActors = linuxInputConfigs.map(liConfig => system.actorOf(Props(classOf[LinuxInputActor], broadcastActor, liConfig)))
  for(ha <- hueActors ++ linuxInputActors) broadcastActor ! ha
}
