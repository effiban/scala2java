package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.renderers.ModListRenderer
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Term, Type}

class CaseClassTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"

  private val ClassName = Type.Name("MyRecord")

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

  private val CtorArgs1 = List(
    termParam("arg1", "Int"),
    termParam("arg2", "Int")
  )
  private val CtorArgs2 = List(
    termParam("arg3", "Int"),
    termParam("arg4", "Int")
  )

  private val TheTemplate = Template(
    early = List(),
    inits = List(),
    self = Self(name = Name.Anonymous(), decltpe = None),
    stats = List(
      Defn.Def(
        mods = List(),
        name = Term.Name("MyMethod"),
        tparams = List(),
        paramss = List(List(termParam("myParam", "String"))),
        decltpe = Some(TypeNames.String),
        body = Block(List())
      )
    )
  )

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termParamListTraverser = mock[DeprecatedTermParamListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]



  private val classTraverser = new CaseClassTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    termParamListTraverser,
    templateTraverser,
    javaTreeTypeResolver,
    javaChildScopeResolver)

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  test("traverse() for one list of ctor args") {
    val scalaMods: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgs1))

    val cls = Defn.Class(
      mods = scalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))

    whenResolveJavaTreeTypeThenReturnRecord(cls, scalaMods)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(cls))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Record)))).thenReturn(JavaScope.Class)
    doWrite("(int arg1, int arg2)").when(termParamListTraverser).traverse(
        termParams = eqTreeList(CtorArgs1),
        context = ArgumentMatchers.eq(StatContext(JavaScope.Class)),
        onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaScope.Class, maybeClassName = Some(ClassName))))

    classTraverser.traverse(cls, ClassOrTraitContext(JavaScope.Package))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2) {
        | /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for one list of ctor args with annotation on ctor.") {
    val scalaMods: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(
      mods = List(
        Mod.Annot(
          Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
        )
      ),
      name = Name.Anonymous(),
      paramss = List(CtorArgs1))

    val cls = Defn.Class(
      mods = scalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))

    whenResolveJavaTreeTypeThenReturnRecord(cls, scalaMods)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(cls))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Record)))).thenReturn(JavaScope.Class)
    doWrite("(int arg1, int arg2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(CtorArgs1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.Class)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaScope.Class, maybeClassName = Some(ClassName), maybePrimaryCtor = Some(primaryCtor)))
    )

    classTraverser.traverse(cls, ClassOrTraitContext(JavaScope.Package))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2) {
        | /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for one list of ctor args with permitted sub-type names") {
    val scalaMods: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgs1))

    val cls = Defn.Class(
      mods = scalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))

    whenResolveJavaTreeTypeThenReturnRecord(cls, scalaMods)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(cls))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Record)))).thenReturn(JavaScope.Class)
    doWrite("(int arg1, int arg2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(CtorArgs1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.Class)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(
        javaScope = JavaScope.Class,
        maybeClassName = Some(ClassName),
        permittedSubTypeNames = permittedSubTypeNames)
      )
    )

    val context = ClassOrTraitContext(
      javaScope = JavaScope.Package,
      permittedSubTypeNames = permittedSubTypeNames
    )
    classTraverser.traverse(cls, context)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2) {
        | /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for two lists of ctor args") {
    val scalaMods: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgs1, CtorArgs2))

    val cls = Defn.Class(
      mods = scalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = scalaMods, javaModifiers = List(JavaModifier.Public))

    whenResolveJavaTreeTypeThenReturnRecord(cls, scalaMods)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(cls))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Record)))).thenReturn(JavaScope.Class)
    doWrite("(int arg1, int arg2, int arg3, int arg4)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(CtorArgs1 ++ CtorArgs2),
      context = ArgumentMatchers.eq(StatContext(JavaScope.Class)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaScope.Class, maybeClassName = Some(ClassName))))

    classTraverser.traverse(cls, ClassOrTraitContext(JavaScope.Package))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2, int arg3, int arg4) {
        | /* BODY */
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }

  private def whenResolveJavaTreeTypeThenReturnRecord(cls: Defn.Class, modifiers: List[Mod]): Unit = {
    val expectedContext = JavaTreeTypeContext(cls, modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedContext))).thenReturn(JavaTreeType.Record)
  }

  private def eqExpectedScalaMods(classDef: Defn.Class) = {
    val expectedModifiersContext = ModifiersContext(classDef, JavaTreeType.Record, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
