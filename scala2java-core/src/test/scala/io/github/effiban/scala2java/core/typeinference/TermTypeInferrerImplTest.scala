package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaInt, JavaString}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Enumerator.Generator
import scala.meta.Term.{Ascribe, Assign, Block, ForYield, If, Match, New}
import scala.meta.{Case, Init, Lit, Name, Pat, Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermTypeInferrerImplTest extends UnitTestSuite {

  private val ApplyArgNames = List(q"arg1", q"arg2")
  private val TheApply = Term.Apply(q"myMethod", ApplyArgNames)

  private val TheApplyType = Term.ApplyType(Term.Name("foo"), List(ScalaInt))

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

  private val TheTermFunction = q"((x: Int) => x + 1)"
  private val TheTypeFunction = t"Int => Int"


  private val applyInfixTypeInferrer = mock[ApplyInfixTypeInferrer]
  private val applyReturnTypeInferrer = mock[ApplyReturnTypeInferrer]
  private val applyTypeTypeInferrer = mock[ApplyTypeTypeInferrer]
  private val blockTypeInferrer = mock[BlockTypeInferrer]
  private val caseListTypeInferrer = mock[CaseListTypeInferrer]
  private val functionTypeInferrer = mock[FunctionTypeInferrer]
  private val ifTypeInferrer = mock[IfTypeInferrer]
  private val litTypeInferrer = mock[LitTypeInferrer]
  private val selectTypeInferrer = mock[InternalSelectTypeInferrer]
  private val superTypeInferrer = mock[SuperTypeInferrer]
  private val tryTypeInferrer = mock[TryTypeInferrer]
  private val tryWithHandlerTypeInferrer = mock[TryWithHandlerTypeInferrer]

  private val tupleTypeInferrer = mock[TupleTypeInferrer]
  private val termTypeInferrer = new TermTypeInferrerImpl(
    applyInfixTypeInferrer,
    applyReturnTypeInferrer,
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    functionTypeInferrer,
    ifTypeInferrer,
    litTypeInferrer,
    selectTypeInferrer,
    superTypeInferrer,
    tryTypeInferrer,
    tryWithHandlerTypeInferrer,
    tupleTypeInferrer
  )

  test("infer 'Apply' return type when 'ApplyTypeInferrer' returns a result, should return it") {
    when(applyReturnTypeInferrer.infer(eqTree(TheApply))).thenReturn(Some(ScalaInt))
    termTypeInferrer.infer(TheApply).value.structure shouldBe ScalaInt.structure
  }

  test("infer 'Apply' return type when 'ApplyTypeInferrer' returns None should return None") {
    when(applyReturnTypeInferrer.infer(eqTree(TheApply))).thenReturn(None)
    termTypeInferrer.infer(TheApply) shouldBe None
  }

  test("infer 'ApplyType' when 'ApplyTypeTypeInferrer' returns a result, should return it") {
    when(applyTypeTypeInferrer.infer(eqTree(TheApplyType))).thenReturn(Some(ScalaInt))
    termTypeInferrer.infer(TheApplyType).value.structure shouldBe ScalaInt.structure
  }

  test("infer 'ApplyType' when 'ApplyTypeTypeInferrer' returns None should return None") {
    when(applyTypeTypeInferrer.infer(eqTree(TheApplyType))).thenReturn(None)
    termTypeInferrer.infer(TheApplyType) shouldBe None
  }

  test("infer 'Ascribe' should return its type") {
    termTypeInferrer.infer(Ascribe(Term.Name("bla"), ScalaInt)).value.structure shouldBe ScalaInt.structure
  }

  test("infer 'Assign' should infer by type of RHS") {
    val rhs = Lit.Int(3)
    val assign = Assign(lhs = Term.Name("x"), rhs = rhs)

    when(litTypeInferrer.infer(eqTree(rhs))).thenReturn(Some(ScalaInt))

    termTypeInferrer.infer(assign).value.structure shouldBe ScalaInt.structure
  }

  test("infer 'Block' when 'BlockTypeInferrer' returns a result should return it") {
    when(blockTypeInferrer.infer(eqTree(TheBlock))).thenReturn(Some(ScalaInt))

    termTypeInferrer.infer(TheBlock).value.structure shouldBe ScalaInt.structure
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

    when(litTypeInferrer.infer(eqTree(body))).thenReturn(Some(JavaString))

    termTypeInferrer.infer(forYield).value.structure shouldBe JavaString.structure
  }

  test("infer Term.Function should return result of 'FunctionTypeInferrer'") {
    when(functionTypeInferrer.infer(eqTree(TheTermFunction))).thenReturn(TheTypeFunction)

    termTypeInferrer.infer(TheTermFunction).value.structure shouldBe TheTypeFunction.structure
  }

  test("infer 'If' when 'IfTypeInferrer' returns a result should return it") {
    when(ifTypeInferrer.infer(eqTree(TheIf))).thenReturn(Some(ScalaInt))

    termTypeInferrer.infer(TheIf).value.structure shouldBe ScalaInt.structure
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

    termTypeInferrer.infer(termInterpolate).value.structure shouldBe JavaString.structure
  }

  test("infer 'Lit' when 'LitTypeInferrer' returns a result should return it") {
    val literalInt = Lit.Int(3)

    when(litTypeInferrer.infer(eqTree(literalInt))).thenReturn(Some(ScalaInt))

    termTypeInferrer.infer(literalInt).value.structure shouldBe ScalaInt.structure
  }

  test("infer 'Lit' when 'LitTypeInferrer' returns None should return None") {
    when(litTypeInferrer.infer(eqTree(Lit.Null()))).thenReturn(None)

    termTypeInferrer.infer(Lit.Null()) shouldBe None
  }

  test("infer 'Match' should infer by its case list") {
    val `match` = Match(expr = Term.Name("x"), cases = MatchCases, mods = Nil)

    when(caseListTypeInferrer.infer(eqTreeList(MatchCases))).thenReturn(Some(ScalaInt))

    termTypeInferrer.infer(`match`).value.structure shouldBe ScalaInt.structure
  }

  test("infer 'Name' should return None") {
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

  test("infer 'Repeated' should return a scala.Array of its inferred type recursively") {
    val expr = Lit.String("abc")

    when(litTypeInferrer.infer(eqTree(expr))).thenReturn(Some(TypeSelects.JavaString))

    termTypeInferrer.infer(Term.Repeated(expr)).value.structure shouldBe Type.Apply(TypeSelects.ScalaArray, List(TypeSelects.JavaString)).structure
  }

  test("infer 'Return' should infer by its expression recursively") {
    val expr = Lit.String("abc")

    when(litTypeInferrer.infer(eqTree(expr))).thenReturn(Some(JavaString))

    termTypeInferrer.infer(Term.Return(expr)).value.structure shouldBe JavaString.structure
  }

  test("infer 'Super' should return result of SuperTypeInferrer") {
    val theSuper = q"super[X]"

    when(superTypeInferrer.infer(eqTree(theSuper))).thenReturn(Some(JavaString))

    termTypeInferrer.infer(theSuper).value.structure shouldBe JavaString.structure
  }

  test("infer 'Tuple' should return result of 'TupleTypeInferrer'") {
    val termTuple = Term.Tuple(List(Lit.String("a"), Lit.Int(1)))

    val expectedTypeTuple = Type.Tuple(List(JavaString, ScalaInt))

    when(tupleTypeInferrer.infer(eqTree(termTuple))).thenReturn(expectedTypeTuple)

    termTypeInferrer.infer(termTuple).value.structure shouldBe expectedTypeTuple.structure
  }
  // TODO complete the coverage
}
