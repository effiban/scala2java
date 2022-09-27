package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{TermNames, TypeNames}
import effiban.scala2java.transformers.ScalaToJavaCollectionInitializerTransformer.transform

import scala.meta.Lit
import scala.meta.Term.{Apply, ApplyInfix, ApplyType, Name, Select}

class ScalaToJavaCollectionInitializerTransformerTest extends UnitTestSuite {

  private val ScalarArgs = List(Lit.Int(1), Lit.Int(2))
  private val AssociativeArgs = List(
    ApplyInfix(Lit.String("a"), Name("->"), Nil, List(Lit.Int(1))),
    ApplyInfix(Lit.String("b"), Name("->"), Nil, List(Lit.Int(2)))
  )
  private val ScalarTypes = List(TypeNames.Int)
  private val MapTypes = List(TypeNames.String, TypeNames.Int)


  private val Scenarios = Table(
    ("ScalaInitializer",                                                 "JavaInitializer"),

    // List(1, 2)                                                        List.of(1, 2)
    (Apply(TermNames.List, ScalarArgs),                                  Apply(Select(TermNames.List, Name("of")), ScalarArgs)),
    // List[Int](1, 2)                                                   List[Int].of(1, 2)
    (Apply(ApplyType(TermNames.List, ScalarTypes), ScalarArgs),          Apply(Select(ApplyType(TermNames.List, ScalarTypes), Name("of")), ScalarArgs)),

    // Seq(1, 2)                                                         List.of(1, 2)
    (Apply(TermNames.Seq, ScalarArgs),                                   Apply(Select(TermNames.List, Name("of")), ScalarArgs)),
    // Seq[Int](1, 2)                                                    List[Int].of(1, 2)
    (Apply(ApplyType(TermNames.Seq, ScalarTypes), ScalarArgs),           Apply(Select(ApplyType(TermNames.List, ScalarTypes), Name("of")), ScalarArgs)),

    // Set(1, 2)                                                         Set.of(1, 2)
    (Apply(TermNames.Set, ScalarArgs),                                   Apply(Select(TermNames.Set, Name("of")), ScalarArgs)),
    // Set[Int](1, 2)                                                    Set[Int].of(1, 2)
    (Apply(ApplyType(TermNames.Set, ScalarTypes), ScalarArgs),           Apply(Select(ApplyType(TermNames.Set, ScalarTypes), Name("of")), ScalarArgs)),

    // Map("a" -> 1, "b" -> 2)                                           Map.ofEntries(....)
    (Apply(TermNames.Map, AssociativeArgs),                              Apply(Select(TermNames.Map, Name("ofEntries")), AssociativeArgs)),
    // Map[String, Int]("a" -> 1, "b" -> 2)                              Map[String, Int].ofEntries(.....)
    (Apply(ApplyType(TermNames.Map, MapTypes), AssociativeArgs),         Apply(Select(ApplyType(TermNames.Map, MapTypes), Name("ofEntries")), AssociativeArgs))
  )

  forAll(Scenarios) { case (scalaInitializer: Apply, javaInitializer: Apply) =>
    test(s"The Scala initializer ${scalaInitializer.toString()} should transform to the Java initializer: ${javaInitializer.toString()}") {
      transform(scalaInitializer).structure shouldBe javaInitializer.structure
    }
  }
}
