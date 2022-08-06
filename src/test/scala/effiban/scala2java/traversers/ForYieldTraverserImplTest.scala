package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.ForYieldToTermApplyTransformer

import scala.meta.Enumerator.Generator
import scala.meta.Term.{ForYield, Select}
import scala.meta.{Pat, Term}

class ForYieldTraverserImplTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val Y = Term.Name("y")

  private val Xs = Term.Name("xs")
  private val Ys = Term.Name("ys")

  private val PatX = Pat.Var(X)
  private val PatY = Pat.Var(Y)

  private val ParamX = paramOf(X)
  private val ParamY = paramOf(Y)

  private val termApplyTraverser = mock[TermApplyTraverser]
  private val forYieldToTermApplyTransformer = mock[ForYieldToTermApplyTransformer]

  private val forYieldTraverser = new ForYieldTraverserImpl(termApplyTraverser, forYieldToTermApplyTransformer)

  test("transform()") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
      Generator(pat = PatY, rhs = Ys)
    )

    val body = Term.Name("result")

    val forYield = ForYield(enums = enumerators, body = body)

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, Term.Name("flatMap")),
        args = List(Term.Function(
          params = List(ParamX),
          body = Term.Apply(
            fun = Select(Ys, Term.Name("map")),
            args = List(Term.Function(
              params = List(ParamY),
              body = body
            ))
          )
        ))
      )

    when(forYieldToTermApplyTransformer.transform(eqTreeList(enumerators), eqTree(body))).thenReturn(expectedTermApply)

    forYieldTraverser.traverse(forYield)

    verify(termApplyTraverser).traverse(eqTree(expectedTermApply))
  }

  private def paramOf(termName: Term.Name) = {
    Term.Param(mods = List.empty, name = termName, decltpe = None, default = None)
  }
}
