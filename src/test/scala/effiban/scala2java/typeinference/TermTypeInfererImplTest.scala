package effiban.scala2java.typeinference

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Term.{Ascribe, New}
import scala.meta.{Init, Lit, Name, Term, Type}

class TermTypeInfererImplTest extends UnitTestSuite {

  private val litTypeInferer = mock[LitTypeInferer]

  private val termTypeInferer = new TermTypeInfererImpl(litTypeInferer)

  test("infer 'Lit' when 'LitTypeInferer' returns a result should return it") {
    val literalInt = Lit.Int(3)

    when(litTypeInferer.infer(eqTree(literalInt))).thenReturn(Some(TypeNames.Int))

    termTypeInferer.infer(literalInt).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'Lit' when 'LitTypeInferer' returns None should return None") {
    when(litTypeInferer.infer(eqTree(Lit.Null()))).thenReturn(None)

    termTypeInferer.infer(Lit.Null()) shouldBe None
  }

  test("infer 'Return' should infer by its expression recursively") {
    val expr = Lit.String("abc")

    when(litTypeInferer.infer(eqTree(expr))).thenReturn(Some(TypeNames.String))

    termTypeInferer.infer(Term.Return(expr)).value.structure shouldBe TypeNames.String.structure
  }

  test("infer 'Ascribe' should return its type") {
    termTypeInferer.infer(Ascribe(Term.Name("bla"), TypeNames.Int)).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'New' should return type of its 'Init'") {
    val myClassTypeName = Type.Name("MyClass")
    val myClassInit = Init(
      tpe = myClassTypeName,
      name = Name.Anonymous(),
      argss = List(List(Term.Name("val1"), Term.Name("val2")))
    )

    termTypeInferer.infer(New(myClassInit)).value.structure shouldBe myClassTypeName.structure
  }

  test("infer 'Repeated' should return Array of its inferred type recursively") {
    val expr = Lit.String("abc")

    when(litTypeInferer.infer(eqTree(expr))).thenReturn(Some(TypeNames.String))

    termTypeInferer.infer(Term.Repeated(expr)).value.structure shouldBe Type.Apply(Type.Name("Array"), List(TypeNames.String)).structure
  }

  test("infer 'Term.Interpolate' should return String") {
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("s"),
      parts = List(Lit.String("start-"), Lit.String("-end")),
      args = List(Term.Name("myArg"))
    )

    termTypeInferer.infer(termInterpolate).value.structure shouldBe TypeNames.String.structure
  }

  test("infer 'Term.Apply' should return None") {
    termTypeInferer.infer(Term.Apply(Term.Name("myMethod"), Nil)) shouldBe None
  }
}
