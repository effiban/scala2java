package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, ImportRenderContext}
import io.github.effiban.scala2java.core.renderers.{DeclRenderer, ImportRenderer, StatTermRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait StatTraverser {
  def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit
}

private[traversers] class StatTraverserImpl(statTermTraverser: => StatTermTraverser,
                                            statTermRenderer: => StatTermRenderer,
                                            importTraverser: => ImportTraverser,
                                            importRenderer: => ImportRenderer,
                                            pkgTraverser: => PkgTraverser,
                                            defnTraverser: => DefnTraverser,
                                            declTraverser: => DeclTraverser,
                                            declRenderer: => DeclRenderer)
                                           (implicit javaWriter: JavaWriter) extends StatTraverser {

  import javaWriter._

  override def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit = stat match {
    case term: Term =>
      val traversedStat = statTermTraverser.traverse(term)
      statTermRenderer.render(traversedStat)
    case `import`: Import =>
      importTraverser.traverse(`import`).foreach(traversedImport =>
        statContext.javaScope match {
          case JavaScope.Package => importRenderer.render(traversedImport)
          case _ => writeComment(traversedImport.toString())
        })
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn, statContext)
    case decl: Decl =>
      val traversalResult = declTraverser.traverse(decl, statContext)
      declRenderer.render(traversalResult.tree, DeclRenderContext(traversalResult.javaModifiers))
    case other => writeComment(s"UNSUPPORTED: $other")
  }

  private def toImportRenderContext(statContext: StatContext): ImportRenderContext = {
    ImportRenderContext(asComment = statContext.javaScope != JavaScope.Package)
  }
}
