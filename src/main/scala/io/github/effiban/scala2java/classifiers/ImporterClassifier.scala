package io.github.effiban.scala2java.classifiers

import scala.annotation.tailrec
import scala.meta.Term.Select
import scala.meta.{Importer, Term}

trait ImporterClassifier {

  def isScala(importer: Importer): Boolean
}

object ImporterClassifier extends ImporterClassifier {

  override def isScala(importer: Importer): Boolean = isScala(importer.ref)

  @tailrec
  private def isScala(term: Term): Boolean = term match {
    case Term.Name("scala") | Select(_, Term.Name("scala")) => true
    case Select(qual, _) => isScala(qual)
    case _ => false
  }
}
