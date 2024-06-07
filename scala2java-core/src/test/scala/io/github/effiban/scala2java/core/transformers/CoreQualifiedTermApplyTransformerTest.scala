package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermSelectClassifier
import io.github.effiban.scala2java.core.entities.TermNames.{Apply, Empty, JavaCompletedFuture, JavaFailedFuture, JavaFailure, JavaOf, JavaOfEntries, JavaOfNullable, JavaOfSupplier, JavaRange, JavaRangeClosed, JavaSuccess, JavaSupplyAsync, LowercaseLeft, LowercaseRight, ScalaFailed, ScalaInclusive, ScalaSuccessful}
import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.entities.{TermNames, TypeSelects}
import io.github.effiban.scala2java.core.matchers.QualifiedTermApplyScalatestMatcher.equalQualifiedTermApply
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.{PartialDeclDef, QualifiedTermApply}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}


class CoreQualifiedTermApplyTransformerTest extends UnitTestSuite {

  private val DummyContext = TermApplyTransformationContext(partialDeclDef = PartialDeclDef(maybeParamTypes = List(Some(t"scala.Int"))))

  private val termSelectClassifier = mock[TermSelectClassifier]

  private val qualifiedTermApplyTransformer = new CoreQualifiedTermApplyTransformer(
    termSelectClassifier
  )

