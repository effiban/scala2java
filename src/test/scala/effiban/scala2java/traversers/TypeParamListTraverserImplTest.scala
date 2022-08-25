package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter._
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeBounds
import org.mockito.ArgumentMatchers

import scala.meta.Type

class TypeParamListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val typeParamTraverser = mock[TypeParamTraverser]

  private val typeParamListTraverser = new TypeParamListTraverserImpl(argumentListTraverser, typeParamTraverser)


  test("traverse() when no params") {
    typeParamListTraverser.traverse(Nil)

    outputWriter.toString shouldBe ""

    verifyNoMoreInteractions(argumentListTraverser)
  }

  test("traverse() when one param") {

    val param = typeParam("T")

    typeParamListTraverser.traverse(typeParams = List(param))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param)),
      argTraverser = ArgumentMatchers.eq(typeParamTraverser),
      onSameLine = ArgumentMatchers.eq(true),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(Some(AngleBracket))
    )
  }

  test("traverse() when two params") {
    val param1 = typeParam("T1")
    val param2 = typeParam("T2")
    val params = List(param1, param2)

    typeParamListTraverser.traverse(typeParams = params)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(params),
      argTraverser = ArgumentMatchers.eq(typeParamTraverser),
      onSameLine = ArgumentMatchers.eq(true),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(Some(AngleBracket))
    )
  }

  private def typeParam(name: String) = {
    Type.Param(
      mods = Nil,
      name = Type.Name(name),
      tparams = Nil,
      tbounds = TypeBounds.Empty,
      vbounds = Nil,
      cbounds = Nil
    )
  }
}
