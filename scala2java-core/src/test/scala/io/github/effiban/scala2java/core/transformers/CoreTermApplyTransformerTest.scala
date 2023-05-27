package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{TermNameClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.{Apply, Empty, Future, JavaAbsent, JavaCompletableFuture, JavaCompletedFuture, JavaFailedFuture, JavaFailure, JavaIntStream, JavaOf, JavaOfNullable, JavaOfSupplier, JavaOptional, JavaRange, JavaRangeClosed, JavaSuccess, JavaSupplyAsync, LowercaseLeft, LowercaseRight, ScalaFailed, ScalaFailure, ScalaInclusive, ScalaLeft, ScalaOption, ScalaRange, ScalaRight, ScalaSome, ScalaSuccess, ScalaSuccessful, Try}
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}


class CoreTermApplyTransformerTest extends UnitTestSuite {
  
  private val DummyContext = TermApplyTransformationContext(maybeParentType = Some(t"Parent"))

  private val termNameClassifier = mock[TermNameClassifier]
  private val typeClassifier = mock[TypeClassifier[Type]]
  private val termSelectTermFunctionTransformer = mock[TermSelectTermFunctionTransformer]

  private val termApplyTransformer = new CoreTermApplyTransformer(
    termNameClassifier,
    typeClassifier,
    termSelectTermFunctionTransformer
  )

