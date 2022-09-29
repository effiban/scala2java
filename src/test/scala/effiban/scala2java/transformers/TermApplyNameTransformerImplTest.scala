package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermNameClassifier
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames

import scala.meta.Term

class TermApplyNameTransformerImplTest extends UnitTestSuite {

  private val termNameClassifier = mock[TermNameClassifier]

  private val termApplyNameTransformer = new TermApplyNameTransformerImpl(termNameClassifier)

  test("transform 'Range' should return 'IntStream.range'") {
    val scalaTermName = TermNames.ScalaRange
    val expectedJavaTerm = Term.Select(TermNames.JavaIntStream, TermNames.JavaRange)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Option' should return 'Optional.ofNullable'") {
    val scalaTermName = TermNames.ScalaOption
    val expectedJavaTerm = Term.Select(TermNames.JavaOptional, TermNames.JavaOfNullable)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Some' should return 'Optional.of'") {
    val scalaTermName = TermNames.ScalaSome
    val expectedJavaTerm = Term.Select(TermNames.JavaOptional, TermNames.JavaOf)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Right' should return 'Either.right'") {
    val scalaTermName = TermNames.ScalaRight
    val expectedJavaTerm = Term.Select(TermNames.Either, TermNames.LowercaseRight)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Left' should return 'Either.left'") {
    val scalaTermName = TermNames.ScalaLeft
    val expectedJavaTerm = Term.Select(TermNames.Either, TermNames.LowercaseLeft)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Stream' should return 'Stream.of'") {
    val scalaTermName = TermNames.Stream
    val expectedJavaTerm = Term.Select(TermNames.Stream, TermNames.JavaOf)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Seq' should return 'List.of'") {
    val scalaTermName = TermNames.Seq
    val expectedJavaTerm = Term.Select(TermNames.List, TermNames.JavaOf)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Set' should return 'Set.of'") {
    val scalaTermName = TermNames.Set
    val expectedJavaTerm = Term.Select(TermNames.Set, TermNames.JavaOf)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'Map' should return 'Map.ofEntries'") {
    val scalaTermName = TermNames.Map
    val expectedJavaTerm = Term.Select(TermNames.Map, TermNames.JavaOfEntries)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termApplyNameTransformer.transform(scalaTermName).structure shouldBe expectedJavaTerm.structure
  }

  test("transform 'foo' should return 'foo'") {
    val termName = Term.Name("foo")

    termApplyNameTransformer.transform(termName).structure shouldBe termName.structure
  }
}
