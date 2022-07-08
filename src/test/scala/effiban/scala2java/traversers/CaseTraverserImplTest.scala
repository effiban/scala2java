package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Case, Lit}

class CaseTraverserImplTest extends UnitTestSuite {

  private val Pat = Lit.String("value1")
  private val Cond = Lit.String("value2")
  private val Body: Lit.Int = Lit.Int(3)

  private val patTraverser = mock[PatTraverser]
  private val termTraverser = mock[TermTraverser]

  private val caseTraverser = new CaseTraverserImpl(patTraverser, termTraverser)


  test("traverse() without condition") {
    doWrite(""""value1"""").when(patTraverser).traverse(eqTree(Pat))
    doWrite("3").when(termTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = Pat,
        cond = None,
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1" -> 3;
        |""".stripMargin
  }

  test("traverse() with condition") {
    doWrite(""""value1"""").when(patTraverser).traverse(eqTree(Pat))
    doWrite(""""value2"""").when(termTraverser).traverse(eqTree(Cond))
    doWrite("3").when(termTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = Pat,
        cond = Some(Cond),
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1" && ("value2") -> 3;
        |""".stripMargin
  }
}
