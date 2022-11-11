package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.providers.DefaultImportersProvider

import scala.meta.{Import, Pkg}

trait PkgTraverser extends ScalaTreeTraverser[Pkg]

private[traversers] class PkgTraverserImpl(termRefTraverser: => TermRefTraverser,
                                           pkgStatListTraverser: => PkgStatListTraverser,
                                           defaultImportersProvider: DefaultImportersProvider)
                                          (implicit javaWriter: JavaWriter) extends PkgTraverser {

  import javaWriter._

  override def traverse(pkg: Pkg): Unit = {
    write("package ")
    termRefTraverser.traverse(pkg.ref)
    writeStatementEnd()
    writeLine()

    val enrichedPkgStats = Import(defaultImportersProvider.provide()) +: pkg.stats

    pkgStatListTraverser.traverse(enrichedPkgStats)
  }
}
