package effiban.scala2java.traversers

import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, TypeNames}
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term, Type}

class TraitTraverserImplTest extends UnitTestSuite {

  private val TraitName = Type.Name("MyTrait")

  private val Modifiers: List[Mod.Annot] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val JavaModifier = "public"

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

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val traitTraverser = new TraitTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    templateTraverser,
    javaModifiersResolver
  )


  test("traverse()") {
    val `trait` = Defn.Trait(
      mods = Modifiers,
      name = TraitName,
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = TheTemplate
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForInterface(eqTreeList(Modifiers))).thenReturn(List(JavaModifier))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(eqTree(TheTemplate))

    traitTraverser.traverse(`trait`)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public interface MyTrait<T> {
        |  /* BODY */
        |}
        |""".stripMargin
  }
}
