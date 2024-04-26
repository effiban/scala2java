package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{TermSelectClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.entities.TermNames.{Apply, Empty, JavaCompletedFuture, JavaFailedFuture, JavaFailure, JavaOf, JavaOfEntries, JavaOfNullable, JavaOfSupplier, JavaRange, JavaRangeClosed, JavaSuccess, JavaSupplyAsync, LowercaseLeft, LowercaseRight, ScalaFailed, ScalaInclusive, ScalaSuccessful}
import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}


class CoreTermApplyTransformerTest extends UnitTestSuite {
  
  private val DummyContext = TermApplyTransformationContext(maybeParentType = Some(t"Parent"))

  private val termSelectClassifier = mock[TermSelectClassifier]
  private val typeClassifier = mock[TypeClassifier[Type]]
  private val termSelectTermFunctionTransformer = mock[TermSelectTermFunctionTransformer]

  private val termApplyTransformer = new CoreTermApplyTransformer(
    termSelectClassifier,
    typeClassifier,
    termSelectTermFunctionTransformer
  )

  test("transform 'scala.Range.apply(1, 10)' should return 'java.util.stream.IntStream.range(1, 10)'") {
    val args = List(q"1", q"10")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRange, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaIntStream, JavaRange), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Range.inclusive(1, 10)' should return 'java.util.stream.IntStream.rangeClosed(1, 10)'") {
    val args = List(q"1", q"10")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRange, ScalaInclusive), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaIntStream, JavaRangeClosed), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Option.apply(1)' should return 'java.util.Optional.ofNullable(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaOption, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaOfNullable), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Option.apply[Int](1)' should return 'java.util.Optional.ofNullable[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(
      Term.ApplyType(Term.Select(ScalaOption, Apply), typeArgs),
      args)
    val expectedJavaTermApply = Term.Apply(
      Term.ApplyType(Term.Select(JavaOptional, JavaOfNullable), typeArgs),
      args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Option.empty()' should return 'java.util.Optional.empty()'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaOption, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, Empty), Nil)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Option.empty[Int]()' should return 'java.util.Optional.empty[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaOption, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaOptional, Empty), typeArgs), Nil)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Some.apply(1)' should return 'java.util.Optional.of(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSome, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaOf), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Some.apply[Int](1)' should return 'java.util.Optional.of[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSome, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaOptional, JavaOf), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Right.apply(1)' should return 'io.vavr.control.Either.right(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRight, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaEither, LowercaseRight), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Right.apply[Throwable, Int](1)' should return 'io.vavr.control.Either.right[Throwable, Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Throwable, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaRight, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaEither, LowercaseRight), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'scala.Left.apply("error")' should return 'io.vavr.control.Either.left("error")'""") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(ScalaLeft, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaEither, LowercaseLeft), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'scala.Left.apply[String, Int]("error")' should return 'io.vavr.control.Either.left[String, Int]("error")'""") {
    val args = List(q"error")
    val typeArgs = List(TypeNames.Throwable, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaLeft, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaEither, LowercaseLeft), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Try.apply(1)' should return 'io.vavr.control.Try.ofSupplier(() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaTry, Apply), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaTry, JavaOfSupplier), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Try.apply[Int](1)' should return 'io.vavr.control.Try.ofSupplier[Int](() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaTry, Apply), typeArgs), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaTry, JavaOfSupplier), typeArgs), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Success.apply(1)' should return 'io.vavr.control.Try.success(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSuccess, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaTry, JavaSuccess), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Success.apply[Int](1)' should return 'io.vavr.control.Try.success[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSuccess, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply( Term.ApplyType(Term.Select(JavaTry, JavaSuccess), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.util.Failure.apply(new RuntimeException())' should return 'io.vavr.control.Try.failure(new RuntimeException())'") {
    val args = List(q"new RuntimeException()")
    val scalaTermApply = Term.Apply(Term.Select(ScalaFailure, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaTry, JavaFailure), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.util.Failure.apply[Int](new RuntimeException())' should return 'io.vavr.control.Try.failure[Int](new RuntimeException())'") {
    val args = List(q"new RuntimeException()")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaFailure, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaTry, JavaFailure), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.concurrent.Future.apply(1)' should return 'java.util.concurrent.CompletableFuture.supplyAsync(() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaFuture, Apply), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaSupplyAsync), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.concurrent.Future.apply[Int](1)' should return 'java.util.concurrent.CompletableFuture.supplyAsync[Int](() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaFuture, Apply), typeArgs), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaCompletableFuture, JavaSupplyAsync), typeArgs), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.concurrent.Future.successful(1)' should return 'java.util.concurrent.CompletableFuture.completedFuture(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaFuture, ScalaSuccessful), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaCompletedFuture), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.concurrent.Future.successful[Int](1)' should return 'java.util.concurrent.CompletableFuture.completedFuture[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaFuture, ScalaSuccessful), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaCompletableFuture, JavaCompletedFuture), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'scala.concurrent.Future.failed("error")' should return 'java.util.concurrent.CompletableFuture.failedFuture("error")'""") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(ScalaFuture, ScalaFailed), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaFailedFuture), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'scala.concurrent.Future.failed[Int]("error")' should return 'java.util.concurrent.CompletableFuture.failedFuture[Int]("error")'""") {
    val args = List(q"error")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaFuture, ScalaFailed), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaCompletableFuture, JavaFailedFuture), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Stream.apply(1, 2)' should return 'java.util.stream.Stream.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(ScalaStream, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaStream, JavaOf), args)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Stream.apply[Int](1, 2)' should return 'java.util.stream.Stream.of[Int](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaStream, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaStream, JavaOf), typeArgs), args)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Stream.empty()' should return 'java.util.stream.Stream.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaStream, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaStream, JavaOf), Nil)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Stream.empty[Int]()' should return 'java.util.stream.Stream.of[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaStream, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaStream, JavaOf), typeArgs), Nil)

    when(termSelectClassifier.isJavaStreamLike(eqTree(ScalaStream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Seq.apply(1, 2)' should return 'java.util.List.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSeq, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaList, JavaOf), args)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Seq.apply[Int](1, 2)' should return 'java.util.List.of[Int](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSeq, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaList, JavaOf), typeArgs), args)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Seq.empty()' should return 'java.util.List.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaSeq, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaList, JavaOf), Nil)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Seq.empty[Int]()' should return 'java.util.List.of[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSeq, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaList, JavaOf), typeArgs), Nil)

    when(termSelectClassifier.isJavaListLike(eqTree(ScalaSeq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Set.apply(1, 2)' should return 'java.util.Set.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSet, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaSet, JavaOf), args)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Set.apply[Int](1, 2)' should return 'java.util.Set.of[Int](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSet, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaSet, JavaOf), typeArgs), args)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Set.empty()' should return 'java.util.Set.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaSet, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaSet, JavaOf), Nil)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Set.empty[Int]()' should return 'java.util.Set.of[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSet, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaSet, JavaOf), typeArgs), Nil)

    when(termSelectClassifier.isJavaSetLike(eqTree(ScalaSet))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'scala.Map.apply(("a", 1), ("b", 2))' should return 'java.util.Map.ofEntries(("a", 1), ("b", 2))'""") {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val scalaTermApply = Term.Apply(Term.Select(ScalaMap, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaMap, JavaOfEntries), args)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'scala.Map.apply[String, Int](("a", 1), ("b", 2))' should return 'java.util.Map.ofEntries[String, Int](("a", 1), ("b", 2))'""") {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val typeArgs = List(TypeNames.String, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaMap, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaMap, JavaOfEntries), typeArgs), args)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Map.empty()' should return 'java.util.Map.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaMap, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaMap, TermNames.JavaOf), Nil)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'scala.Map.empty[String, Int]()' should return 'java.util.Map.of[String, Int]()'") {
    val typeArgs = List(TypeNames.String, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaMap, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaMap, TermNames.JavaOf), typeArgs), Nil)

    when(termSelectClassifier.isJavaMapLike(eqTree(ScalaMap))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform a 'Term.Function' (lambda) with a method name, should return result of 'TermSelectTermFunctionTransformer'") {
    val lambda = q"(x: Int) => print(x)"
    val termApply = q"((x: Int) => print(x)).apply()"
    val expectedTransformedTermSelect = q"(((x: Int) => print(x)): Consumer[Int]).accept"
    val expectedTransformedTermApply = q"(((x: Int) => print(x)): Consumer[Int]).accept()"

    when(termSelectTermFunctionTransformer.transform(eqTree(lambda), eqTree(Apply))).thenReturn(expectedTransformedTermSelect)

    termApplyTransformer.transform(termApply).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform 'myList.take(2)' with parent type 'scala.List[String]', should return 'myList.subList(0, 2)'") {
    val arg = q"2"
    val scalaTermApply = Term.Apply(q"myList.take", List(arg))
    val parentType = Type.Apply(TypeNames.List, List(TypeNames.String))
    val context = TermApplyTransformationContext(maybeParentType = Some(parentType))
    val expectedJavaTermApply = Term.Apply(q"myList.subList", List(q"0", arg))

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, context).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'myList.length()' with parent type 'scala.List[String]', should return 'myList.size()'") {
    val scalaTermApply = q"myList.length()"
    val parentType = Type.Apply(TypeNames.List, List(TypeNames.String))
    val context = TermApplyTransformationContext(maybeParentType = Some(parentType))
    val expectedJavaTermApply = q"myList.size()"

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, context).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'elems.foreach(print(_))' should return 'elems.forEach(print(_))'") {
    val scalaTermApply = q"elems.foreach(print(_))"
    val expectedJavaTermApply = q"elems.forEach(print(_))"

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }


  test("transform 'Dummy.dummy(1)' should return same") {
    val termApply = q"Dummy.dummy(1)"

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }

}

