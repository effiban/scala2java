package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.JavaTreeType.Interface
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.{PrimaryCtors, TypeNames}
import org.mockito.ArgumentMatchers

import scala.meta.Defn.Trait
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
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]


  private val traitTraverser = new TraitTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    templateTraverser,
    javaModifiersResolver,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("traverse()") {
    val `trait` = Defn.Trait(
      mods = Modifiers,
      name = TraitName,
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = TheTemplate
    )

    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnInterface(`trait`)
    whenResolveJavaModifiersThenReturnPublic(`trait`)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(TheTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaScope.Interface, permittedSubTypeNames = permittedSubTypeNames)))

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(`trait`, JavaTreeType.Interface)))).thenReturn(JavaScope.Interface)

    val context = ClassOrTraitContext(javaScope = JavaScope.Package, permittedSubTypeNames = permittedSubTypeNames)
    traitTraverser.traverse(`trait`, context)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public interface MyTrait<T> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(`trait`: Trait): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(`trait`, Modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(Interface)
  }

  private def whenResolveJavaModifiersThenReturnPublic(`trait`: Trait): Unit = {
    val expectedJavaModifiersContext = JavaModifiersContext(`trait`, Modifiers, Interface, JavaScope.Package)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext))).thenReturn(List(JavaModifier.Public))
  }
}