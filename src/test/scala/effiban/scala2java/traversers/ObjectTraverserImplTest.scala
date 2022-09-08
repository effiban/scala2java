package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersResolverParamsMatcher.eqJavaModifiersResolverParams
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term, Type}

class ObjectTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val annotListTraverser = mock[AnnotListTraverser]
  private val templateTraverser = mock[TemplateTraverser]

  private val objectTraverser = new ObjectTraverserImpl(
    annotListTraverser,
    templateTraverser,
    javaModifiersResolver)


  test("traverse()") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val template = Template(
      early = List(),
      inits = List(),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(
        Defn.Def(
          mods = List(),
          name = Term.Name("MyMethod"),
          tparams = List(),
          paramss = List(List(termParam("myParam1", "Int"), termParam("myParam2", "String"))),
          decltpe = Some(TypeNames.String),
          body = Block(List())
        )
      )
    )

    val objectDef = Defn.Object(
      mods = modifiers,
      name = Term.Name("MyObject"),
      templ = template
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiersThenReturnPublic(objectDef, modifiers)
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(eqTree(template), ArgumentMatchers.eq(None))

    objectTraverser.traverse(objectDef)

    outputWriter.toString shouldBe
      """
        |/* originally a Scala object */
        |@MyAnnotation
        |public class MyObject {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }

  private def whenResolveJavaModifiersThenReturnPublic(obj: Defn.Object, modifiers: List[Mod]): Unit = {
    val expectedResolverParams = JavaModifiersResolverParams(obj, modifiers, JavaTreeType.Class, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersResolverParams(expectedResolverParams))).thenReturn(List(JavaModifier.Public))
  }
}
