package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term.Throw
import scala.meta.{Init, Name, Term, Type}

class ThrowTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]

  private val throwTraverser = new ThrowTraverserImpl(termTraverser)

  test("traverse") {
    val exception = Term.New(Init(tpe = Type.Name("IllegalStateException"), name = Name.Anonymous(), argss = Nil))
    val `throw` = Throw(exception)

    throwTraverser.traverse(`throw`)

    verify(termTraverser).traverse(eqTree(exception))
  }
}
