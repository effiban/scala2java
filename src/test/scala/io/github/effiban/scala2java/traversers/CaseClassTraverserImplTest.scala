package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

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
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]



  private val classTraverser = new CaseClassTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    termParamListTraverser,
    templateTraverser,
    javaTreeTypeResolver,
    javaChildScopeResolver)

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  test("traverse() for one list of ctor args") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgs1))

    val cls = Defn.Class(
      mods = modifiers,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val modifiers: List[Mod] = List(
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
      mods = modifiers,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgs1))

    val cls = Defn.Class(
      mods = modifiers,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgs1, CtorArgs2))

    val cls = Defn.Class(
      mods = modifiers,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )

    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
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

  private def eqExpectedModifiers(classDef: Defn.Class) = {
    val expectedJavaModifiersContext = JavaModifiersContext(classDef, JavaTreeType.Record, JavaScope.Package)
    eqJavaModifiersContext(expectedJavaModifiersContext)
  }
}
