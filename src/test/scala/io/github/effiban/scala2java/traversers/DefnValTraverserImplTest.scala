package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnValTraverserImplTest extends UnitTestSuite {

  private val IntType = TypeNames.Int
  private val MyValPat = Pat.Var(Term.Name("myVal"))
  private val Rhs = Lit.Int(3)

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)

  private val modListTraverser = mock[ModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val rhsTermTraverser = mock[RhsTermTraverser]

  private val defnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    rhsTermTraverser)


  test("traverse() when it is a class member - typed") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |private final """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal = 3""".stripMargin
  }

  test("traverse() when it is a class member - untyped") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |private final """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal = 3""".stripMargin
  }

  test("traverse() when it is an interface member - untyped") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal = 3""".stripMargin
  }

  private def eqExpectedModifiers(defnVal: Defn.Val, javaScope: JavaScope) = {
    val expectedJavaModifiersContext = JavaModifiersContext(defnVal, JavaTreeType.Variable, javaScope)
    eqJavaModifiersContext(expectedJavaModifiersContext)
  }
}
