package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Name, Tree, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class TemplateChildrenTraverserImplTest extends UnitTestSuite {

  private val TheDeclVal = q"val x: Int"

  private val TheDefnVal = q"val x = 4"

  private val PrimaryCtorArgs = List(
    param"arg1: Int",
    param"arg2: String"
  )
  private val SecondaryCtorArgs = List(
    param"arg3: Int",
    param"arg4: String"
  )

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )

  private val SecondaryCtor = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = init"this()",
    stats = Nil
  )

  private val TheTermApply = q"doSomething1(param1)"

  private val TheDefnDef = Defn.Def(
    mods = Nil,
    name = q"myMethod1",
    tparams = Nil,
    paramss = List(List(param"param: Int")),
    decltpe = Some(TypeNames.Int),
    body = TheTermApply
  )

  private val ChildOrder = List[Tree](
    TheDeclVal,
    TheDefnVal,
    PrimaryCtor,
    SecondaryCtor,
    TheDefnDef
  )

  private val childContext = mock[TemplateChildContext]

  private val childTraverser = mock[TemplateChildTraverser]
  private val javaChildOrdering = mock[JavaTemplateChildOrdering]

  private val childrenTraverser = new TemplateChildrenTraverserImpl(childTraverser, javaChildOrdering)
  
  test("traverse") {

    val children = List[Tree](
      TheDefnDef,
      TheDeclVal,
      SecondaryCtor,
      TheDefnVal,
      PrimaryCtor
    )

    expectWriteDeclVal()
    expectWriteDefnVal()
    expectWritePrimaryCtor()
    expectWriteSecondaryCtor()
    expectWriteDefnDef()

    expectChildOrdering()

    childrenTraverser.traverse(children, childContext)

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  PRIMARY CTOR
        |*/
        |/*
        |*  SECONDARY CTOR
        |*/
        |/*
        |*  METHOD DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  private def expectChildOrdering() = {
    when(javaChildOrdering.compare(any[Tree], any[Tree]))
      .thenAnswer((tree1: Tree, tree2: Tree) => positionOf(tree1) - positionOf(tree2))
  }

  private def expectWriteDeclVal(): Unit = {
    doWrite(
      """/* DATA MEMBER DECL */;
        |""".stripMargin)
      .when(childTraverser).traverse(eqTree(TheDeclVal), eqTo(childContext))
  }

  private def expectWriteDefnVal(): Unit = {
    doWrite(
      """/* DATA MEMBER DEFINITION */;
        |""".stripMargin)
      .when(childTraverser).traverse(eqTree(TheDefnVal), eqTo(childContext))
  }

  private def expectWritePrimaryCtor(): Unit = {
    doWrite(
      """/*
        |*  PRIMARY CTOR
        |*/
        |""".stripMargin)
      .when(childTraverser).traverse(eqTree(PrimaryCtor), eqTo(childContext))
  }

  private def expectWriteSecondaryCtor(): Unit = {
    doWrite(
      """/*
        |*  SECONDARY CTOR
        |*/
        |""".stripMargin)
      .when(childTraverser).traverse(eqTree(SecondaryCtor), eqTo(childContext))
  }

  private def expectWriteDefnDef(): Unit = {
    doWrite(
      """/*
        |*  METHOD DEFINITION
        |*/
        |""".stripMargin)
      .when(childTraverser).traverse(eqTree(TheDefnDef), eqTo(childContext))
  }

  private def positionOf(tree: Tree) = {
    ChildOrder.zipWithIndex
      .find { case (child, _) => child.structure == tree.structure }
      .map(_._2)
      .getOrElse(Int.MaxValue)
  }
}
