package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.Enumerator.{CaseGenerator, Generator}
import scala.meta.Term.Select
import scala.meta.{Enumerator, Pat, Term}

class ForVariantDesugarerTest extends UnitTestSuite {

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

  private val patToTermParamDesugarer = mock[PatToTermParamDesugarer]

  private val forVariantDesugarer = new ForVariantDesugarer {

    override val patToTermParamDesugarer: PatToTermParamDesugarer = ForVariantDesugarerTest.this.patToTermParamDesugarer

    override val intermediateFunctionName: Term.Name = FlatMapFunctionName
    override val finalFunctionName: Term.Name = MapFunctionName
  }

  test("desugar() for one Generator enumerator") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    when(patToTermParamDesugarer.desugar(eqTree(PatX))).thenReturn(Some(ParamX))

    val actualTermApply = forVariantDesugarer.desugar(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure
  }

  test("desugar() for one CaseGenerator enumerator") {
    val enumerators = List(
      CaseGenerator(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    when(patToTermParamDesugarer.desugar(eqTree(PatX))).thenReturn(Some(ParamX))

    val actualTermApply = forVariantDesugarer.desugar(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure
  }

  test("desugar() for one Val enumerator") {
    val enumerators = List(
      Enumerator.Val(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    when(patToTermParamDesugarer.desugar(eqTree(PatX))).thenReturn(Some(ParamX))

    val actualTermApply = forVariantDesugarer.desugar(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure

  }

  test("desugar() for three enumerators") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
      Generator(pat = PatY, rhs = Ys),
      Generator(pat = PatZ, rhs = Zs)
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X, Y, Z))

    // xs.flatMap(x => ys.flatMap(y => zs.map(z => result(x, y, z))))
    val expectedTermApply =
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

    when(patToTermParamDesugarer.desugar(any[Pat]))
      .thenAnswer((pat: Pat) => {
        pat match {
          case aPat if aPat.structure == PatX.structure => Some(ParamX)
          case aPat if aPat.structure == PatY.structure => Some(ParamY)
          case aPat if aPat.structure == PatZ.structure => Some(ParamZ)
        }
      })

    val actualTermApply = forVariantDesugarer.desugar(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure
  }

  private def paramOf(termName: Term.Name) = {
    Term.Param(mods = List.empty, name = termName, decltpe = None, default = None)
  }
}
