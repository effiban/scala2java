package effiban.scala2java.transformers

import effiban.scala2java.entities.JavaModifier

import scala.meta.Mod
import scala.meta.Mod.{Abstract, Final, Private, Protected, Sealed}

trait ScalaToJavaModifierTransformer {

  def transform(scalaMod: Mod): Option[JavaModifier]
}

object ScalaToJavaModifierTransformer extends ScalaToJavaModifierTransformer {

  override def transform(scalaMod: Mod): Option[JavaModifier] = {
    scalaMod match {
      case _: Private => Some(JavaModifier.Private)
      case _: Protected => Some(JavaModifier.Protected)
      case _: Sealed => Some(JavaModifier.Sealed)
      case _: Abstract => Some(JavaModifier.Abstract)
      case _: Final => Some(JavaModifier.Final)
      case _ => None
    }
  }
}
