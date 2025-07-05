package io.github.effiban.scala2java.core.extractors

import scala.annotation.tailrec
import scala.meta.{Init, Type}

trait TypeRefExtractor {
  def extract(tpe: Type): Option[Type.Ref]
}

object TypeRefExtractor extends TypeRefExtractor {

  @tailrec
  def extract(tpe: Type): Option[Type.Ref] = tpe match {
    case typeRef: Type.Ref => Some(typeRef)
    case typeRepeated: Type.Repeated => extract(typeRepeated.tpe)
    case typeApply: Type.Apply => extract(typeApply.tpe)
    case _ => None
  }
}
