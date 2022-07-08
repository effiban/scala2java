package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Pat, Term}

class PatListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val patTraverser = mock[PatTraverser]

  private val patListTraverser = new PatListTraverserImpl(argumentListTraverser, patTraverser)


  test("traverse() when no pats") {
    patListTraverser.traverse(Nil)

    outputWriter.toString shouldBe ""

    verifyNoMoreInteractions(argumentListTraverser)
  }

  test("traverse() when one pat") {

    val pat = Pat.Var(Term.Name("x"))

    patListTraverser.traverse(List(pat))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(pat)),
      argTraverser = ArgumentMatchers.eq(patTraverser),
      onSameLine = ArgumentMatchers.eq(true),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(None)
    )
  }

  test("traverse() when two pats") {
    val pat1 = Pat.Var(Term.Name("x"))
    val pat2 = Pat.Var(Term.Name("y"))

    patListTraverser.traverse(List(pat1, pat2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(pat1, pat2)),
      argTraverser = ArgumentMatchers.eq(patTraverser),
      onSameLine = ArgumentMatchers.eq(true),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(None)
    )
  }
}
