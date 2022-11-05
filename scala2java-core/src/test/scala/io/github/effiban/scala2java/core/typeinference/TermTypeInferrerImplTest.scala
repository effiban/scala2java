package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames

import scala.meta.Enumerator.Generator
import scala.meta.Term.{Ascribe, Assign, Block, ForYield, If, Match, New}
import scala.meta.{Case, Init, Lit, Name, Pat, Term, Type}

class TermTypeInferrerImplTest extends UnitTestSuite {

  private val TheApply = Term.Apply(Term.Name("myMethod"), Nil)

  private val TheApplyType = Term.ApplyType(Term.Name("foo"), List(TypeNames.Int))

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

  private val applyTypeInferrer = mock[ApplyTypeInferrer]
  private val applyTypeTypeInferrer = mock[ApplyTypeTypeInferrer]
  private val caseListTypeInferrer = mock[CaseListTypeInferrer]
  private val ifTypeInferrer = mock[IfTypeInferrer]
  private val blockTypeInferrer = mock[BlockTypeInferrer]
  private val litTypeInferrer = mock[LitTypeInferrer]
  private val nameTypeInferrer = mock[NameTypeInferrer]
  private val selectTypeInferrer = mock[SelectTypeInferrer]
  private val tryTypeInferrer = mock[TryTypeInferrer]
  private val tryWithHandlerTypeInferrer = mock[TryWithHandlerTypeInferrer]
  private val tupleTypeInferrer = mock[TupleTypeInferrer]

  private val termTypeInferrer = new TermTypeInferrerImpl(
    applyTypeInferrer,
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    ifTypeInferrer,
    litTypeInferrer,
    nameTypeInferrer,
    selectTypeInferrer,
    tryTypeInferrer,
    tryWithHandlerTypeInferrer,
    tupleTypeInferrer
  )

  test("infer 'Apply' when 'ApplyTypeInferrer returns a result, should return it") {
    when(applyTypeInferrer.infer(eqTree(TheApply))).thenReturn(Some(TypeNames.Int))
    termTypeInferrer.infer(TheApply).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'Apply' when 'ApplyTypeInferrer returns None should return None") {
    when(applyTypeInferrer.infer(eqTree(TheApply))).thenReturn(None)
    termTypeInferrer.infer(TheApply) shouldBe None
  }

  test("infer 'ApplyType' when 'ApplyTypeTypeInferrer' returns a result, should return it") {
    when(applyTypeTypeInferrer.infer(eqTree(TheApplyType))).thenReturn(Some(TypeNames.Int))
    termTypeInferrer.infer(TheApplyType).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer 'ApplyType' when 'ApplyTypeTypeInferrer' returns None should return None") {
    when(applyTypeTypeInferrer.infer(eqTree(TheApplyType))).thenReturn(None)
    termTypeInferrer.infer(TheApplyType) shouldBe None
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

  test("infer 'Name' when 'NameTypeInferrer' returns a result, should return it") {
    when(nameTypeInferrer.infer(eqTree(Term.Name("List")))).thenReturn(Some(TypeNames.List))
    termTypeInferrer.infer(Term.Name("List")).value.structure shouldBe TypeNames.List.structure
  }

  test("infer 'Name' when 'NameTypeInferrer' returns None should return None") {
    when(nameTypeInferrer.infer(eqTree(Term.Name("foo")))).thenReturn(None)
    termTypeInferrer.infer(Term.Name("foo")) shouldBe None
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

  test("infer 'Tuple' should return result of 'TupleTypeInferrer'") {
    val termTuple = Term.Tuple(List(Lit.String("a"), Lit.Int(1)))

    val expectedTypeTuple = Type.Tuple(List(TypeNames.String, TypeNames.Int))

    when(tupleTypeInferrer.infer(eqTree(termTuple))).thenReturn(expectedTypeTuple)

    termTypeInferrer.infer(termTuple).value.structure shouldBe expectedTypeTuple.structure
  }
  // TODO complete the coverage
}
