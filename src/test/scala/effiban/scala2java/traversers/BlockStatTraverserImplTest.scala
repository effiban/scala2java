package effiban.scala2java.traversers

import effiban.scala2java.classifiers.JavaStatClassifier
import effiban.scala2java.entities.Decision.{No, Uncertain, Yes}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.ShouldReturnValueResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Term.Return
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


  private val ifTraverser = mock[IfTraverser]
  private val statTraverser = mock[StatTraverser]
  private val shouldReturnValueResolver = mock[ShouldReturnValueResolver]
  private val javaTermClassifier = mock[JavaStatClassifier]

  private val blockStatTraverser = new BlockStatTraverserImpl(
    ifTraverser,
    statTraverser,
    shouldReturnValueResolver,
    javaTermClassifier)


  test("traverse() when statement end required") {
    doWrite(TheTermApplyStr).when(statTraverser).traverse(TheTermApply)
    when(javaTermClassifier.requiresEndDelimiter(TheTermApply)).thenReturn(true)

    blockStatTraverser.traverse(TheTermApply)

    outputWriter.toString shouldBe
      s"""$TheTermApplyStr;
         |""".stripMargin
  }

  test("traverse() when statement end not required") {
    doWrite(TheTermApplyStr).when(statTraverser).traverse(TheTermApply)
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

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Yes, shouldTermReturnValue=Yes and statementEndRequired=true") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(Yes))).thenReturn(Yes)
    doWrite(s"return $TheTermApplyStr").when(statTraverser).traverse(eqTree(Return(TheTermApply)))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(Return(TheTermApply)))).thenReturn(true)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Yes)

    outputWriter.toString shouldBe
      s"""return $TheTermApplyStr;
        |""".stripMargin
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Yes, shouldTermReturnValue=No and statementEndRequired=true") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(Yes))).thenReturn(No)
    doWrite(TheTermApplyStr).when(statTraverser).traverse(eqTree(TheTermApply))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheTermApply))).thenReturn(true)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Yes)

    outputWriter.toString shouldBe
      s"""$TheTermApplyStr;
         |""".stripMargin
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Uncertain, shouldTermReturnValue=Uncertain and statementEndRequired=true") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(Uncertain))).thenReturn(Uncertain)
    doWrite(TheTermApplyStr).when(statTraverser).traverse(eqTree(TheTermApply))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheTermApply))).thenReturn(true)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Uncertain)

    outputWriter.toString shouldBe
      s"""/* return? */$TheTermApplyStr;
         |""".stripMargin
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=No, shouldTermReturnValue=No and statementEndRequired=false") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), ArgumentMatchers.eq(No))).thenReturn(No)
    doWrite(TheTermApplyStr).when(statTraverser).traverse(eqTree(TheTermApply))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheTermApply))).thenReturn(false)

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = No)

    outputWriter.toString shouldBe TheTermApplyStr
  }

  test("traverseLast() for a 'Defn.Def' when statementEndRequired=false") {
    doWrite(TheDefnDefStr).when(statTraverser).traverse(eqTree(TheDefnDef))
    when(javaTermClassifier.requiresEndDelimiter(eqTree(TheDefnDef))).thenReturn(false)

    blockStatTraverser.traverseLast(TheDefnDef)

    outputWriter.toString shouldBe TheDefnDefStr
  }

  private def termParamXInt() = {
    Term.Param(mods = List(), name = Term.Name("x"), decltpe = Some(TypeNames.Int), default = None)
  }
}
