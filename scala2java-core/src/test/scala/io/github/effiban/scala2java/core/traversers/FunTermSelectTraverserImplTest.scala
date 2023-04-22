package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class FunTermSelectTraverserImplTest extends UnitTestSuite {

  private val MyInstance = q"MyObject"
  private val MyMethod = q"myMethod"
  private val MyMethod2 = q"myMethod"
  private val SelectWithTermName = Term.Select(qual = MyInstance, name = MyMethod)

  private val qualifierTraverser = mock[TermTraverser]
  private val termNameRenderer = mock[TermNameRenderer]
  private val typeListTraverser = mock[TypeListTraverser]

  private val funTermSelectTraverser = new FunTermSelectTraverserImpl(
    qualifierTraverser,
    termNameRenderer,
    typeListTraverser,
  )

  test("traverse() when qualifier is a Term.Name, and has type args") {
    val typeArgs = List(TypeNames.Int)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)

    doWrite("MyObject").when(qualifierTraverser).traverse(eqTree(MyInstance))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MyMethod))
    doWrite("<Integer>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    funTermSelectTraverser.traverse(SelectWithTermName, context)

    outputWriter.toString shouldBe "MyObject.<Integer>myMethod"
  }

  test("traverse() when qualifier is a Term.Name and has no type args") {

    doWrite("MyObject").when(qualifierTraverser).traverse(eqTree(MyInstance))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MyMethod))
    funTermSelectTraverser.traverse(SelectWithTermName)

    outputWriter.toString shouldBe "MyObject.myMethod"
  }

  test("traverse() when qualifier is a Term.Function should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val scalaSelect = Term.Select(termFunction, MyMethod)

    doWrite("() -> 1").when(qualifierTraverser).traverse(eqTree(termFunction))
    doWrite("get").when(termNameRenderer).render(eqTree(MyMethod))
    funTermSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe "(() -> 1).get"
  }

  test("traverse() when qualifier is a Term.Ascribe applied to a Term.Function, should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val ascribedTermFunction = Term.Ascribe(termFunction, t"Supplier[Int]")
    val termSelect = Term.Select(ascribedTermFunction, MyMethod)

    doWrite("(Supplier<Integer>)() -> 1").when(qualifierTraverser).traverse(eqTree(ascribedTermFunction))
    doWrite("get").when(termNameRenderer).render(eqTree(MyMethod))

    funTermSelectTraverser.traverse(termSelect)

    outputWriter.toString shouldBe "((Supplier<Integer>)() -> 1).get"
  }

  test("traverse() when qualifier is a Term.Apply should break the line") {
    val arg = List(Term.Name("arg1"))

    val qual = Term.Apply(SelectWithTermName, arg)
    val select = Term.Select(qual, MyMethod2)

    doWrite("MyObject.myMethod(arg1)").when(qualifierTraverser).traverse(eqTree(qual))
    doWrite("myMethod2").when(termNameRenderer).render(eqTree(MyMethod2))
    funTermSelectTraverser.traverse(select)

    outputWriter.toString shouldBe
      """MyObject.myMethod(arg1)
        |.myMethod2""".stripMargin
  }
}
