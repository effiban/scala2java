package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter._
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Type

class TypeListTraverserImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(AngleBracket)
  )

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val typeTraverser = mock[TypeTraverser]

  private val typeListTraverser = new TypeListTraverserImpl(argumentListTraverser, typeTraverser)


  test("traverse() when no types") {
    typeListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = ArgumentMatchers.eq(typeTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() when one type") {

    val tpe = Type.Name("x")

    typeListTraverser.traverse(types = List(tpe))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(tpe)),
      argTraverser = ArgumentMatchers.eq(typeTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() when two types") {
    val type1 = Type.Name("x")
    val type2 = Type.Name("y")

    typeListTraverser.traverse(types = List(type1, type2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(type1, type2)),
      argTraverser = ArgumentMatchers.eq(typeTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }
}
