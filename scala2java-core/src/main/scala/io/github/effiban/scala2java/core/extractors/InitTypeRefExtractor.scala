package io.github.effiban.scala2java.core.extractors

import scala.annotation.tailrec
import scala.meta.{Init, Type}

object InitTypeRefExtractor {

  def extract(init: Init): Option[Type.Ref] = extract(init.tpe)

  @tailrec
  private def extract(tpe: Type): Option[Type.Ref] = tpe match {
    case typeRef: Type.Ref => Some(typeRef)
    case typeApply: Type.Apply => extract(typeApply.tpe)
    case _ => None
  }
}
