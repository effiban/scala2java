package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Init, Name, Term, Type}

class InitListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val initArgTraverserFactory = mock[InitArgTraverserFactory]
  private val initArgTraverser = mock[InitArgumentTraverser]

  private val initListTraverser = new InitListTraverserImpl(argumentListTraverser, initArgTraverserFactory)


  test("traverse() when no inits") {
    when(initArgTraverserFactory(InitContext())).thenReturn(initArgTraverser)

    initListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(args = Nil, argTraverser = initArgTraverser, context = ArgumentListContext())
  }

  test("traverse() when two inits") {
    val init1 = Init(tpe = Type.Name("MyType1"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))
    val init2 = Init(tpe = Type.Name("MyType2"), name = Name.Anonymous(), argss = List(List(Term.Name("arg3"), Term.Name("arg4"))))

    when(initArgTraverserFactory(InitContext())).thenReturn(initArgTraverser)

    initListTraverser.traverse(List(init1, init2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init1, init2)),
      argTraverser = eqTo(initArgTraverser),
      context = eqTo(ArgumentListContext())
    )
  }
}
