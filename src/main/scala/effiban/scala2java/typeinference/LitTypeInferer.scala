package effiban.scala2java.typeinference

import scala.meta.{Lit, Type}

trait LitTypeInferer extends TypeInferer[Lit]

object LitTypeInferer extends LitTypeInferer {

  override def infer(lit: Lit): Option[Type] = {
    val maybeTypeValue = lit match {
      case _: Lit.Boolean => Some("Boolean")
      case _: Lit.Byte => Some("Byte")
      case _: Lit.Short => Some("Short")
      case _: Lit.Int => Some("Int")
      case _: Lit.Long => Some("Long")
      case _: Lit.Float => Some("Float")
      case _: Lit.Double => Some("Double")
      case _: Lit.Char => Some("Char")
      case _: Lit.String => Some("String")
      case _: Lit.Unit => Some("Unit")
      case _ => None
    }
    maybeTypeValue.map(Type.Name(_))
  }
}
