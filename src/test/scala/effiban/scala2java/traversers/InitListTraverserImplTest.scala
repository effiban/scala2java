package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.{ArgumentCaptor, ArgumentMatchers}

import scala.meta.{Init, Name, Term, Type}

class InitListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val initTraverser = mock[InitTraverser]
  private val initTraverserCaptor: ArgumentCaptor[ScalaTreeTraverser[Init]] = ArgumentCaptor.forClass(classOf[ScalaTreeTraverser[Init]])

  private val initListTraverser = new InitListTraverserImpl(argumentListTraverser, initTraverser)


  test("traverse() when no inits") {
    initListTraverser.traverse(Nil)

    outputWriter.toString shouldBe ""

    verifyNoMoreInteractions(argumentListTraverser)
  }

  test("traverse() when one init") {
    val init = Init(tpe = Type.Name("MyType"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))

    initListTraverser.traverse(List(init))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init)),
      argTraverser = initTraverserCaptor.capture(),
      onSameLine = ArgumentMatchers.eq(false),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(None)
    )

    initTraverserCaptor.getValue.traverse(init)
    verify(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(false))
  }

  test("traverse() when two inits") {
    val init1 = Init(tpe = Type.Name("MyType1"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))
    val init2 = Init(tpe = Type.Name("MyType2"), name = Name.Anonymous(), argss = List(List(Term.Name("arg3"), Term.Name("arg4"))))

    initListTraverser.traverse(List(init1, init2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init1, init2)),
      argTraverser = initTraverserCaptor.capture(),
      onSameLine = ArgumentMatchers.eq(false),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(None)
    )

    initTraverserCaptor.getValue.traverse(init1)
    verify(initTraverser).traverse(eqTree(init1), ArgumentMatchers.eq(false))
  }
}
