package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TermParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.{ModListTraversalResult, TermParamTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Term, Type, XtensionQuasiquoteTermParam}

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

  private val ctorArg1 = param"arg1: Int"
  private val ctorArg2 = param"arg2: Int"
  private val ctorArg3 = param"arg3: Int"
  private val ctorArg4 = param"arg4: Int"

  private val CtorArgList1 = List(ctorArg1, ctorArg2)
  private val CtorArgList2 = List(ctorArg3, ctorArg4)

  private val TheTemplate = Template(
    early = List(),
    inits = List(),
    self = Self(name = Name.Anonymous(), decltpe = None),
    stats = List(
      Defn.Def(
        mods = List(),
        name = Term.Name("MyMethod"),
        tparams = List(),
        paramss = List(List(param"myParam: String")),
        decltpe = Some(TypeNames.String),
        body = Block(List())
      )
    )
  )

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termParamTraverser = mock[TermParamTraverser]
  private val termParamListRenderer = mock[TermParamListRenderer]  
  private val templateTraverser = mock[TemplateTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]



  private val classTraverser = new CaseClassTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    termParamTraverser,
    termParamListRenderer,
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

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList1))

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
    doAnswer((param: Term.Param) => TermParamTraversalResult(param))
      .when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doWrite("(int arg1, int arg2)").when(termParamListRenderer).render(
        termParams = eqTreeList(CtorArgList1),
        context = eqTo(TermParamListRenderContext())
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
      paramss = List(CtorArgList1))

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
    doAnswer((param: Term.Param) => TermParamTraversalResult(param))
      .when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doWrite("(int arg1, int arg2)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1),
      context = eqTo(TermParamListRenderContext())
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

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList1))

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
    doAnswer((param: Term.Param) => TermParamTraversalResult(param))
      .when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doWrite("(int arg1, int arg2)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1),
      context = eqTo(TermParamListRenderContext())
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

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList1, CtorArgList2))

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
    doAnswer((param: Term.Param) => TermParamTraversalResult(param))
      .when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doWrite("(int arg1, int arg2, int arg3, int arg4)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1 ++ CtorArgList2),
      context = eqTo(TermParamListRenderContext())
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

  private def whenResolveJavaTreeTypeThenReturnRecord(cls: Defn.Class, modifiers: List[Mod]): Unit = {
    val expectedContext = JavaTreeTypeContext(cls, modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedContext))).thenReturn(JavaTreeType.Record)
  }

  private def eqExpectedScalaMods(classDef: Defn.Class) = {
    val expectedModifiersContext = ModifiersContext(classDef, JavaTreeType.Record, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