  test("transform 'Range.apply(1, 10)' should return 'IntStream.range(1, 10)'") {
    val args = List(q"1", q"10")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRange, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaIntStream, JavaRange), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Range.inclusive(1, 10)' should return 'IntStream.rangeClosed(1, 10)'") {
    val args = List(q"1", q"10")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRange, ScalaInclusive), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaIntStream, JavaRangeClosed), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Option.apply(1)' should return 'Optional.ofNullable(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaOption, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaOfNullable), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Option.apply[Int](1)' should return 'Optional.ofNullable[Int](1)'") {
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

  test("transform 'Option.empty()' should return 'Optional.absent()'") {
    val scalaTermApply = Term.Apply(Term.Select(ScalaOption, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaAbsent), Nil)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Option.empty[Int]()' should return 'Optional.absent[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaOption, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaOptional, JavaAbsent), typeArgs), Nil)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Some.apply(1)' should return 'Optional.of(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSome, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaOptional, JavaOf), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Some.apply[Int](1)' should return 'Optional.of[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSome, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaOptional, JavaOf), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Right.apply(1)' should return 'Either.right(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaRight, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Either, LowercaseRight), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Right.apply[Throwable, Int](1)' should return 'Either.right[Throwable, Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Throwable, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaRight, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Either, LowercaseRight), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'Left.apply("error")' should return 'Either.left("error")'""") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(ScalaLeft, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Either, LowercaseLeft), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'Left.apply[String, Int]("error")' should return 'Either.left[String, Int]("error")'""") {
    val args = List(q"error")
    val typeArgs = List(TypeNames.Throwable, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaLeft, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Either, LowercaseLeft), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Try.apply(1)' should return 'Try.ofSupplier(() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val scalaTermApply = Term.Apply(Term.Select(Try, Apply), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.Select(Try, JavaOfSupplier), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Try.apply[Int](1)' should return 'Try.ofSupplier[Int](() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(Try, Apply), typeArgs), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(Try, JavaOfSupplier), typeArgs), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Success.apply(1)' should return 'Try.success(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(ScalaSuccess, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(Try, JavaSuccess), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Success.apply[Int](1)' should return 'Try.success[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaSuccess, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply( Term.ApplyType(Term.Select(Try, JavaSuccess), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Failure.apply(new RuntimeException())' should return 'Try.failure(new RuntimeException())'") {
    val args = List(q"new RuntimeException()")
    val scalaTermApply = Term.Apply(Term.Select(ScalaFailure, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(Try, JavaFailure), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Failure.apply[Int](new RuntimeException())' should return 'Try.failure[Int](new RuntimeException())'") {
    val args = List(q"new RuntimeException()")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(ScalaFailure, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(Try, JavaFailure), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.apply(1)' should return 'CompletableFuture.supplyAsync(() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Future, Apply), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaSupplyAsync), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.apply[Int](1)' should return 'CompletableFuture.supplyAsync[Int](() => 1)'") {
    val scalaArgs = List(q"1")
    val expectedJavaArgs = List(q"() => 1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Future, Apply), typeArgs), scalaArgs)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaCompletableFuture, JavaSupplyAsync), typeArgs), expectedJavaArgs)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.successful(1)' should return 'CompletableFuture.completedFuture(1)'") {
    val args = List(q"1")
    val scalaTermApply = Term.Apply(Term.Select(Future, ScalaSuccessful), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaCompletedFuture), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Future.successful[Int](1)' should return 'CompletableFuture.completedFuture[Int](1)'") {
    val args = List(q"1")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(Future, ScalaSuccessful), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaCompletableFuture, JavaCompletedFuture), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'Future.failed("error")' should return 'CompletableFuture.failedFuture("error")'""") {
    val args = List(q"error")
    val scalaTermApply = Term.Apply(Term.Select(Future, ScalaFailed), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(JavaCompletableFuture, JavaFailedFuture), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'Future.failed[Int]("error")' should return 'CompletableFuture.failedFuture[Int]("error")'""") {
    val args = List(q"error")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(Future, ScalaFailed), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(JavaCompletableFuture, JavaFailedFuture), typeArgs), args)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Stream.apply(1, 2)' should return 'Stream.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Stream, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Stream, JavaOf), args)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Stream.apply[Int](1, 2)' should return 'Stream.of[Int](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Stream, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Stream, JavaOf), typeArgs), args)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Stream.empty()' should return 'Stream.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Stream, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Stream, JavaOf), Nil)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Stream.empty[Int]()' should return 'Stream.of[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Stream, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Stream, JavaOf), typeArgs), Nil)

    when(termNameClassifier.isJavaStreamLike(eqTree(TermNames.Stream))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Seq.apply(1, 2)' should return 'List.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Seq, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.List, JavaOf), args)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Seq.apply[Int](1, 2)' should return 'List.of[Int](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Seq, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.List, JavaOf), typeArgs), args)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Seq.empty()' should return 'List.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Seq, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.List, JavaOf), Nil)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Seq.empty[Int]()' should return 'List.of[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Seq, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.List, JavaOf), typeArgs), Nil)

    when(termNameClassifier.isJavaListLike(eqTree(TermNames.Seq))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Set.apply(1, 2)' should return 'Set.of(1, 2)'") {
    val args = List(q"1", q"2")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Set, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Set, JavaOf), args)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Set.apply[Int](1, 2)' should return 'Set.of[Int](1, 2)'") {
    val args = List(q"1", q"2")
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Set, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Set, JavaOf), typeArgs), args)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Set.empty()' should return 'Set.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Set, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Set, JavaOf), Nil)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Set.empty[Int]()' should return 'Set.of[Int]()'") {
    val typeArgs = List(TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Set, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Set, JavaOf), typeArgs), Nil)

    when(termNameClassifier.isJavaSetLike(eqTree(TermNames.Set))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'Map.apply(("a", 1), ("b", 2))' should return 'Map.ofEntries(("a", 1), ("b", 2))'""") {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Map, Apply), args)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Map, TermNames.JavaOfEntries), args)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("""transform 'Map.apply[String, Int](("a", 1), ("b", 2))' should return 'Map.ofEntries[String, Int](("a", 1), ("b", 2))'""") {
    val args = List(q"""("a", 1)""", q"""("b", 2)""")
    val typeArgs = List(TypeNames.String, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Map, Apply), typeArgs), args)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Map, TermNames.JavaOfEntries), typeArgs), args)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Map.empty()' should return 'Map.of()'") {
    val scalaTermApply = Term.Apply(Term.Select(TermNames.Map, Empty), Nil)
    val expectedJavaTermApply = Term.Apply(Term.Select(TermNames.Map, TermNames.JavaOf), Nil)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, DummyContext).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Map.empty[String, Int]()' should return 'Map.of[String, Int]()'") {
    val typeArgs = List(TypeNames.String, TypeNames.Int)
    val scalaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Map, Empty), typeArgs), Nil)
    val expectedJavaTermApply = Term.Apply(Term.ApplyType(Term.Select(TermNames.Map, TermNames.JavaOf), typeArgs), Nil)

    when(termNameClassifier.isJavaMapLike(eqTree(TermNames.Map))).thenReturn(true)

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

  test("transform 'myList.take(2)' with parent type 'List[String]', should return 'myList.subList(0, 2)'") {
    val arg = q"2"
    val scalaTermApply = Term.Apply(q"myList.take", List(arg))
    val parentType = Type.Apply(TypeNames.List, List(TypeNames.String))
    val context = TermApplyTransformationContext(maybeParentType = Some(parentType))
    val expectedJavaTermApply = Term.Apply(q"myList.subList", List(q"0", arg))

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, context).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'myList.length()' with parent type 'List[String]', should return 'myList.size()'") {
    val scalaTermApply = q"myList.length()"
    val parentType = Type.Apply(TypeNames.List, List(TypeNames.String))
    val context = TermApplyTransformationContext(maybeParentType = Some(parentType))
    val expectedJavaTermApply = q"myList.size()"

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    termApplyTransformer.transform(scalaTermApply, context).structure shouldBe expectedJavaTermApply.structure
  }

  test("transform 'Dummy.dummy(1)' should return same") {
    val termApply = q"Dummy.dummy(1)"

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }

}

