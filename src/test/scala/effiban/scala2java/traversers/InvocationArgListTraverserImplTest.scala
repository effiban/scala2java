package effiban.scala2java.traversers

import effiban.scala2java.contexts.{InvocationArgContext, InvocationArgListContext}
import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.{ArgumentCaptor, ArgumentMatchers}

import scala.meta.Term

class InvocationArgListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val invocationArgTraverser = mock[InvocationArgTraverser]
  private val invocationArgTraverserCaptor: ArgumentCaptor[ScalaTreeTraverser[Term]] = ArgumentCaptor.forClass(classOf[ScalaTreeTraverser[Term]])

  private val invocationArgListTraverser = new InvocationArgListTraverserImpl(argumentListTraverser, invocationArgTraverser)


  test("traverse() when no args") {
    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))

    invocationArgListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = invocationArgTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(expectedOptions)
    )

    verifyNoMoreInteractions(invocationArgTraverser)
  }

  test("traverse() when one arg and the default context") {
    val arg = Term.Name("x")

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedInvocationArgContext = InvocationArgContext()

    invocationArgListTraverser.traverse(args = List(arg))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(arg)),
      argTraverser = invocationArgTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(expectedOptions)
    )

    invocationArgTraverserCaptor.getValue.traverse(arg)
    verify(invocationArgTraverser).traverse(eqTree(arg), ArgumentMatchers.eq(expectedInvocationArgContext))
  }

  test("traverse() when one arg, on same line and the rest default") {
    val arg = Term.Name("x")
    val invocationArgListContext = InvocationArgListContext(onSameLine = true)

    val expectedOptions = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
    val expectedInvocationArgContext = InvocationArgContext()

    invocationArgListTraverser.traverse(args = List(arg), context = invocationArgListContext)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(arg)),
      argTraverser = invocationArgTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(expectedOptions)
    )

    invocationArgTraverserCaptor.getValue.traverse(arg)
    verify(invocationArgTraverser).traverse(eqTree(arg), ArgumentMatchers.eq(expectedInvocationArgContext))
  }

  test("traverse() when one arg, argNameAsComment=true, and the rest default") {
    val arg = Term.Name("x")
    val invocationArgListContext = InvocationArgListContext(argNameAsComment = true)

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedInvocationArgContext = InvocationArgContext(argNameAsComment = true)

    invocationArgListTraverser.traverse(args = List(arg), context = invocationArgListContext)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(arg)),
      argTraverser = invocationArgTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(expectedOptions)
    )

    invocationArgTraverserCaptor.getValue.traverse(arg)
    verify(invocationArgTraverser).traverse(eqTree(arg), ArgumentMatchers.eq(expectedInvocationArgContext))
  }

  test("traverse() when two args and default context") {
    val arg1 = Term.Name("x")
    val arg2 = Term.Name("y")

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedInvocationArgContext = InvocationArgContext()

    invocationArgListTraverser.traverse(args = List(arg1, arg2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(arg1, arg2)),
      argTraverser = invocationArgTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(expectedOptions)
    )

    invocationArgTraverserCaptor.getValue.traverse(arg1)
    verify(invocationArgTraverser).traverse(eqTree(arg1), ArgumentMatchers.eq(expectedInvocationArgContext))
  }

  test("traverse() when two args, on same line and the rest default") {
    val arg1 = Term.Name("x")
    val arg2 = Term.Name("y")
    val invocationArgListContext = InvocationArgListContext(onSameLine = true)

    val expectedOptions = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
    val expectedInvocationArgContext = InvocationArgContext()

    invocationArgListTraverser.traverse(args = List(arg1, arg2), context = invocationArgListContext)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(arg1, arg2)),
      argTraverser = invocationArgTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(expectedOptions)
    )

    invocationArgTraverserCaptor.getValue.traverse(arg1)
    verify(invocationArgTraverser).traverse(eqTree(arg1), ArgumentMatchers.eq(expectedInvocationArgContext))
  }
}
