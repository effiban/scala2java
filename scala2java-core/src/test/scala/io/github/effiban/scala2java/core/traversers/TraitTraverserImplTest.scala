package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.PrimaryCtors
import io.github.effiban.scala2java.core.traversers.results.matchers.TraitTraversalResultScalatestMatcher.equalTraitTraversalResult
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Defn.Trait
import scala.meta.{Defn, Mod, Name, Self, Template, Type, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TraitTraverserImplTest extends UnitTestSuite {

  private val TraitName = t"MyTrait"

  private val ScalaMods: List[Mod] = List(mod"@MyAnnotation1", mod"@MyAnnotation2")
  private val TraversedScalaMods: List[Mod] = List(mod"@MyTraversedAnnotation1", mod"@MyTraversedAnnotation2")

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val TraversedTypeParam1 = tparam"T11"
  private val TraversedTypeParam2 = tparam"T22"
  private val TraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val Inits = List(Init1, Init2)

  private val TraversedInit1 = init"TraversedParent1()"
  private val TraversedInit2 = init"TraversedParent2()"
  private val TraversedInits = List(TraversedInit1, TraversedInit2)

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))
  private val TheTraversedSelf = Self(name = Name.Indeterminate("TraversedSelfName"), decltpe = Some(t"SelfType"))

  private val DefnVar = q"var y = 4"
  private val TraversedDefnVar = q"var yy = 44"
  private val TheDefnVarTraversalResult = DefnVarTraversalResult(TraversedDefnVar)

  private val DefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TraversedDefnDef = q"def myTraversedMethod(param2: Int): Int = doSomething(param2)"
  private val TheDefnDefTraversalResult = DefnDefTraversalResult(TraversedDefnDef)

  private val TheStats = List(DefnVar, DefnDef)
  private val TheTraversedStatResults = List(TheDefnVarTraversalResult, TheDefnDefTraversalResult)

  private val TheTemplate =
    Template(
      early = List(),
      inits = Inits,
      self = TheSelf,
      stats = TheStats
    )

  private val statModListTraverser = mock[StatModListTraverser]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val templateTraverser = mock[TemplateTraverser]


  private val traitTraverser = new TraitTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    templateTraverser
  )


  test("traverse()") {
    val `trait` = Defn.Trait(
      mods = ScalaMods,
      name = TraitName,
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = TheTemplate
    )

    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TraversedScalaMods, javaModifiers = expectedJavaModifiers)
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      inits = TraversedInits,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedTraitTraversalResult = TraitTraversalResult(
      scalaMods = TraversedScalaMods,
      javaModifiers = expectedJavaModifiers,
      name = TraitName,
      tparams = TraversedTypeParams,
      inits = TraversedInits,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(`trait`))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(
        eqTree(TheTemplate),
        eqTemplateContext(TemplateContext(javaScope = JavaScope.Interface)))


    val context = ClassOrTraitContext(javaScope = JavaScope.Package)
    traitTraverser.traverse(`trait`, context) should equalTraitTraversalResult(expectedTraitTraversalResult)
  }

  private def eqExpectedScalaMods(`trait`: Trait) = {
    val expectedModifiersContext = ModifiersContext(`trait`, JavaTreeType.Interface, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
