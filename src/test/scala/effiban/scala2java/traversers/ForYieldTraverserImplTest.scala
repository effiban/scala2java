package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.PatToTermParamTransformer
import org.mockito.ArgumentMatchers.any

import scala.meta.Enumerator.Generator
import scala.meta.Term.ForYield
import scala.meta.{Pat, Term}

class ForYieldTraverserImplTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val Y = Term.Name("y")

  private val PatX = Pat.Var(X)
  private val PatY = Pat.Var(Y)

  private val ParamX = paramOf(X)
  private val ParamY = paramOf(Y)

  private val termTraverser = mock[TermTraverser]
  private val patToTermParamTransformer = mock[PatToTermParamTransformer]

  private val forYieldTraverser = spy(new ForYieldTraverserImpl(termTraverser, patToTermParamTransformer))

  test("traverse") {
    val enumerators = List(
      Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
      Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
    )

    val body = Term.Name("result")

    val forYield = ForYield(
      enums = enumerators,
      body = body
    )

    when(patToTermParamTransformer.transform(any[Pat]))
      .thenAnswer((pat: Pat) => {
        pat match {
          case aPat if aPat.structure == PatX.structure => Some(ParamX)
          case aPat if aPat.structure == PatY.structure => Some(ParamY)
        }
      })

    forYieldTraverser.traverse(forYield)

    verify(forYieldTraverser).traverse(
      enumerators = eqTreeList(enumerators),
      body = eqTree(body)
    )
  }

  private def paramOf(termName: Term.Name) = {
    Term.Param(mods = List.empty, name = termName, decltpe = None, default = None)
  }
}
