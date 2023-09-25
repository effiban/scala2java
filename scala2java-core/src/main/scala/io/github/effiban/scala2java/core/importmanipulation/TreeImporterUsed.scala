package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Member, Term, Traverser, Tree, Type}

trait TreeImporterUsed extends ((Tree, Importer) => Boolean)

private[importmanipulation] class TreeImporterUsedImpl(termNameImporterMatcher: TermNameImporterMatcher,
                                                       typeNameImporterMatcher: TypeNameImporterMatcher) extends TreeImporterUsed {

  override def apply(tree: Tree, importer: Importer): Boolean = {
    val inUseChecker = new InUseChecker(importer)
    inUseChecker(tree)
    inUseChecker.inUse
  }

  private class InUseChecker(importer: Importer) extends Traverser {

    var inUse = false

    override def apply(tree: Tree): Unit = tree match {
      case _: Term.Select => //TODO support match by qualifier prefix
      case termName: Term.Name => checkTermName(termName)
      case _: Type.Select => //TODO support match by qualifier prefix
      case _: Type.Project => //TODO support match by qualifier prefix
      case typeName: Type.Name => checkTypeName(typeName)
      case aTree => super.apply(aTree)
    }

    private def checkTermName(termName: Term.Name): Unit = {
      termName.parent match {
        // Don't check term name definitions
        case Some(_: Member.Term | _: Term.Param) =>
        case _ => inUse ||= termNameImporterMatcher.findMatch(termName, importer).nonEmpty
      }
    }

    private def checkTypeName(typeName: Type.Name): Unit = {
      typeName.parent match {
        // Don't check type name definitions
        case Some(_: Member.Type | _: Type.Param) =>
        case _ => inUse ||= typeNameImporterMatcher.findMatch(typeName, importer).nonEmpty
      }
    }
  }
}

object TreeImporterUsed extends TreeImporterUsedImpl(TermNameImporterMatcher, TypeNameImporterMatcher)
