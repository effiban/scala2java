package effiban.scala2java.transformers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any

import scala.meta.Enumerator.{CaseGenerator, Generator}
import scala.meta.Term.Select
import scala.meta.{Enumerator, Pat, Term}

class ForVariantToTermApplyTransformerTest extends UnitTestSuite {

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

  private val patToTermParamTransformer = mock[PatToTermParamTransformer]

  private val forVariantToTermApplyTransformer = new ForVariantToTermApplyTransformer {

    override val patToTermParamTransformer: PatToTermParamTransformer = ForVariantToTermApplyTransformerTest.this.patToTermParamTransformer

    override val intermediateFunctionName: Term.Name = FlatMapFunctionName
    override val finalFunctionName: Term.Name = MapFunctionName
  }

  test("transform() for one Generator enumerator") {
    val enumerators = List(
      Generator(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    when(patToTermParamTransformer.transform(eqTree(PatX), ArgumentMatchers.eq(None))).thenReturn(Some(ParamX))

    val actualTermApply = forVariantToTermApplyTransformer.transform(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure
  }

  test("transform() for one CaseGenerator enumerator") {
    val enumerators = List(
      CaseGenerator(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    when(patToTermParamTransformer.transform(eqTree(PatX), ArgumentMatchers.eq(None))).thenReturn(Some(ParamX))

    val actualTermApply = forVariantToTermApplyTransformer.transform(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure
  }

  test("transform() for one Val enumerator") {
    val enumerators = List(
      Enumerator.Val(pat = PatX, rhs = Xs),
    )
    val inputBody = Term.Apply(ResultFunctionName, List(X))

    val expectedTermApply =
      Term.Apply(
        fun = Select(Xs, MapFunctionName),
        args = List(Term.Function(params = List(ParamX), body = inputBody))
      )

    when(patToTermParamTransformer.transform(eqTree(PatX), ArgumentMatchers.eq(None))).thenReturn(Some(ParamX))

    val actualTermApply = forVariantToTermApplyTransformer.transform(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure

  }

  test("transform() for three enumerators") {
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

    when(patToTermParamTransformer.transform(any[Pat], ArgumentMatchers.eq(None)))
      .thenAnswer((pat: Pat) => {
        pat match {
          case aPat if aPat.structure == PatX.structure => Some(ParamX)
          case aPat if aPat.structure == PatY.structure => Some(ParamY)
          case aPat if aPat.structure == PatZ.structure => Some(ParamZ)
        }
      })

    val actualTermApply = forVariantToTermApplyTransformer.transform(enumerators, inputBody)

    actualTermApply.structure shouldBe expectedTermApply.structure
  }

  private def paramOf(termName: Term.Name) = {
    Term.Param(mods = List.empty, name = termName, decltpe = None, default = None)
  }
}
