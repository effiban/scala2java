package io.github.effiban.scala2java.classifiers

import scala.meta.Mod

trait ModsClassifier {

  def arePublic(mods: List[Mod]): Boolean

  def includeSealed(mods: List[Mod]): Boolean

  def includeFinal(mods: List[Mod]): Boolean
}

object ModsClassifier extends ModsClassifier {

  override def arePublic(mods: List[Mod]): Boolean = {
    mods.collect {
      case m: Mod.Private => m
      case m: Mod.Protected => m
    }.isEmpty
  }

  override def includeSealed(mods: List[Mod]): Boolean = {
    mods.collect { case m: Mod.Sealed => m}.nonEmpty
  }

  override def includeFinal(mods: List[Mod]): Boolean = {
    mods.collect { case m: Mod.Final => m }.nonEmpty
  }
}
