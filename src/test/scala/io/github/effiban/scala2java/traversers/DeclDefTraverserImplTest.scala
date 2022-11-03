package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Term, Type}

class DeclDefTraverserImplTest extends UnitTestSuite {

  private val MethodType: Type.Name = TypeNames.Int
  private val MethodName: Term.Name = Term.Name("myMethod")

  private val Modifiers: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val MethodParams1 = List(
    termParamInt("param1"),
    termParamInt("param2")
  )
  private val MethodParams2 = List(
    termParamInt("param3"),
    termParamInt("param4")
  )

  private val modListTraverser = mock[ModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]

  private val declDefTraverser = new DeclDefTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    typeTraverser,
    termNameTraverser,
    termParamListTraverser)


  test("traverse() for class method when has one list of params") {
    val javaScope = JavaScope.Class

    val declDef = Decl.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = MethodType
    )

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2)""".stripMargin
  }

  test("traverse() for class method when has type params") {
    val javaScope = JavaScope.Class

    val declDef = Decl.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParams1),
      decltpe = MethodType
    )

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("int").when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T> int myMethod(int param1, int param2)""".stripMargin
  }

  test("traverse() for interface method when has one list of params") {
    val javaScope = JavaScope.Interface

    val declDef = Decl.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = MethodType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |int myMethod(int param1, int param2)""".stripMargin
  }

  test("traverse() for interface method when has two lists of params") {
    val javaScope = JavaScope.Interface

    val declDef = Decl.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1, MethodParams2),
      decltpe = MethodType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2, int param3, int param4)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1 ++ MethodParams2),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |int myMethod(int param1, int param2, int param3, int param4)""".stripMargin
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }

  private def eqExpectedModifiers(declDef: Decl.Def, javaScope: JavaScope) = {
    val expectedJavaModifiersContext = JavaModifiersContext(declDef, Modifiers, JavaTreeType.Method, javaScope)
    eqJavaModifiersContext(expectedJavaModifiersContext)
  }
}
