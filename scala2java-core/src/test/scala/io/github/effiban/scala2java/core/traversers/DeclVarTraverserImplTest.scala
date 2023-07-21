package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.DeclVarTraversalResultScalatestMatcher.equalDeclVarTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DeclVarTraversalResult, ModListTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Decl, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod, XtensionQuasiquoteType}

class DeclVarTraverserImplTest extends UnitTestSuite {

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

  private val TheDeclVar = Decl.Var(
    mods = TheScalaMods,
    pats = List(MyVarPat),
    decltpe = TheType
  )
  private val TheTraversedDeclVar = Decl.Var(
    mods = TheTraversedScalaMods,
    pats = List(MyTraversedVarPat),
    decltpe = TheTraversedType
  )
  private val TheDeclVarResult = DeclVarTraversalResult(TheTraversedDeclVar, TheJavaModifiers)

  private val statModListTraverser = mock[StatModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val patTraverser = mock[PatTraverser]

  private val declVarTraverser = new DeclVarTraverserImpl(
    statModListTraverser,
    typeTraverser,
    patTraverser
  )


  test("traverse() when it is a class member") {
    val javaScope = JavaScope.Class

    doReturn(TheModListResult).when(statModListTraverser).traverse(eqExpectedModifiers(TheDeclVar, javaScope))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))

    declVarTraverser.traverse(TheDeclVar, StatContext(javaScope)) should equalDeclVarTraversalResult(TheDeclVarResult)
  }

  test("traverse() when it is an interface member") {
    val javaScope = JavaScope.Interface

    doReturn(TheModListResult).when(statModListTraverser).traverse(eqExpectedModifiers(TheDeclVar, javaScope))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))

    declVarTraverser.traverse(TheDeclVar, StatContext(javaScope)) should equalDeclVarTraversalResult(TheDeclVarResult)
  }

  private def eqExpectedModifiers(declVar: Decl.Var, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
