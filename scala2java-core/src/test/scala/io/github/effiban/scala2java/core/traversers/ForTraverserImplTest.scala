package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.ForToTermApplyTransformer

import scala.meta.Enumerator.Generator
import scala.meta.Term.{For, Select}
import scala.meta.{Pat, Term}

class ForTraverserImplTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val Y = Term.Name("y")

  private val Xs = Term.Name("xs")
  private val Ys = Term.Name("ys")

  private val PatX = Pat.Var(X)
  private val PatY = Pat.Var(Y)

  private val ParamX = paramOf(X)
  private val ParamY = paramOf(Y)

  private val ForEachFunctionName = Term.Name("forEach")

  private val termApplyTraverser = mock[TermApplyTraverser]
  private val forToTermApplyTransformer = mock[ForToTermApplyTransformer]

  private val forTraverser = new ForTraverserImpl(termApplyTraverser, forToTermApplyTransformer)


  test("transform()") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
      Generator(pat = PatY, rhs = Ys)
    )

    val body = Term.Name("result")

    val `for` = For(enums = enumerators, body = body)

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, ForEachFunctionName),
        args = List(Term.Function(
          params = List(ParamX),
          body = Term.Apply(
            fun = Select(Ys, ForEachFunctionName),
            args = List(Term.Function(
              params = List(ParamY),
              body = body
            ))
          )
        ))
      )

    when(forToTermApplyTransformer.transform(eqTreeList(enumerators), eqTree(body))).thenReturn(expectedTermApply)

    forTraverser.traverse(`for`)

    verify(termApplyTraverser).traverse(eqTree(expectedTermApply))
  }

  private def paramOf(termName: Term.Name) = {
    Term.Param(mods = List.empty, name = termName, decltpe = None, default = None)
  }
}
