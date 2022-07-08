package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Enumerator.{CaseGenerator, Generator}
import scala.meta.Term.Select
import scala.meta.{Enumerator, Pat, Term}

class ForVariantTraverserImplTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val Y = Term.Name("y")
  private val Z = Term.Name("z")

  private val Xs = Term.Name("xs")
  private val Ys = Term.Name("ys")
  private val Zs = Term.Name("zs")

  private val ParamX = paramOf(X)
  private val ParamY = paramOf(Y)
  private val ParamZ = paramOf(Z)

  private val PatX = Pat.Var(X)
  private val PatY = Pat.Var(Y)
  private val PatZ = Pat.Var(Z)

  private val MapFunctionName = Term.Name("map")
  private val FlatMapFunctionName = Term.Name("flatMap")
  private val ResultFunctionName = Term.Name("result")

  private val termTraverser = mock[TermTraverser]


  private val forVariantTraverser = new ForVariantTraverserImpl(termTraverser)
  test("traverse() for one Generator with a variable in the LHS") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTranslatedFor =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    forVariantTraverser.traverse(enumerators, inputBody, MapFunctionName)

    verify(termTraverser).traverse(eqTree(expectedTranslatedFor))
  }

  test("traverse() for one Generator enumerator with a wildcard in the LHS") {
    val enumerators = List(
      Generator(pat = Pat.Wildcard(), rhs = Xs)
    )
    val inputBody = Term.Apply(ResultFunctionName, Nil)

    val expectedTranslatedFor =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(paramOf(Term.Name(JavaPlaceholder))), body = inputBody))
      )

    forVariantTraverser.traverse(enumerators, inputBody, MapFunctionName)

    verify(termTraverser).traverse(eqTree(expectedTranslatedFor))
  }

  test("traverse() for one CaseGenerator enumerator with a variable in the LHS") {
    val enumerators = List(
      CaseGenerator(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTranslatedFor =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    forVariantTraverser.traverse(enumerators, inputBody, MapFunctionName)

    verify(termTraverser).traverse(eqTree(expectedTranslatedFor))
  }

  test("traverse() for one Val enumerator with a variable in the LHS") {
    val enumerators = List(
      Enumerator.Val(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTranslatedFor =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    forVariantTraverser.traverse(enumerators, inputBody, MapFunctionName)

    verify(termTraverser).traverse(eqTree(expectedTranslatedFor))
  }

  test("traverse() for three enumerators") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
      Generator(pat = PatY, rhs = Ys),
      Generator(pat = PatZ, rhs = Zs)
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X, Y, Z))

    // xs.flatMap(x => ys.flatMap(y => zs.map(z => result(x, y, z))))
    val expectedTranslatedFor =
      Term.Apply(
        fun = Select(Xs, FlatMapFunctionName),
        args = List(Term.Function(
          params = List(ParamX),
          body = Term.Apply(
            fun = Select(Ys, FlatMapFunctionName),
            args = List(Term.Function(
              params = List(ParamY),
              body = Term.Apply(
                fun = Select(Zs, MapFunctionName),
                args = List(Term.Function(
                  params = List(ParamZ),
                  body = inputBody
                ))
              )
            ))
          )
        ))
      )

    forVariantTraverser.traverse(enumerators, inputBody, MapFunctionName)

    verify(termTraverser).traverse(eqTree(expectedTranslatedFor))
  }

  private def paramOf(termName: Term.Name) = {
    Term.Param(mods = List.empty, name = termName, decltpe = None, default = None)
  }
}
