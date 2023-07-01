package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.WithJavaModifiersTraversalResultScalatestMatcher.equalWithJavaModifiersTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DefnVarTraversalResult, ModListTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqSomeTree
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Defn, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()))
  private val TheJavaModifiers = List(JavaModifier.Private)
  private val TheModListResult = ModListTraversalResult(TheTraversedScalaMods, TheJavaModifiers)

  private val MyVarPat = p"myVar"
  private val MyTraversedVarPat = p"myTraversedVar"

  private val TheType = t"MyType"
  private val TheTraversedType = t"MyTraversedType"

  private val TheRhs = q"3"
  private val TheTraversedRhs = q"33"

  private val modListTraverser = mock[ModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patTraverser = mock[PatTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    expressionTermTraverser)


  test("traverse() when it is a class member - typed with value") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = Some(TheRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheType), eqSomeTree(TheRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is a class member - typed without value") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = None
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = None
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheType), eqTo(None))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is a class member - untyped with value - type inferred") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is a class member - untyped with value - type not inferred") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = None,
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(None).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - typed with value") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = Some(TheRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheType), eqSomeTree(TheRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - typed without value") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = None
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = None
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheType), eqTo(None))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - untyped with value - type inferred") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - untyped with value - type not inferred") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = None,
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope))
    doReturn(None).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  private def eqExpectedModifiers(defnVar: Defn.Var, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
