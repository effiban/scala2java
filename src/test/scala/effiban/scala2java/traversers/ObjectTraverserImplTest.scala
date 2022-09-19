package effiban.scala2java.traversers

import effiban.scala2java.contexts._
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import effiban.scala2java.matchers.TemplateContextMatcher.eqTemplateContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term, Type}

class ObjectTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"

  private val TheMods: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
    )
  )

  private val TheDefnDef = Defn.Def(
    mods = List(),
    name = Term.Name("MyMethod"),
    tparams = List(),
    paramss = List(List(termParam("myParam1", "Int"), termParam("myParam2", "String"))),
    decltpe = Some(TypeNames.String),
    body = Block(List())
  )

  private val annotListTraverser = mock[AnnotListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val objectTraverser = new ObjectTraverserImpl(
    annotListTraverser,
    templateTraverser,
    javaModifiersResolver,
    javaTreeTypeResolver,
    javaChildScopeResolver)


  test("traverse() when standalone") {
    val javaScope = JavaScope.Package

    val template = Template(
      early = List(),
      inits = List(),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(TheDefnDef)
    )

    val objectDef = Defn.Object(
      mods = TheMods,
      name = Term.Name("MyObject"),
      templ = template
    )

    val expectedTemplateContext = TemplateContext(javaScope = JavaScope.Class)

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, JavaTreeType.Class)))).thenReturn(JavaScope.Class)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(TheMods), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnClass(objectDef, TheMods)
    whenResolveJavaModifiersThenReturnPublic(objectDef, TheMods, javaScope)

    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyObject {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when inheriting") {
    val javaScope = JavaScope.Package

    val template = Template(
      early = List(),
      inits = List(
        Init(tpe = Type.Name("A"), name = Name.Anonymous(), argss = List(List(Term.Name("a"))))
      ),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(TheDefnDef)
    )

    val objectDef = Defn.Object(
      mods = TheMods,
      name = Term.Name("MyObject"),
      templ = template
    )

    val expectedTemplateContext = TemplateContext(javaScope = JavaScope.Class)

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, JavaTreeType.Class)))).thenReturn(JavaScope.Class)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(TheMods), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnClass(objectDef, TheMods)
    whenResolveJavaModifiersThenReturnPublic(objectDef, TheMods, javaScope)
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyObject {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }

  private def whenResolveJavaTreeTypeThenReturnClass(obj: Defn.Object, modifiers: List[Mod]): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(obj, modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Class)
  }

  private def whenResolveJavaModifiersThenReturnPublic(obj: Defn.Object, modifiers: List[Mod], javaScope: JavaScope): Unit = {
    val expectedJavaModifiersContext = JavaModifiersContext(obj, modifiers, JavaTreeType.Class, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext))).thenReturn(List(JavaModifier.Public))
  }
}
