package io.github.effiban.scala2java.core.unqualifiers

import scala.meta.{Pkg, Source}

trait SourceUnqualifier {
  def unqualify(source: Source): Source
}

private[unqualifiers] class SourceUnqualifierImpl(pkgUnqualifier: PkgUnqualifier) extends SourceUnqualifier {

  override def unqualify(source: Source): Source = {
    source.transform {
      case pkg: Pkg => pkgUnqualifier.unqualify(pkg)
    } match {
      case transformedSource: Source => transformedSource
      case other => throw new IllegalStateException(s"The transformed source should also be a Source but it is: $other")
    }
  }
}

object SourceUnqualifier extends SourceUnqualifierImpl(Unqualifiers.pkgUnqualifier)
