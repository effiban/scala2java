package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.qualifiers.Qualifiers.pkgQualifier

import scala.meta.{Pkg, Source}

trait SourceQualifier {
  def qualify(source: Source): Source
}

private[qualifiers] class SourceQualifierImpl(pkgQualifier: PkgQualifier) extends SourceQualifier {

  override def qualify(source: Source): Source =
    source.transform {
      case pkg: Pkg => pkgQualifier.qualify(pkg)
    } match {
      case transformedSource: Source => transformedSource
      case other => throw new IllegalStateException(s"The transformed source should also be a Source but it is: $other")
    }
}

object SourceQualifier extends SourceQualifierImpl(pkgQualifier)