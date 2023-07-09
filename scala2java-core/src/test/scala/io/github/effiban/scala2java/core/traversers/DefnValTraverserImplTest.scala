package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.WithJavaModifiersTraversalResultScalatestMatcher.equalWithJavaModifiersTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DeclVarTraversalResult, DefnValTraversalResult, ModListTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqSomeTree
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Decl, Defn, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnValTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()))
  private val TheJavaModifiers = List(JavaModifier.Private)
  private val TheModListResult = ModListTraversalResult(TheTraversedScalaMods, TheJavaModifiers)

  private val MyValPat = p"myVal"
  private val MyTraversedValPat = p"myTraversedVal"

  private val TheType = t"MyType"
  private val TheTraversedType = t"MyTraversedType"

  private val TheRhs = q"3"
  private val TheTraversedRhs = q"333"
  
  
  private val modListTraverser = mock[ModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patTraverser = mock[PatTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val declVarTraverser = mock[DeclVarTraverser]
  private val defnValToDeclVarTransformer = mock[DefnValToDeclVarTransformer]

  private val defnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    expressionTermTraverser,
    declVarTraverser,
    defnValToDeclVarTransformer
  )


  test("traverse() when transformed to a Decl.Var, should traverse with the DeclVarTraverser") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = Some(TheType),
      rhs = TheRhs
    )
    
    val declVar = Decl.Var(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = TheType
    )
    val traversedDeclVar = Decl.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = TheTraversedType
    )

    val context = StatContext(javaScope)

    val expectedResult = DeclVarTraversalResult(traversedDeclVar, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(Some(declVar))
    doReturn(expectedResult).when(declVarTraverser).traverse(eqTree(declVar), eqTo(context))

    defnValTraverser.traverse(defnVal, context) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when not transformed, and it is a class member - typed") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = Some(TheType),
      rhs = TheRhs
    )
    val traversedDefnVal = Defn.Val(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = Some(TheTraversedType),
      rhs = TheTraversedRhs
    )
    val expectedResult = DefnValTraversalResult(traversedDefnVal, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(None)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheType), eqSomeTree(TheRhs))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when not transformed, and it is a class member - untyped, inferred") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = None,
      rhs = TheRhs
    )
    val traversedDefnVal = Defn.Val(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = Some(TheTraversedType),
      rhs = TheTraversedRhs
    )
    val expectedResult = DefnValTraversalResult(traversedDefnVal, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(None)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when not transformed, and it is a class member - untyped, not inferred") {
    val javaScope = JavaScope.Class

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = None,
      rhs = TheRhs
    )
    val traversedDefnVal = Defn.Val(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = None,
      rhs = TheTraversedRhs
    )
    val expectedResult = DefnValTraversalResult(traversedDefnVal, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(None)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope))
    doReturn(None).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when not transformed, and it is an interface member - typed") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = Some(TheType),
      rhs = TheRhs
    )
    val traversedDefnVal = Defn.Val(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = Some(TheTraversedType),
      rhs = TheTraversedRhs
    )
    val expectedResult = DefnValTraversalResult(traversedDefnVal, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(None)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheType), eqSomeTree(TheRhs))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when not transformed, and it is an interface member - untyped, inferred") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = None,
      rhs = TheRhs
    )
    val traversedDefnVal = Defn.Val(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = Some(TheTraversedType),
      rhs = TheTraversedRhs
    )
    val expectedResult = DefnValTraversalResult(traversedDefnVal, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(None)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when not transformed, and it is an interface member - untyped, not inferred") {
    val javaScope = JavaScope.Interface

    val defnVal = Defn.Val(
      mods = TheScalaMods,
      pats = List(MyValPat),
      decltpe = None,
      rhs = TheRhs
    )
    val traversedDefnVal = Defn.Val(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedValPat),
      decltpe = None,
      rhs = TheTraversedRhs
    )
    val expectedResult = DefnValTraversalResult(traversedDefnVal, TheJavaModifiers)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), eqTo(javaScope))).thenReturn(None)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(defnVal, javaScope))
    doReturn(None).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheRhs))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheRhs))

    defnValTraverser.traverse(defnVal, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  private def eqExpectedModifiers(defnVal: Defn.Val, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnVal, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