  test("transform 'scala.Range.apply(1, 10)' should return 'java.util.stream.IntStream.range(1, 10)'") {
    val args = List(q"1", q"10")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaRange, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaIntStream, JavaRange), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Range.inclusive(1, 10)' should return 'java.util.stream.IntStream.rangeClosed(1, 10)'") {
    val args = List(q"1", q"10")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaRange, ScalaInclusive), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaIntStream, JavaRangeClosed), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Option.apply(1)' should return 'java.util.Optional.ofNullable(1)'") {
    val args = List(q"1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaOption, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaOptional, JavaOfNullable), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Option.apply[java.lang.Integer](1)' should return 'java.util.Optional.ofNullable[java.lang.Integer](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaOption, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaOptional, JavaOfNullable), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Option.empty()' should return 'java.util.Optional.empty()'") {
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaOption, Empty), Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaOptional, Empty), Nil)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Option.empty[java.lang.Integer]()' should return 'java.util.Optional.empty[java.lang.Integer]()'") {
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaOption, Empty), typeArgs, Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaOptional, Empty), typeArgs, Nil)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Some.apply(1)' should return 'java.util.Optional.of(1)'") {
    val args = List(q"1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSome, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaOptional, JavaOf), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Some.apply[java.lang.Integer](1)' should return 'java.util.Optional.of[java.lang.Integer](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSome, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaOptional, JavaOf), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Right.apply(1)' should return 'io.vavr.control.Either.right(1)'") {
    val args = List(q"1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaRight, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaEither, LowercaseRight), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Right.apply[java.lang.Throwable, java.lang.Integer](1)' " +
    "should return 'io.vavr.control.Either.right[java.lang.Throwable, java.lang.Integer](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeSelects.JavaThrowable, TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaRight, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaEither, LowercaseRight), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("""transform 'scala.Left.apply("error")' should return 'io.vavr.control.Either.left("error")'""") {
    val args = List(q"error")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaLeft, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaEither, LowercaseLeft), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test(
    """transform 'scala.Left.apply[java.lang.Throwable, java.lang.Integer]("error")'
      |should return 'io.vavr.control.Either.left[java.lang.Throwable, java.lang.Integer]("error")'""".stripMargin) {
    val args = List(q"error")
    val typeArgs = List(TypeSelects.JavaThrowable, TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaLeft, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaEither, LowercaseLeft), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Try.apply(1)' should return 'io.vavr.control.Try.ofSupplier(() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaTry, Apply), scalaArgs)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaTry, JavaOfSupplier), expectedJavaArgs)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Try.apply[java.lang.Integer](1)' should return 'io.vavr.control.Try.ofSupplier[java.lang.Integer](() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaTry, Apply), typeArgs, scalaArgs)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaTry, JavaOfSupplier), typeArgs, expectedJavaArgs)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Success.apply(1)' should return 'io.vavr.control.Try.success(1)'") {
    val args = List(q"1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSuccess, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaTry, JavaSuccess), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Success.apply[java.lang.Integer](1)' should return 'io.vavr.control.Try.success[java.lang.Integer](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSuccess, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaTry, JavaSuccess), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.util.Failure.apply(new java.lang.RuntimeException())' " +
    "should return 'io.vavr.control.Try.failure(new java.lang.RuntimeException())'") {
    val args = List(q"new java.lang.RuntimeException()")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFailure, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaTry, JavaFailure), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.util.Failure.apply[Int](new java.lang.RuntimeException())' " +
    "should return 'io.vavr.control.Try.failure[Int](new java.lang.RuntimeException())'") {
    val args = List(q"new java.lang.RuntimeException()")
    val typeArgs = List(TypeSelects.ScalaInt)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFailure, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaTry, JavaFailure), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.concurrent.Future.apply(1)' should return 'java.util.concurrent.CompletableFuture.supplyAsync(() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFuture, Apply), scalaArgs)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaCompletableFuture, JavaSupplyAsync), expectedJavaArgs)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.concurrent.Future.apply[java.lang.Integer](1)' " +
    "should return 'java.util.concurrent.CompletableFuture.supplyAsync[java.lang.Integer](() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFuture, Apply), typeArgs, scalaArgs)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaCompletableFuture, JavaSupplyAsync), typeArgs, expectedJavaArgs)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.concurrent.Future.successful(1)' should return 'java.util.concurrent.CompletableFuture.completedFuture(1)'") {
    val args = List(q"1")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFuture, ScalaSuccessful), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaCompletableFuture, JavaCompletedFuture), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.concurrent.Future.successful[java.lang.Integer](1)' " +
    "should return 'java.util.concurrent.CompletableFuture.completedFuture[java.lang.Integer](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFuture, ScalaSuccessful), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaCompletableFuture, JavaCompletedFuture), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("""transform 'scala.concurrent.Future.failed("error")' should return 'java.util.concurrent.CompletableFuture.failedFuture("error")'""") {
    val args = List(q"error")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFuture, ScalaFailed), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaCompletableFuture, JavaFailedFuture), args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test(
    """transform 'scala.concurrent.Future.failed[java.lang.Integer]("error")'
      |should return 'java.util.concurrent.CompletableFuture.failedFuture[java.lang.Integer]("error")'""".stripMargin) {
    val args = List(q"error")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaFuture, ScalaFailed), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaCompletableFuture, JavaFailedFuture), typeArgs, args)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Stream.apply(1, 2)' should return 'java.util.stream.Stream.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaStream, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaStream, JavaOf), args)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Stream.apply[java.lang.Integer](1, 2)' should return 'java.util.stream.Stream.of[java.lang.Integer](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaStream, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaStream, JavaOf), typeArgs, args)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Stream.empty()' should return 'java.util.stream.Stream.of()'") {
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaStream, Empty), Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaStream, JavaOf), Nil)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Stream.empty[java.lang.Integer]()' should return 'java.util.stream.Stream.of[java.lang.Integer]()'") {
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaStream, Empty), typeArgs, Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaStream, JavaOf), typeArgs, Nil)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Seq.apply(1, 2)' should return 'java.util.List.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSeq, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaList, JavaOf), args)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Seq.apply[java.lang.Integer](1, 2)' should return 'java.util.List.of[java.lang.Integer](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSeq, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaList, JavaOf), typeArgs, args)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Seq.empty()' should return 'java.util.List.of()'") {
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSeq, Empty), Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaList, JavaOf), Nil)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Seq.empty[java.lang.Integer]()' should return 'java.util.List.of[java.lang.Integer]()'") {
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSeq, Empty), typeArgs, Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaList, JavaOf), typeArgs, Nil)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Set.apply(1, 2)' should return 'java.util.Set.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSet, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaSet, JavaOf), args)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Set.apply[java.lang.Integer](1, 2)' should return 'java.util.Set.of[java.lang.Integer](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSet, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaSet, JavaOf), typeArgs, args)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Set.empty()' should return 'java.util.Set.of()'") {
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSet, Empty), Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaSet, JavaOf), Nil)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Set.empty[java.lang.Integer]()' should return 'java.util.Set.of[java.lang.Integer]()'") {
    val typeArgs = List(TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaSet, Empty), typeArgs, Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaSet, JavaOf), typeArgs, Nil)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("""transform 'scala.Map.apply(("a", 1), ("b", 2))' should return 'java.util.Map.ofEntries(("a", 1), ("b", 2))'""") {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaMap, Apply), args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaMap, JavaOfEntries), args)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test(
    """transform 'scala.Map.apply[java.lang.String, java.lang.Integer](("a", 1), ("b", 2))'
      |should return 'java.util.Map.ofEntries[java.lang.String, java.lang.Integer](("a", 1), ("b", 2))'""".stripMargin) {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val typeArgs = List(TypeSelects.JavaString, TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaMap, Apply), typeArgs, args)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaMap, JavaOfEntries), typeArgs, args)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Map.empty()' should return 'java.util.Map.of()'") {
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaMap, Empty), Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaMap, TermNames.JavaOf), Nil)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'scala.Map.empty[java.lang.String, java.lang.Integer]()' " +
    "should return 'java.util.Map.of[java.lang.String, java.lang.Integer]()'") {
    val typeArgs = List(TypeSelects.JavaString, TypeSelects.JavaInteger)
    val scalaQualifiedTermApply = QualifiedTermApply(Term.Select(ScalaMap, Empty), typeArgs, Nil)
    val expectedJavaQualifiedTermApply = QualifiedTermApply(Term.Select(JavaMap, TermNames.JavaOf), typeArgs, Nil)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    qualifiedTermApplyTransformer.transform(scalaQualifiedTermApply, DummyContext).value should
      equalQualifiedTermApply(expectedJavaQualifiedTermApply)
  }

  test("transform 'Dummy.dummy(1)' should return None") {
    val qualifiedTermApply = QualifiedTermApply(q"Dummy.dummy", List(q"1"))

    qualifiedTermApplyTransformer.transform(qualifiedTermApply, DummyContext) shouldBe None
  }

}

