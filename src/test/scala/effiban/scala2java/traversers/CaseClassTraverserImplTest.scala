package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext, TemplateContext}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import effiban.scala2java.matchers.TemplateContextMatcher.eqTemplateContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
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

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]



  private val classTraverser = new CaseClassTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    termParamListTraverser,
    templateTraverser,
    javaModifiersResolver,
    javaTreeTypeResolver)


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

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    whenResolveJavaModifiersThenReturnPublic(cls, modifiers)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("(int arg1, int arg2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(CtorArgs1),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaTreeType.Class, maybeClassName = Some(ClassName))))

    classTraverser.traverse(cls, StatContext(javaScope))

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

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    whenResolveJavaModifiersThenReturnPublic(cls, modifiers)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("(int arg1, int arg2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(CtorArgs1),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaTreeType.Class, maybeClassName = Some(ClassName), maybePrimaryCtor = Some(primaryCtor)))
    )

    classTraverser.traverse(cls, StatContext(javaScope))

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

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnRecord(cls, modifiers)
    whenResolveJavaModifiersThenReturnPublic(cls, modifiers)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("(int arg1, int arg2, int arg3, int arg4)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(CtorArgs1 ++ CtorArgs2),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        | /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaTreeType.Class, maybeClassName = Some(ClassName))))

    classTraverser.traverse(cls, StatContext(javaScope))

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

  private def whenResolveJavaModifiersThenReturnPublic(cls: Defn.Class, modifiers: List[Mod]): Unit = {
    val expectedJavaModifiersContext = JavaModifiersContext(cls, modifiers, JavaTreeType.Class, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext))).thenReturn(List(JavaModifier.Public))
  }

}
