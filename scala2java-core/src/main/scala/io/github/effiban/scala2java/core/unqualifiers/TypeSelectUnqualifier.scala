package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.meta.{Type, XtensionQuasiquoteType}

trait TypeSelectUnqualifier {

  def unqualify(typeSelect: Type.Select, context: QualificationContext = QualificationContext()): Type.Ref
}

private[unqualifiers] class TypeSelectUnqualifierImpl(typeSelectImporterMatcher: TypeSelectImporterMatcher) extends TypeSelectUnqualifier {

  override def unqualify(typeSelect: Type.Select, context: QualificationContext = QualificationContext()): Type.Ref = {

    typeSelect match {
      case aTypeSelect@(t"scala.Array" | t"scala.Enumeration") => aTypeSelect
      case aTypeSelect =>
        // TODO support partial unqualification once support is added to the matcher
        context.importers.map(importer => typeSelectImporterMatcher.findMatch(aTypeSelect, importer))
          .collectFirst {
            case Some(_) => aTypeSelect.name
          }
          .getOrElse(aTypeSelect)
    }
  }
}

object TypeSelectUnqualifier extends TypeSelectUnqualifierImpl(TypeSelectImporterMatcher)
