package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Term, XtensionQuasiquoteTerm}

//TODO - TEMPORARY until all core term names are supported
object StandaloneCoreTermNameQualifier extends CoreTermNameQualifier {

  def qualify(termName: Term.Name): Option[Term] = {
    termName match {
      case q"Nil" => Some(q"scala.Nil")
      case q"None" => Some(q"scala.None")
      case _ => None
    }
  }
}
