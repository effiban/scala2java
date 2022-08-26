package effiban.scala2java.transformers

import scala.meta.Mod
import scala.meta.Mod.{Abstract, Final, Private, Protected, Sealed}

trait ScalaToJavaModifierTransformer {

  def transform(scalaMod: Mod): Option[String]
}

object ScalaToJavaModifierTransformer extends ScalaToJavaModifierTransformer {

  override def transform(scalaMod: Mod): Option[String] = {
    scalaMod match {
      case _: Private => Some("private")
      case _: Protected => Some("protected")
      case _: Abstract => Some("abstract")
      case _: Final => Some("final")
      case _: Sealed => Some("sealed")
      case _ => None
    }
  }
}
