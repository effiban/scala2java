package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
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

  private val modListTraverser = mock[ModListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val objectTraverser = new ObjectTraverserImpl(
    modListTraverser,
    templateTraverser,
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

    whenResolveJavaTreeTypeThenReturnClass(objectDef, TheMods)
    doWrite(
    """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(objectDef), annotsOnSameLine = ArgumentMatchers.eq(false))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, JavaTreeType.Class)))).thenReturn(JavaScope.Class)
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
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(objectDef), annotsOnSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnClass(objectDef, TheMods)
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

  private def eqExpectedModifiers(obj: Defn.Object) = {
    val expectedJavaModifiersContext = JavaModifiersContext(obj, JavaTreeType.Class, JavaScope.Package)
    eqJavaModifiersContext(expectedJavaModifiersContext)
  }
}
