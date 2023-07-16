package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.renderers.contexts.ImportRenderContext
import io.github.effiban.scala2java.core.renderers.{ImportRenderer, StatTermRenderer}
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
                                            declTraverser: => DeclTraverser)
                                           (implicit javaWriter: JavaWriter) extends StatTraverser {

  import javaWriter._

  override def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit = stat match {
    case term: Term =>
      val traversedStat = statTermTraverser.traverse(term)
      statTermRenderer.render(traversedStat)
    case `import`: Import =>
      importTraverser.traverse(`import`)
        .foreach(traversedImport => importRenderer.render(traversedImport, toImportRenderContext(statContext)))
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn, statContext)
    case decl: Decl => declTraverser.traverse(decl, statContext)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

  private def toImportRenderContext(statContext: StatContext): ImportRenderContext = {
    ImportRenderContext(asComment = statContext.javaScope != JavaScope.Package)
  }
}
