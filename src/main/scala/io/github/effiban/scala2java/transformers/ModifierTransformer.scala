package io.github.effiban.scala2java.transformers

import io.github.effiban.scala2java.entities.JavaModifier

import scala.meta.Mod.{Abstract, Final, Private, Protected, Sealed}
import scala.meta.{Mod, Name}

trait ModifierTransformer {

  def transform(scalaMod: Mod): Option[JavaModifier]
}

object ModifierTransformer extends ModifierTransformer {

  override def transform(scalaMod: Mod): Option[JavaModifier] = {
    scalaMod match {
      // The other private and protected flavors are package-level, so we will approximate them with the Java default and return None
      case Private(Name.Anonymous()) => Some(JavaModifier.Private)
      case Protected(Name.Anonymous()) => Some(JavaModifier.Protected)
      case _: Sealed => Some(JavaModifier.Sealed)
      case _: Abstract => Some(JavaModifier.Abstract)
      case _: Final => Some(JavaModifier.Final)
      case _ => None
    }
  }
}
