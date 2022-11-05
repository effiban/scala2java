package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.{ArgumentCaptor, ArgumentMatchers}

import scala.meta.{Init, Name, Term, Type}

class InitListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val initTraverser = mock[InitTraverser]
  private val initTraverserCaptor: ArgumentCaptor[ScalaTreeTraverser[Init]] = ArgumentCaptor.forClass(classOf[ScalaTreeTraverser[Init]])

  private val initListTraverser = new InitListTraverserImpl(argumentListTraverser, initTraverser)


  test("traverse() when no inits") {
    initListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = initTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )

    verifyNoMoreInteractions(initTraverser)
  }

  test("traverse() when one init") {
    val init = Init(tpe = Type.Name("MyType"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))

    initListTraverser.traverse(List(init))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init)),
      argTraverser = initTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )

    initTraverserCaptor.getValue.traverse(init)
    verify(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(InitContext()))
  }

  test("traverse() when one init and args ignored") {
    val init = Init(tpe = Type.Name("MyType"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))
    val context = InitContext(ignoreArgs = true)

    initListTraverser.traverse(List(init), context)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init)),
      argTraverser = initTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )

    initTraverserCaptor.getValue.traverse(init)
    verify(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(context))
  }

  test("traverse() when two inits") {
    val init1 = Init(tpe = Type.Name("MyType1"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))
    val init2 = Init(tpe = Type.Name("MyType2"), name = Name.Anonymous(), argss = List(List(Term.Name("arg3"), Term.Name("arg4"))))

    initListTraverser.traverse(List(init1, init2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init1, init2)),
      argTraverser = initTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )

    initTraverserCaptor.getValue.traverse(init1)
    verify(initTraverser).traverse(eqTree(init1), ArgumentMatchers.eq(InitContext()))
  }
}
