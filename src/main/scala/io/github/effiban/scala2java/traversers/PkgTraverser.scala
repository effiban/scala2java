package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.generators.JavaImportersProvider
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.{Import, Pkg}

trait PkgTraverser extends ScalaTreeTraverser[Pkg]

private[traversers] class PkgTraverserImpl(termRefTraverser: => TermRefTraverser,
                                           pkgStatListTraverser: => PkgStatListTraverser,
                                           javaImportersProvider: JavaImportersProvider)
                                          (implicit javaWriter: JavaWriter) extends PkgTraverser {

  import javaWriter._

  override def traverse(pkg: Pkg): Unit = {
    write("package ")
    termRefTraverser.traverse(pkg.ref)
    writeStatementEnd()
    writeLine()

    val enrichedPkgStats = Import(javaImportersProvider.provide()) +: pkg.stats

    pkgStatListTraverser.traverse(enrichedPkgStats)
  }
}
