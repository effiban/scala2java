package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{TermSelectClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.entities.TermNames.{Apply, Empty, JavaCompletedFuture, JavaFailedFuture, JavaFailure, JavaOf, JavaOfEntries, JavaOfNullable, JavaOfSupplier, JavaRange, JavaRangeClosed, JavaSuccess, JavaSupplyAsync, LowercaseLeft, LowercaseRight, ScalaFailed, ScalaInclusive, ScalaSuccessful}
import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.entities.{TreeElemSet, TreeKeyedMap}
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

private[transformers] class CoreTermApplyTransformer(termSelectClassifier: TermSelectClassifier,
                                                     typeClassifier: TypeClassifier[Type],
                                                     termSelectTermFunctionTransformer: => TermSelectTermFunctionTransformer) extends TermApplyTransformer {

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

  override final def transform(termApply: Term.Apply, context: TermApplyTransformationContext = TermApplyTransformationContext()): Term.Apply =
    transformOptional(termApply, context)
      .getOrElse(termApply)

  private def transformOptional(termApply: Term.Apply, context: TermApplyTransformationContext) = {
    termApply match {
      case Term.Apply(termSelect: Term.Select, args) =>
        transformUntypedByQualifiedName(termSelect, args)
          .orElse(transformUntypedByQualifierTypeAndName(termSelect, args, context))

      case Term.Apply(Term.ApplyType(termSelect: Term.Select, targs), args) =>
        transformTypedByQualifiedName(termSelect, targs, args)
          .orElse(transformTypedByQualifierTypeAndName(termSelect, targs, args, context))

      case _ => None
    }
  }

  private def transformUntypedByQualifiedName(termSelect: Term.Select, args: List[Term]) = {
    transformByQualifiedName(termSelect)
      .map(transformedSelect => {
        val transformedArgs = transformArgs(transformedSelect, args)
        Term.Apply(transformedSelect, transformedArgs)
      })
  }

  private def transformTypedByQualifiedName(termSelect: Term.Select, targs: List[Type], args: List[Term]) = {
    transformByQualifiedName(termSelect)
      .map(transformedQual => {
        val transformedArgs = transformArgs(transformedQual, args)
        Term.Apply(Term.ApplyType(transformedQual, targs), transformedArgs)
      })
  }

  // Transform a method name which is a Scala-specific qualified name, into an equivalent in Java
  // Either and Try use the syntax of the VAVR framework (Maven: io.vavr:vavr)
  private def transformByQualifiedName(termSelect: Term.Select): Option[Term] = {
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
      case (qual, q"foreach") => Some(Term.Select(qual, q"forEach"))

      case (termFunction: Term.Function, methodName: Term.Name) =>
        Some(termSelectTermFunctionTransformer.transform(termFunction, methodName))

      case _ => None
    }
  }

  private def transformUntypedByQualifierTypeAndName(termSelect: Term.Select,
                                                     args: List[Term],
                                                     context: TermApplyTransformationContext): Option[Term.Apply] = {
    transformByQualifierTypeAndName(termSelect, args, context)
      .map { case (transformedSelect, transformedArgs) => Term.Apply(transformedSelect, transformedArgs) }
  }

  private def transformTypedByQualifierTypeAndName(termSelect: Term.Select,
                                                   targs: List[Type],
                                                   args: List[Term],
                                                   context: TermApplyTransformationContext): Option[Term.Apply] = {
    transformByQualifierTypeAndName(termSelect, args, context)
      .map { case (transformedSelect, transformedArgs) => Term.Apply(Term.ApplyType(transformedSelect, targs), transformedArgs) }
  }

  private def transformByQualifierTypeAndName(termSelect: Term.Select,
                                              args: List[Term],
                                              context: TermApplyTransformationContext): Option[(Term.Select, List[Term])] = {
    (context.maybeParentType, termSelect.name, args) match {
      case (Some(parentType), q"take", arg :: Nil) if typeClassifier.isJavaListLike(parentType) =>
        Some(termSelect.copy(name = q"subList"), List(q"0", arg))
      case (Some(parentType), q"length", Nil) if typeClassifier.isJavaListLike(parentType) => Some(termSelect.copy(name = q"size"), Nil)
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
