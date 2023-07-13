package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType.Interface
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, TypeNames}
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Defn.Trait
import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term, Type, XtensionQuasiquoteTypeParam}

class TraitTraverserImplTest extends UnitTestSuite {

  private val TraitName = Type.Name("MyTrait")

  private val ScalaMods: List[Mod.Annot] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val TraversedTypeParam1 = tparam"T11"
  private val TraversedTypeParam2 = tparam"T22"
  private val TraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val TheTemplate =
    Template(
      early = List(),
      inits = List(),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(
        Defn.Def(
          mods = List(),
          name = Term.Name("myMethod"),
          tparams = List(),
          paramss = List(List(Term.Param(
            mods = Nil,
            name = Term.Name("myParam"),
            decltpe = Some(TypeNames.Int),
            default = None
          ))),
          decltpe = Some(Type.Name("String")),
          body = Block(List())
        )
      )
    )

  private val statModListTraverser = mock[StatModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]


  private val traitTraverser = new TraitTraverserImpl(
    statModListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    templateTraverser,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("traverse()") {
    val `trait` = Defn.Trait(
      mods = ScalaMods,
      name = TraitName,
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = TheTemplate
    )

    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    whenResolveJavaTreeTypeThenReturnInterface(`trait`)
    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(`trait`))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doWrite("<T11, T22>").when(typeParamListRenderer).render(eqTreeList(TraversedTypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(`trait`, JavaTreeType.Interface)))).thenReturn(JavaScope.Interface)
    doWrite(
    """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
        eqTree(TheTemplate),
        eqTemplateContext(TemplateContext(javaScope = JavaScope.Interface, permittedSubTypeNames = permittedSubTypeNames)))


    val context = ClassOrTraitContext(javaScope = JavaScope.Package, permittedSubTypeNames = permittedSubTypeNames)
    traitTraverser.traverse(`trait`, context)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public interface MyTrait<T11, T22> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(`trait`: Trait): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(`trait`, ScalaMods)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(Interface)
  }

  private def eqExpectedScalaMods(`trait`: Trait) = {
    val expectedModifiersContext = ModifiersContext(`trait`, JavaTreeType.Interface, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
