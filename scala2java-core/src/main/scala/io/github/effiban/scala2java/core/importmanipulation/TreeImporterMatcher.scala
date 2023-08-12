package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Tree, Type}

trait TreeImporterMatcher {
  def matches(tree: Tree, importer: Importer): Boolean
}

private[importmanipulation] class TreeImporterMatcherImpl(typeSelectImporterMatcher: TypeSelectImporterMatcher) extends TreeImporterMatcher {

  override def matches(tree: Tree, importer: Importer): Boolean = {
    tree match {
      case typeSelect: Type.Select => typeSelectImporterMatcher.matches(typeSelect, importer)
      // TODO handle Type.Project
      // TODO handle Term.Select
      case _ => false
    }
  }
}

object TreeImporterMatcher extends TreeImporterMatcherImpl(TypeSelectImporterMatcher)
