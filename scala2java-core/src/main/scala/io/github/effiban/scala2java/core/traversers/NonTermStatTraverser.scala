package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.renderers.ImportRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat}

trait NonTermStatTraverser {
  def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit
}

private[traversers] class NonTermStatTraverserImpl(importTraverser: => ImportTraverser,
                                                   importRenderer: => ImportRenderer,
                                                   pkgTraverser: => PkgTraverser,
                                                   defnTraverser: => DefnTraverser,
                                                   declTraverser: => DeclTraverser)
                                                  (implicit javaWriter: JavaWriter) extends NonTermStatTraverser {

  import javaWriter._

  override def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit = stat match {
    case `import`: Import =>
      importTraverser.traverse(`import`)
        .foreach(traversedImport => importRenderer.render(traversedImport, statContext))
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn, statContext)
    case decl: Decl => declTraverser.traverse(decl, statContext)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
