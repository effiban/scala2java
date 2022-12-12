package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Defn, Init, Lit, Mod, Name, Pat, Term, Type}

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
  private val declVarTraverser = mock[DeclVarTraverser]
  private val defnValToDeclVarTransformer = mock[DefnValToDeclVarTransformer]

  private val defnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    rhsTermTraverser,
    declVarTraverser,
    defnValToDeclVarTransformer
  )


  test("traverse() when transformed, should traverse with the DeclVarTraverser") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    val declVar = Decl.Var(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = TypeNames.Int
    )

    val context = StatContext(javaScope)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(Some(declVar))

    defnValTraverser.traverse(defnVal, context)

    verify(declVarTraverser).traverse(eqTree(declVar), ArgumentMatchers.eq(context))
  }

  test("traverse() when not transformed, and it is a class member - typed") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
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

  test("traverse() when not transformed, and it is a class member - untyped") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
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

  test("traverse() when not transformed, and it is an interface member - typed") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
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

  test("traverse() when not transformed, and it is an interface member - untyped") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
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
    val expectedModifiersContext = ModifiersContext(defnVal, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
