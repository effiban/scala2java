package effiban.scala2java.typeinference

import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Enumerator.Generator
import scala.meta.Term.{Ascribe, Assign, Block, ForYield, If, Match, New}
import scala.meta.{Case, Init, Lit, Name, Pat, Term, Type}

class TermTypeInferrerImplTest extends UnitTestSuite {

  private val Condition = Term.ApplyInfix(
    lhs = Term.Name("x"),
    op = Term.Name("<"),
    targs = Nil,
    args = List(Lit.Int(3))
  )

  private val Thenp = Lit.String("abc")

  private val TheIf = If(cond = Condition, thenp = Thenp, elsep = Lit.Unit())

  private val TheBlock = Block(List(Term.Apply(Term.Name("doSomething"), Nil)))

  private val MatchCase1 = Case(
    pat = Lit.String("one"),
    cond = None,
    body = Lit.Int(1)
  )
  private val MatchCase2 = Case(
    pat = Lit.String("two"),
    cond = None,
    body = Lit.Int(2)
  )
  private val MatchCases = List(MatchCase1, MatchCase2)

  private val applyTypeTypeInferrer = mock[ApplyTypeTypeInferrer]
  private val caseListTypeInferrer = mock[CaseListTypeInferrer]
  private val ifTypeInferrer = mock[IfTypeInferrer]
  private val blockTypeInferrer = mock[BlockTypeInferrer]
  private val litTypeInferrer = mock[LitTypeInferrer]
  private val tryTypeInferrer = mock[TryTypeInferrer]
  private val tryWithHandlerTypeInferrer = mock[TryWithHandlerTypeInferrer]

  private val termTypeInferrer = new TermTypeInferrerImpl(
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    ifTypeInferrer,
    litTypeInferrer,
    tryTypeInferrer,
    tryWithHandlerTypeInferrer
  )

  test("infer 'Apply' should return None") {
    termTypeInferrer.infer(Term.Apply(Term.Name("myMethod"), Nil)) shouldBe None
  }

  test("infer 'Ascribe' should return its type") {
    termTypeInferrer.infer(Ascribe(Term.Name("bla"), TypeNames.Int)).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'Assign' should infer by type of RHS") {
    val rhs = Lit.Int(3)
    val assign = Assign(lhs = Term.Name("x"), rhs = rhs)

    when(litTypeInferrer.infer(eqTree(rhs))).thenReturn(Some(TypeNames.Int))

    termTypeInferrer.infer(assign).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'Block' when 'BlockTypeInferrer' returns a result should return it") {
    when(blockTypeInferrer.infer(eqTree(TheBlock))).thenReturn(Some(TypeNames.Int))

    termTypeInferrer.infer(TheBlock).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'Block' when 'BlockTypeInferrer' returns None should return None") {
    when(blockTypeInferrer.infer(eqTree(TheBlock))).thenReturn(None)

    termTypeInferrer.infer(TheBlock) shouldBe None
  }

  test("infer 'For.Yield' should return type of its body") {
    val body = Lit.String("abc")
    val forYield = ForYield(
      enums = List(Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs"))),
      body = body
    )

    when(litTypeInferrer.infer(eqTree(body))).thenReturn(Some(TypeNames.String))

    termTypeInferrer.infer(forYield).value.structure shouldBe TypeNames.String.structure
  }

  test("infer 'If' when 'IfTypeInferrer' returns a result should return it") {
    when(ifTypeInferrer.infer(eqTree(TheIf))).thenReturn(Some(TypeNames.Int))

    termTypeInferrer.infer(TheIf).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'If' when 'IfTypeInferrer' returns None should return None") {
    when(ifTypeInferrer.infer(eqTree(TheIf))).thenReturn(None)

    termTypeInferrer.infer(TheIf) shouldBe None
  }

  test("infer 'Interpolate' should return String") {
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("s"),
      parts = List(Lit.String("start-"), Lit.String("-end")),
      args = List(Term.Name("myArg"))
    )

    termTypeInferrer.infer(termInterpolate).value.structure shouldBe TypeNames.String.structure
  }

  test("infer 'Lit' when 'LitTypeInferrer' returns a result should return it") {
    val literalInt = Lit.Int(3)

    when(litTypeInferrer.infer(eqTree(literalInt))).thenReturn(Some(TypeNames.Int))

    termTypeInferrer.infer(literalInt).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'Lit' when 'LitTypeInferrer' returns None should return None") {
    when(litTypeInferrer.infer(eqTree(Lit.Null()))).thenReturn(None)

    termTypeInferrer.infer(Lit.Null()) shouldBe None
  }

  test("infer 'Match' should infer by its case list") {
    val `match` = Match(expr = Term.Name("x"), cases = MatchCases, mods = Nil)

    when(caseListTypeInferrer.infer(eqTreeList(MatchCases))).thenReturn(Some(TypeNames.Int))

    termTypeInferrer.infer(`match`).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'New' should return type of its 'Init'") {
    val myClassTypeName = Type.Name("MyClass")
    val myClassInit = Init(
      tpe = myClassTypeName,
      name = Name.Anonymous(),
      argss = List(List(Term.Name("val1"), Term.Name("val2")))
    )

    termTypeInferrer.infer(New(myClassInit)).value.structure shouldBe myClassTypeName.structure
  }

  test("infer 'Repeated' should return Array of its inferred type recursively") {
    val expr = Lit.String("abc")

    when(litTypeInferrer.infer(eqTree(expr))).thenReturn(Some(TypeNames.String))

    termTypeInferrer.infer(Term.Repeated(expr)).value.structure shouldBe Type.Apply(Type.Name("Array"), List(TypeNames.String)).structure
  }

  test("infer 'Return' should infer by its expression recursively") {
    val expr = Lit.String("abc")

    when(litTypeInferrer.infer(eqTree(expr))).thenReturn(Some(TypeNames.String))

    termTypeInferrer.infer(Term.Return(expr)).value.structure shouldBe TypeNames.String.structure
  }

  // TODO complete the coverage
}
