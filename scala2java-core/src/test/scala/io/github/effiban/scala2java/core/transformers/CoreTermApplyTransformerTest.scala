package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.testtrees.TermNames.{Apply, Empty, Future, JavaAbsent, JavaCompletableFuture, JavaCompletedFuture, JavaFailedFuture, JavaFailure, JavaIntStream, JavaOf, JavaOfNullable, JavaOfSupplier, JavaOptional, JavaRange, JavaRangeClosed, JavaSuccess, JavaSupplyAsync, LowercaseLeft, LowercaseRight, ScalaFailed, ScalaFailure, ScalaInclusive, ScalaLeft, ScalaOption, ScalaRange, ScalaRight, ScalaSome, ScalaSuccess, ScalaSuccessful, Try}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm}


class CoreTermApplyTransformerTest extends UnitTestSuite {

  private val termNameClassifier = mock[TermNameClassifier]
  private val termSelectTermFunctionTransformer = mock[TermSelectTermFunctionTransformer]

  private val termApplyTransformer = new CoreTermApplyTransformer(termNameClassifier, termSelectTermFunctionTransformer)

  test("transform 'Range.apply' should return 'IntStream.range'") {
    val args = List(q"1", q"10")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRange, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaIntStream, JavaRange), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Range.inclusive' should return 'IntStream.rangeClosed'") {
    val args = List(q"1", q"10")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRange, ScalaInclusive), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaIntStream, JavaRangeClosed), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Option.apply' should return 'Optional.ofNullable'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaOption, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaOfNullable), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Option.empty' should return 'Optional.absent'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaOption, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaAbsent), Nil)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Some.apply' should return 'Optional.of'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSome, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaOf), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Right.apply' should return 'Either.right'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRight, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Either, LowercaseRight), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Left.apply' should return 'Either.left'") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(ScalaLeft, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Either, LowercaseLeft), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Try.apply' should return 'Try.ofSupplier'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(Try, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(Try, JavaOfSupplier), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Success.apply' should return 'Try.success'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSuccess, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(Try, JavaSuccess), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Failure.apply' should return 'Try.failure'") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(ScalaFailure, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(Try, JavaFailure), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.apply' should return 'CompletableFuture.supplyAsync'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Future, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaSupplyAsync), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.successful' should return 'CompletableFuture.completedFuture'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(Future, ScalaSuccessful), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaCompletedFuture), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.failed' should return 'CompletableFuture.failedFuture'") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(Future, ScalaFailed), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaFailedFuture), args)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Stream.apply' should return 'Stream.of'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Stream, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Stream, JavaOf), args)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Seq.apply' should return 'List.of'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Seq, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.List, JavaOf), args)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Seq.empty' should return 'List.of'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Seq, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.List, JavaOf), Nil)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Set.apply' should return 'Set.of'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Set, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Set, JavaOf), args)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Set.empty' should return 'Set.of'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Set, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Set, JavaOf), Nil)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Map.apply' should return 'Map.ofEntries'") {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Map, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Map, TermNames.JavaOfEntries), args)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Map.empty' should return 'Map.of'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Map, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Map, TermNames.JavaOf), Nil)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform a 'Term.Function' (lambda) with a method name, should return result of 'TermSelectTermFunctionTransformer'") {
    val lambda = q"(x: Int) => print(x)"
    val termApply = q"((x: Int) => print(x)).apply()"
    val expectedTransformedTermSelect = q"(((x: Int) => print(x)): Consumer[Int]).accept"
    val expectedTransformedTermApply = q"(((x: Int) => print(x)): Consumer[Int]).accept()"

    when(termSelectTermFunctionTransformer.transform(eqTree(lambda), eqTree(Apply))).thenReturn(expectedTransformedTermSelect)

    termApplyTransformer.transform(termApply).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform 'Dummy.dummy' should return same") {
    val termApply = Term.Apply(Term.Select(Term.Name("Dummy"), Term.Name("dummy")), Nil)

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }
}

