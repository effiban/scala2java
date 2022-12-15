package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.contexts.{StatContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Term.{Return, TryWithHandler}
import scala.meta.{Defn, Lit, Term}

class BlockStatTraverserImplTest extends UnitTestSuite {

  private val TheTermApply = Term.Apply(fun = Term.Name("foo"), args = Nil)
  private val TheTermApplyStr = "foo1()"

  private val TheIf = Term.If(
    cond = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
    thenp = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
    elsep = Lit.Unit()
  )
  private val TheIfStr =
    """if (x < 3) {
      |    doSomething();
      |}""".stripMargin

  private val TheTry = Term.Try(
    expr = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
    catchp = Nil,
    finallyp = None
  )

  private val TheTryWithHandler = TryWithHandler(
    expr = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
    catchp = Lit.Unit(),
    finallyp = None
  )

  private val TheTryStr =
    """try {
      |    doSomething();
      |}""".stripMargin

  private val TheDefnDef = Defn.Def(
    mods = Nil,
    name = Term.Name("foo"),
    tparams = Nil,
    paramss = List(List(termParamXInt())),
    decltpe = Some(TypeNames.Unit),
    body = Term.Apply(fun = Term.Name("print"), args = List(Term.Name("x")))
  )
  private val TheDefnDefStr =
    """void foo(int x) {
      |   print(x);
      |}
      |""".stripMargin

  private val BlockStatContext = StatContext(JavaScope.Block)


  private val ifTraverser = mock[IfTraverser]
  private val tryTraverser = mock[TryTraverser]
  private val tryWithHandlerTraverser = mock[TryWithHandlerTraverser]
  private val statTraverser = mock[StatTraverser]
  private val shouldReturnValueResolver = mock[ShouldReturnValueResolver]
  private val javaTermClassifier = mock[JavaStatClassifier]

  private val blockStatTraverser = new BlockStatTraverserImpl(
    ifTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    statTraverser,
    shouldReturnValueResolver,
    javaTermClassifier)


  test("traverse() when statement end required") {
    doWrite(TheTermApplyStr).when(statTraverser).traverse(eqTree(TheTermApply), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(TheTermApply)).thenReturn(true)

    blockStatTraverser.traverse(TheTermApply)

    outputWriter.toString shouldBe
      s"""$TheTermApplyStr;
         |""".stripMargin
  }

  test("traverse() when statement end not required") {
    doWrite(TheTermApplyStr).when(statTraverser).traverse(eqTree(TheTermApply), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(TheTermApply)).thenReturn(false)

    blockStatTraverser.traverse(TheTermApply)

    outputWriter.toString shouldBe TheTermApplyStr
  }

  test("traverseLast() for an 'if' when shouldReturnValue=Yes") {
    doWrite(TheIfStr).when(ifTraverser).traverse(eqTree(TheIf), ArgumentMatchers.eq(Yes))

    blockStatTraverser.traverseLast(TheIf, shouldReturnValue = Yes)

    outputWriter.toString shouldBe TheIfStr
  }

  test("traverseLast() for an 'if' when shouldReturnValue=Uncertain") {
    doWrite(TheIfStr).when(ifTraverser).traverse(eqTree(TheIf), ArgumentMatchers.eq(Uncertain))

    blockStatTraverser.traverseLast(TheIf, shouldReturnValue = Uncertain)

    outputWriter.toString shouldBe TheIfStr
  }

  test("traverseLast() for a 'Try' when shouldReturnValue=Yes") {
    doWrite(TheTryStr).when(tryTraverser).traverse(eqTree(TheTry), ArgumentMatchers.eq(TryContext(Yes)))

    blockStatTraverser.traverseLast(TheTry, shouldReturnValue = Yes)

    outputWriter.toString shouldBe TheTryStr
  }

  test("traverseLast() for a 'TryWithHandler' when shouldReturnValue=Yes") {
    doWrite(TheTryStr).when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), ArgumentMatchers.eq(TryContext(Yes)))

    blockStatTraverser.traverseLast(TheTryWithHandler, shouldReturnValue = Yes)

    outputWriter.toString shouldBe TheTryStr
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Yes, shouldTermReturnValue=Yes and statementEndRequired=true") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(Yes))).thenReturn(Yes)
    doWrite(s"return $TheTermApplyStr")
      .when(statTraverser).traverse(eqTree(Return(TheTermApply)), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(Return(TheTermApply)))).thenReturn(true)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Yes)

    outputWriter.toString shouldBe
      s"""return $TheTermApplyStr;
        |""".stripMargin
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Yes, shouldTermReturnValue=No and statementEndRequired=true") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(Yes))).thenReturn(No)
    doWrite(TheTermApplyStr)
      .when(statTraverser).traverse(eqTree(TheTermApply), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheTermApply))).thenReturn(true)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Yes)

    outputWriter.toString shouldBe
      s"""$TheTermApplyStr;
         |""".stripMargin
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Uncertain, shouldTermReturnValue=Uncertain and statementEndRequired=true") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(Uncertain))).thenReturn(Uncertain)
    doWrite(TheTermApplyStr)
      .when(statTraverser).traverse(eqTree(TheTermApply), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheTermApply))).thenReturn(true)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Uncertain)

    outputWriter.toString shouldBe
      s"""/* return? */$TheTermApplyStr;
         |""".stripMargin
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=No, shouldTermReturnValue=No and statementEndRequired=false") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(No))).thenReturn(No)
    doWrite(TheTermApplyStr)
      .when(statTraverser).traverse(eqTree(TheTermApply), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheTermApply))).thenReturn(false)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = No)

    outputWriter.toString shouldBe TheTermApplyStr
  }

  test("traverseLast() for a 'Defn.Def' when statementEndRequired=false") {
    doWrite(TheDefnDefStr)
      .when(statTraverser).traverse(eqTree(TheDefnDef), ArgumentMatchers.eq(BlockStatContext))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheDefnDef))).thenReturn(false)

    blockStatTraverser.traverseLast(TheDefnDef)

    outputWriter.toString shouldBe TheDefnDefStr
  }

  private def termParamXInt() = {
    Term.Param(mods = List(), name = Term.Name("x"), decltpe = Some(TypeNames.Int), default = None)
  }
}
