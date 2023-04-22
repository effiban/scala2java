package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

import scala.meta.{Import, Pkg}

trait PkgTraverser extends ScalaTreeTraverser[Pkg]

private[traversers] class PkgTraverserImpl(termRefTraverser: => DefaultTermRefTraverser,
                                           pkgStatListTraverser: => PkgStatListTraverser,
                                           additionalImportersProvider: AdditionalImportersProvider)
                                          (implicit javaWriter: JavaWriter) extends PkgTraverser {

  import javaWriter._

  override def traverse(pkg: Pkg): Unit = {
    write("package ")
    termRefTraverser.traverse(pkg.ref)
    writeStatementEnd()
    writeLine()

    val enrichedPkgStats = Import(additionalImportersProvider.provide()) +: pkg.stats

    pkgStatListTraverser.traverse(enrichedPkgStats)
  }
}
