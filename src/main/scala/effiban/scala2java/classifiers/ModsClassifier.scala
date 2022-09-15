package effiban.scala2java.classifiers

import scala.meta.Mod

trait ModsClassifier {

  def arePublic(mods: List[Mod]): Boolean
}

object ModsClassifier extends ModsClassifier {

  override def arePublic(mods: List[Mod]): Boolean = {
    mods.collect {
      case m: Mod.Private => m
      case m: Mod.Protected => m
    }.isEmpty
  }
}
