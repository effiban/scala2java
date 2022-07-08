package effiban.scala2java

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Name, Term, Type}

class InitListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val initTraverser = mock[InitTraverser]

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
      argTraverser = ArgumentMatchers.eq(initTraverser),
      onSameLine = ArgumentMatchers.eq(false),
      maybeDelimiterType = ArgumentMatchers.eq(None)
    )
  }

  test("traverse() when two inits") {
    val init1 = Init(tpe = Type.Name("MyType1"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))
    val init2 = Init(tpe = Type.Name("MyType2"), name = Name.Anonymous(), argss = List(List(Term.Name("arg3"), Term.Name("arg4"))))

    initListTraverser.traverse(List(init1, init2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(init1, init2)),
      argTraverser = ArgumentMatchers.eq(initTraverser),
      onSameLine = ArgumentMatchers.eq(false),
      maybeDelimiterType = ArgumentMatchers.eq(None)
    )
  }
}
