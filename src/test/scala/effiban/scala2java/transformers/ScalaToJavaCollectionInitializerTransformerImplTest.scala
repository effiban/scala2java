package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermNameClassifier
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{TermNames, TypeNames}

import scala.meta.Term.Name
import scala.meta.{Lit, Term}

class ScalaToJavaCollectionInitializerTransformerImplTest extends UnitTestSuite {

  private val ScalarArgs = List(Lit.Int(1), Lit.Int(2))
  private val AssociativeArgs = List(
    Term.ApplyInfix(Lit.String("a"), Name("->"), Nil, List(Lit.Int(1))),
    Term.ApplyInfix(Lit.String("b"), Name("->"), Nil, List(Lit.Int(2)))
  )
  private val ScalarTypes = List(TypeNames.Int)
  private val MapTypes = List(TypeNames.String, TypeNames.Int)

  private val termNameClassifier = mock[TermNameClassifier]

  private val collectionInitializerTransformer = new ScalaToJavaCollectionInitializerTransformerImpl(termNameClassifier)

  test("transform Stream(1, 2) should return Stream.of(1, 2)") {
    val scalaInitializer = Term.Apply(TermNames.Stream, ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.Select(TermNames.Stream, Name("of")), ScalarArgs)

    when(termNameClassifier.isLazySeqLike(eqTree(TermNames.Stream))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Stream[Int](1, 2) should return Stream.of[Int](1, 2)") {
    val scalaInitializer = Term.Apply(Term.ApplyType(TermNames.Stream, ScalarTypes), ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.ApplyType(Term.Select(TermNames.Stream, Name("of")), ScalarTypes), ScalarArgs)

    when(termNameClassifier.isLazySeqLike(eqTree(TermNames.Stream))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform List(1, 2) should return List.of(1, 2)") {
    val scalaInitializer = Term.Apply(TermNames.List, ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.Select(TermNames.List, Name("of")), ScalarArgs)

    when(termNameClassifier.isEagerSeqLike(eqTree(TermNames.List))).thenReturn(true)
    
    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform List[Int](1, 2) should return List.of[Int](1, 2)") {
    val scalaInitializer = Term.Apply(Term.ApplyType(TermNames.List, ScalarTypes), ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.ApplyType(Term.Select(TermNames.List, Name("of")), ScalarTypes), ScalarArgs)

    when(termNameClassifier.isEagerSeqLike(eqTree(TermNames.List))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Seq(1, 2) should return List.of(1, 2)") {
    val scalaInitializer = Term.Apply(TermNames.Seq, ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.Select(TermNames.List, Name("of")), ScalarArgs)

    when(termNameClassifier.isEagerSeqLike(eqTree(TermNames.Seq))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Seq[Int](1, 2) should return List.of[Int](1, 2)") {
    val scalaInitializer = Term.Apply(Term.ApplyType(TermNames.Seq, ScalarTypes), ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.ApplyType(Term.Select(TermNames.List, Name("of")), ScalarTypes), ScalarArgs)

    when(termNameClassifier.isEagerSeqLike(eqTree(TermNames.Seq))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Set(1, 2) should return Set.of(1, 2)") {
    val scalaInitializer = Term.Apply(TermNames.Set, ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.Select(TermNames.Set, Name("of")), ScalarArgs)

    when(termNameClassifier.isSetLike(eqTree(TermNames.Set))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Set[Int](1, 2) should return Set.of[Int](1, 2)") {
    val scalaInitializer = Term.Apply(Term.ApplyType(TermNames.Set, ScalarTypes), ScalarArgs)
    val expectedJavaInitializer = Term.Apply(Term.ApplyType(Term.Select(TermNames.Set, Name("of")), ScalarTypes), ScalarArgs)

    when(termNameClassifier.isSetLike(eqTree(TermNames.Set))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Map(\"a\" -> 1, \"b\" -> 2) should return Map.ofEntries(\"a\" -> 1, \"b\" -> 2)") {
    val scalaInitializer = Term.Apply(TermNames.Map, AssociativeArgs)
    val expectedJavaInitializer = Term.Apply(Term.Select(TermNames.Map, Name("ofEntries")), AssociativeArgs)

    when(termNameClassifier.isMapLike(eqTree(TermNames.Map))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }

  test("transform Map[String, Int](\"a\" -> 1, \"b\" -> 2) should return Map.ofEntries[String, Int](\"a\" -> 1, \"b\" -> 2)") {
    val scalaInitializer = Term.Apply(Term.ApplyType(TermNames.Map, MapTypes), AssociativeArgs)
    val expectedJavaInitializer = Term.Apply(Term.ApplyType(Term.Select(TermNames.Map, Name("ofEntries")), MapTypes), AssociativeArgs)

    when(termNameClassifier.isMapLike(eqTree(TermNames.Map))).thenReturn(true)

    collectionInitializerTransformer.transform(scalaInitializer).structure shouldBe expectedJavaInitializer.structure
  }
}
