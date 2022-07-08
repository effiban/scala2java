package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Enumerator.Generator
import scala.meta.Mod.Annot
import scala.meta.Term.{Apply, ApplyInfix, ApplyType, Assign, Block, Eta, For, ForYield, If, NewAnonymous}
import scala.meta.{Case, Init, Lit, Name, Pat, Self, Template, Term, Type}

class TermTraverserImplTest extends UnitTestSuite {

  private val termRefTraverser = mock[TermRefTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val applyTypeTraverser = mock[ApplyTypeTraverser]
  private val termApplyInfixTraverser = mock[TermApplyInfixTraverser]
  private val assignTraverser = mock[AssignTraverser]
  private val returnTraverser = mock[ReturnTraverser]
  private val throwTraverser = mock[ThrowTraverser]
  private val ascribeTraverser = mock[AscribeTraverser]
  private val termAnnotateTraverser = mock[TermAnnotateTraverser]
  private val termTupleTraverser = mock[TermTupleTraverser]
  private val blockTraverser = mock[BlockTraverser]
  private val ifTraverser = mock[IfTraverser]
  private val termMatchTraverser = mock[TermMatchTraverser]
  private val tryTraverser = mock[TryTraverser]
  private val tryWithHandlerTraverser = mock[TryWithHandlerTraverser]
  private val termFunctionTraverser = mock[TermFunctionTraverser]
  private val partialFunctionTraverser = mock[PartialFunctionTraverser]
  private val anonymousFunctionTraverser = mock[AnonymousFunctionTraverser]
  private val whileTraverser = mock[WhileTraverser]
  private val doTraverser = mock[DoTraverser]
  private val forTraverser = mock[ForTraverser]
  private val forYieldTraverser = mock[ForYieldTraverser]
  private val newTraverser = mock[NewTraverser]
  private val newAnonymousTraverser = mock[NewAnonymousTraverser]
  private val termPlaceholderTraverser = mock[TermPlaceholderTraverser]
  private val etaTraverser = mock[EtaTraverser]
  private val termRepeatedTraverser = mock[TermRepeatedTraverser]
  private val termInterpolateTraverser = mock[TermInterpolateTraverser]
  private val litTraverser = mock[LitTraverser]


  private val termTraverser = new TermTraverserImpl(
    termRefTraverser,
    termApplyTraverser,
    applyTypeTraverser,
    termApplyInfixTraverser,
    assignTraverser,
    returnTraverser,
    throwTraverser,
    ascribeTraverser,
    termAnnotateTraverser,
    termTupleTraverser,
    blockTraverser,
    ifTraverser,
    termMatchTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    termFunctionTraverser,
    partialFunctionTraverser,
    anonymousFunctionTraverser,
    whileTraverser,
    doTraverser,
    forTraverser,
    forYieldTraverser,
    newTraverser,
    newAnonymousTraverser,
    termPlaceholderTraverser,
    etaTraverser,
    termRepeatedTraverser,
    termInterpolateTraverser,
    litTraverser
  )

  test("traverse() for Term.Name") {
    val termName = Term.Name("x")
    termTraverser.traverse(termName)
    verify(termRefTraverser).traverse(eqTree(termName))
  }

  test("traverse() for Term.Apply") {
    val termApply = Term.Apply(Term.Name("myFunc"), List(Term.Name("x"), Term.Name("y")))
    termTraverser.traverse(termApply)
    verify(termApplyTraverser).traverse(eqTree(termApply))
  }

  test("traverse() for ApplyType") {
    val applyType = ApplyType(Term.Name("myFunc"), List(Type.Name("T"), Type.Name("U")))
    termTraverser.traverse(applyType)
    verify(applyTypeTraverser).traverse(eqTree(applyType))
  }

  test("traverse() for Term.ApplyInfix") {
    val applyInfix = Term.ApplyInfix(
      lhs = Term.Name("x"),
      op = Term.Name("+"),
      targs = Nil,
      args = List(Term.Name("y"))
    )
    termTraverser.traverse(applyInfix)
    verify(termApplyInfixTraverser).traverse(eqTree(applyInfix))
  }

  test("traverse() for Assign") {
    val assign = Term.Assign(lhs = Term.Name("x"), rhs = Lit.Int(3))
    termTraverser.traverse(assign)
    verify(assignTraverser).traverse(eqTree(assign))
  }

  test("traverse() for Return") {
    val `return` = Term.Return(Term.Name("x"))
    termTraverser.traverse(`return`)
    verify(returnTraverser).traverse(eqTree(`return`))
  }

  test("traverse() for Throw") {
    val `throw` = Term.Throw(Term.Name("IllegalStateException"))
    termTraverser.traverse(`throw`)
    verify(throwTraverser).traverse(eqTree(`throw`))
  }

  test("traverse() for Ascribe") {
    val ascribe = Term.Ascribe(Term.Name("myVar"), Type.Name("T"))
    termTraverser.traverse(ascribe)
    verify(ascribeTraverser).traverse(eqTree(ascribe))
  }

  test("traverse() for Term.Annotate") {
    val annotate = Term.Annotate(
      expr = Term.Name("myName"),
      annots = List(
        Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())),
        Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))
      )
    )
    termTraverser.traverse(annotate)
    verify(termAnnotateTraverser).traverse(eqTree(annotate))
  }

  test("traverse() for Term.Tuple") {
    val tuple = Term.Tuple(List(Term.Name("x"), Term.Name("y")))
    termTraverser.traverse(tuple)
    verify(termTupleTraverser).traverse(eqTree(tuple))
  }

  test("traverse() for Block") {
    val block = Block(
      List(
        Assign(Term.Name("x"), Lit.Int(3)),
        Assign(Term.Name("y"), Lit.Int(4))
      )
    )
    termTraverser.traverse(block)
    verify(blockTraverser).traverse(
      block = eqTree(block),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )
  }

  test("traverse() for If") {
    val `if` = If(
      cond = ApplyInfix(
        lhs = Term.Name("x"),
        op = Term.Name("<"),
        targs = Nil,
        args = List(Lit.Int(3))
      ),
      thenp = Block(
        List(
          Apply(Term.Name("callOperation"), List(Term.Name("x"))),
        )
      ),
      elsep = Lit.Unit()
    )
    termTraverser.traverse(`if`)
    verify(ifTraverser).traverse(`if` = eqTree(`if`), shouldReturnValue = ArgumentMatchers.eq(false))
  }

  test("traverse() for Term.Match") {
    val expr = Term.Name("x")
    val case1 = Case(pat = Lit.Int(1), cond = None, body = Lit.String("one"))
    val case2 = Case(pat = Lit.Int(2), cond = None, body = Lit.String("two"))
    val termMatch = Term.Match(
      expr = expr,
      cases = List(case1, case2),
      mods = Nil
    )
    termTraverser.traverse(termMatch)
    verify(termMatchTraverser).traverse(eqTree(termMatch))
  }

  test("traverse() for Try") {
    val `try` = Term.Try(
      expr = Term.Apply(Term.Name("doSomething"), Nil),
      catchp = List(
        Case(
          pat = Pat.Typed(lhs = Pat.Var(Term.Name("e")), rhs = Type.Name("RuntimeException")),
          cond = None,
          body = Apply(fun = Term.Name("log"), args = List(Term.Name("e")))
        )
      ),
      finallyp = None
    )
    termTraverser.traverse(`try`)
    verify(tryTraverser).traverse(eqTree(`try`))
  }

  test("traverse() for TryWithHandler") {
    val tryWithHandler = Term.TryWithHandler(
      expr = Term.Apply(Term.Name("doSomething"), Nil),
      catchp = Term.Apply(
        fun = Term.Name("e"),
        args = List(
          Block(
            List(Term.Throw(expr = Term.Name("e")))
          )
        )
      ),
      finallyp = None
    )
    termTraverser.traverse(tryWithHandler)
    verify(tryWithHandlerTraverser).traverse(eqTree(tryWithHandler))
  }

  test("traverse() for Term.Function") {
    val function = Term.Function(
      params = List(Term.Param(mods = Nil, name = Term.Name("x"), decltpe = Some(TypeNames.Int), default = None)),
      body = Apply(fun = Term.Name("doSomething"), args = List(Term.Name("x")))
    )
    termTraverser.traverse(function)
    verify(termFunctionTraverser).traverse(eqTree(function))
  }

  test("traverse() for PartialFunction") {
    val partialFunction = Term.PartialFunction(
      List(
        Case(pat = Lit.Int(1), cond = None, body = Lit.String("one")),
        Case(pat = Lit.Int(2), cond = None, body = Lit.String("two"))
      )
    )
    termTraverser.traverse(partialFunction)
    verify(partialFunctionTraverser).traverse(eqTree(partialFunction))
  }

  test("traverse() for AnonymousFunction") {
    val anonymousFunction = Term.AnonymousFunction(Apply(Term.Name("doSomething"), Nil))
    termTraverser.traverse(anonymousFunction)
    verify(anonymousFunctionTraverser).traverse(eqTree(anonymousFunction))
  }

  test("traverse() for While") {
    val `while` = Term.While(
      expr = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = Nil, args = List(Lit.Int(3))),
      body = Term.Name("doSomething")
    )
    termTraverser.traverse(`while`)
    verify(whileTraverser).traverse(eqTree(`while`))
  }

  test("traverse() for Do") {
    val `do` = Term.Do(
      body = Term.Name("doSomething"),
      expr = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = Nil, args = List(Lit.Int(3))),
    )
    termTraverser.traverse(`do`)
    verify(doTraverser).traverse(eqTree(`do`))
  }

  test("traverse() for For") {
    val `for` = For(
      enums = List(
        Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
        Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
      ),
      body = Term.Name("result")
    )
    termTraverser.traverse(`for`)
    verify(forTraverser).traverse(eqTree(`for`))
  }

  test("traverse() for ForYield") {
    val forYield = ForYield(
      enums = List(
        Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
        Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
      ),
      body = Term.Name("result")
    )
    termTraverser.traverse(forYield)
    verify(forYieldTraverser).traverse(eqTree(forYield))
  }

  test("traverse() for New") {
    val `new` = Term.New(Init(tpe = Type.Name("MyClass"), name = Name.Anonymous(), argss = Nil))
    termTraverser.traverse(`new`)
    verify(newTraverser).traverse(eqTree(`new`))
  }

  test("traverse() for NewAnonymous") {
    val newAnonymous = NewAnonymous(
      Template(
        early = Nil,
        inits = List(Init(tpe = Type.Name("MyParent"), name = Name.Anonymous(), argss = Nil)),
        self = Self(name = Name.Anonymous(), decltpe = None),
        stats = List(Term.Apply(Term.Name("doSomething"), List(Term.Name("arg"))))
      )
    )
    termTraverser.traverse(newAnonymous)
    verify(newAnonymousTraverser).traverse(eqTree(newAnonymous))
  }

  test("traverse() for Term.Placeholder") {
    termTraverser.traverse(Term.Placeholder())
    verify(termPlaceholderTraverser).traverse(eqTree(Term.Placeholder()))
  }

  test("traverse() for Eta") {
    val eta = Eta(Term.Name("myFunc"))
    termTraverser.traverse(eta)
    verify(etaTraverser).traverse(eqTree(eta))
  }

  test("traverse() for Term.Repeated") {
    val termRepeated = Term.Repeated(Term.Name("x"))
    termTraverser.traverse(termRepeated)
    verify(termRepeatedTraverser).traverse(eqTree(termRepeated))
  }

  test("traverse() for Term.Interpolate") {
    val interpolate = Term.Interpolate(
      prefix = Term.Name("s"),
      parts = List(Lit.String("start-"), Lit.String("-end")),
      args = List(Term.Name("myVal"))
    )
    termTraverser.traverse(interpolate)
    verify(termInterpolateTraverser).traverse(eqTree(interpolate))
  }

  test("traverse() for Lit.Int") {
    val lit = Lit.Int(3)
    termTraverser.traverse(lit)
    verify(litTraverser).traverse(eqTree(lit))
  }
}