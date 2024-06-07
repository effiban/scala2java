package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermSelectClassifier
import io.github.effiban.scala2java.core.entities.TermNames.{Apply, Empty, JavaCompletedFuture, JavaFailedFuture, JavaFailure, JavaOf, JavaOfEntries, JavaOfNullable, JavaOfSupplier, JavaRange, JavaRangeClosed, JavaSuccess, JavaSupplyAsync, LowercaseLeft, LowercaseRight, ScalaFailed, ScalaInclusive, ScalaSuccessful}
import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.entities.{TreeElemSet, TreeKeyedMap}
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.QualifiedTermApply
import io.github.effiban.scala2java.spi.transformers.QualifiedTermApplyTransformer

import scala.meta.{Term, XtensionQuasiquoteTerm}

private[transformers] class CoreQualifiedTermApplyTransformer(termSelectClassifier: TermSelectClassifier)
  extends QualifiedTermApplyTransformer {

  private val ScalaToJavaQualifiedName = Map[Term.Select, Term.Select](
    Term.Select(ScalaRange, Apply) -> Term.Select(JavaIntStream, JavaRange),
    Term.Select(ScalaRange, ScalaInclusive) -> Term.Select(JavaIntStream, JavaRangeClosed),
    Term.Select(ScalaOption, Apply) -> Term.Select(JavaOptional, JavaOfNullable),
    Term.Select(ScalaOption, Empty) -> Term.Select(JavaOptional, Empty),
    Term.Select(ScalaSome, Apply) -> Term.Select(JavaOptional, JavaOf),

    Term.Select(ScalaRight, Apply) -> Term.Select(JavaEither, LowercaseRight),
    Term.Select(ScalaLeft, Apply) -> Term.Select(JavaEither, LowercaseLeft),

    Term.Select(ScalaTry, Apply) -> Term.Select(JavaTry, JavaOfSupplier),
    Term.Select(ScalaSuccess, Apply) -> Term.Select(JavaTry, JavaSuccess),
    Term.Select(ScalaFailure, Apply) -> Term.Select(JavaTry, JavaFailure),

    Term.Select(ScalaFuture, Apply) -> Term.Select(JavaCompletableFuture, JavaSupplyAsync),
    Term.Select(ScalaFuture, ScalaSuccessful) -> Term.Select(JavaCompletableFuture, JavaCompletedFuture),
    Term.Select(ScalaFuture, ScalaFailed) -> Term.Select(JavaCompletableFuture, JavaFailedFuture),

    ScalaPrint -> JavaPrint,
    ScalaPrintln -> JavaPrintln
  )

  private val JavaSupplierQualifiedNames = Set(
    Term.Select(JavaTry, JavaOfSupplier),
    Term.Select(JavaCompletableFuture, JavaSupplyAsync)
  )

  override final def transform(qualifiedTermApply: QualifiedTermApply, context: TermApplyTransformationContext)
    : Option[QualifiedTermApply] =

    transformByQualifiedName(qualifiedTermApply.qualifiedName)
      .map(transformedQualifiedName => {
        val transformedArgs = transformArgs(transformedQualifiedName, qualifiedTermApply.args)
        qualifiedTermApply.copy(qualifiedName = transformedQualifiedName, args = transformedArgs)
      })

  // Transform a method name which is a Scala-specific qualified name, into an equivalent in Java
  // Either and Try use the syntax of the VAVR framework (Maven: io.vavr:vavr)
  private def transformByQualifiedName(termSelect: Term.Select) = {
    TreeKeyedMap.get(ScalaToJavaQualifiedName, termSelect)
      .orElse(transformByQualifiedNameSpecialCases(termSelect))
  }

  private def transformByQualifiedNameSpecialCases(termSelect: Term.Select) = {
    (termSelect.qual, termSelect.name) match {
      case (qual: Term.Select, q"apply" | q"empty") if termSelectClassifier.isJavaStreamLike(qual) =>
        Some(Term.Select(JavaStream, JavaOf))
      case (qual: Term.Select, q"apply" | q"empty") if termSelectClassifier.isJavaListLike(qual) =>
        Some(Term.Select(JavaList, JavaOf))
      case (qual: Term.Select, q"apply" | q"empty") if termSelectClassifier.isJavaSetLike(qual) =>
        Some(Term.Select(JavaSet, JavaOf))
      case (qual: Term.Select, q"apply") if termSelectClassifier.isJavaMapLike(qual) =>
        Some(Term.Select(JavaMap, JavaOfEntries))
      case (qual: Term.Select, q"empty") if termSelectClassifier.isJavaMapLike(qual) =>
        Some(Term.Select(JavaMap, JavaOf))
      case _ => None
    }
  }

  private def transformArgs(transformedQual: Term, args: List[Term]): List[Term] = {
    (transformedQual, args) match {
      case (theTransformedQual: Term.Select, arg :: Nil) if isJavaSupplierMethod(theTransformedQual) => List(Term.Function(Nil, arg))
      case (_, theArgs) => theArgs
    }
  }

  private def isJavaSupplierMethod(termSelect: Term.Select) = TreeElemSet.contains(JavaSupplierQualifiedNames, termSelect)
}
