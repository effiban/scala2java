package io.github.effiban.scala2java.transformers

import io.github.effiban.scala2java.classifiers.TermNameClassifier
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames
import io.github.effiban.scala2java.testtrees.TermNames._

import scala.meta.Term

class TermSelectTransformerImplTest extends UnitTestSuite {

  private val termNameClassifier = mock[TermNameClassifier]

  private val termSelectTransformer = new TermSelectTransformerImpl(termNameClassifier)

  test("transform 'Range.apply' should return 'IntStream.range'") {
    val scalaTermSelect = Term.Select(ScalaRange, Apply)
    val expectedJavaTermSelect = Term.Select(JavaIntStream, JavaRange)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Range.inclusive' should return 'IntStream.rangeClosed'") {
    val scalaTermSelect = Term.Select(ScalaRange, ScalaInclusive)
    val expectedJavaTermSelect = Term.Select(JavaIntStream, JavaRangeClosed)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Option.apply' should return 'Optional.ofNullable'") {
    val scalaTermSelect = Term.Select(ScalaOption, Apply)
    val expectedJavaTermSelect = Term.Select(JavaOptional, JavaOfNullable)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Option.empty' should return 'Optional.absent'") {
    val scalaTermSelect = Term.Select(ScalaOption, Empty)
    val expectedJavaTermSelect = Term.Select(JavaOptional, JavaAbsent)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Some.apply' should return 'Optional.of'") {
    val scalaTermSelect = Term.Select(ScalaSome, Apply)
    val expectedJavaTermSelect = Term.Select(JavaOptional, JavaOf)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Right.apply' should return 'Either.right'") {
    val scalaTermSelect = Term.Select(ScalaRight, Apply)
    val expectedJavaTermSelect = Term.Select(TermNames.Either, LowercaseRight)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Left.apply' should return 'Either.left'") {
    val scalaTermSelect = Term.Select(ScalaLeft, Apply)
    val expectedJavaTermSelect = Term.Select(TermNames.Either, LowercaseLeft)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Try.apply' should return 'Try.ofSupplier'") {
    val scalaTermSelect = Term.Select(Try, Apply)
    val expectedJavaTermSelect = Term.Select(Try, JavaOfSupplier)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Success.apply' should return 'Try.success'") {
    val scalaTermSelect = Term.Select(ScalaSuccess, Apply)
    val expectedJavaTermSelect = Term.Select(Try, JavaSuccess)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Failure.apply' should return 'Try.failure'") {
    val scalaTermSelect = Term.Select(ScalaFailure, Apply)
    val expectedJavaTermSelect = Term.Select(Try, JavaFailure)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Future.apply' should return 'CompletableFuture.supplyAsync'") {
    val scalaTermSelect = Term.Select(TermNames.Future, Apply)
    val expectedJavaTermSelect = Term.Select(JavaCompletableFuture, JavaSupplyAsync)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Future.successful' should return 'CompletableFuture.completedFuture'") {
    val scalaTermSelect = Term.Select(Future, ScalaSuccessful)
    val expectedJavaTermSelect = Term.Select(JavaCompletableFuture, JavaCompletedFuture)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Future.failed' should return 'CompletableFuture.failedFuture'") {
    val scalaTermSelect = Term.Select(Future, ScalaFailed)
    val expectedJavaTermSelect = Term.Select(JavaCompletableFuture, JavaFailedFuture)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'myTuple._1' should return 'myTuple.v1'") {
    val scalaTermSelect = Term.Select(Term.Name("myTuple"), Term.Name("_1"))
    val expectedJavaTermSelect = Term.Select(Term.Name("myTuple"), Term.Name("v1"))

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Stream.apply' should return 'Stream.of'") {
    val scalaTermSelect = Term.Select(TermNames.Stream, Apply)
    val expectedJavaTermSelect = Term.Select(TermNames.Stream, JavaOf)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Seq.apply' should return 'List.of'") {
    val scalaTermSelect = Term.Select(TermNames.Seq, Apply)
    val expectedJavaTermSelect = Term.Select(TermNames.List, JavaOf)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Seq.empty' should return 'List.of'") {
    val scalaTermSelect = Term.Select(TermNames.Seq, Empty)
    val expectedJavaTermSelect = Term.Select(TermNames.List, JavaOf)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Set.apply' should return 'Set.of'") {
    val scalaTermSelect = Term.Select(TermNames.Set, Apply)
    val expectedJavaTermSelect = Term.Select(TermNames.Set, JavaOf)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Set.empty' should return 'Set.of'") {
    val scalaTermSelect = Term.Select(TermNames.Set, Empty)
    val expectedJavaTermSelect = Term.Select(TermNames.Set, JavaOf)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Map.apply' should return 'Map.ofEntries'") {
    val scalaTermSelect = Term.Select(TermNames.Map, Apply)
    val expectedJavaTermSelect = Term.Select(TermNames.Map, TermNames.JavaOfEntries)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Map.empty' should return 'Map.of'") {
    val scalaTermSelect = Term.Select(TermNames.Map, Empty)
    val expectedJavaTermSelect = Term.Select(TermNames.Map, TermNames.JavaOf)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Dummy.dummy' should return the same") {
    val termSelect = Term.Select(Term.Name("Dummy"), Term.Name("dummy"))

    termSelectTransformer.transform(termSelect).structure shouldBe termSelect.structure
  }
}
