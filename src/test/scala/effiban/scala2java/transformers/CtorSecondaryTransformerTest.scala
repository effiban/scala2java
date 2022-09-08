package effiban.scala2java.transformers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{CtorContext, JavaTreeType}
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Term.Block
import scala.meta.{Ctor, Defn, Init, Lit, Mod, Name, Term, Type}

class CtorSecondaryTransformerTest extends UnitTestSuite {

  private val ClassName = "MyClass"

  private val AnnotationName = "MyAnnotation"

  private val Modifiers = List(
    Mod.Annot(
      Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
    )
  )

  private val CtorParams = List(
    termParamInt("param1"),
    termParamInt("param2")
  )

  private val ThisInit =
    Init(
      tpe = Type.Singleton(ref = Term.This(Name.Anonymous())),
      name = Name.Anonymous(),
      argss = List(List(Lit.String("param3"), Lit.String("param4")))
    )


  test("traverse() when has no params and no body") {
    javaScope = JavaTreeType.Class

    val secondaryCtor = Ctor.Secondary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(Nil),
      init = ThisInit,
      stats = Nil
    )

    val expectedDefnDef = Defn.Def(
      mods = secondaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(Nil),
      decltpe = Some(Type.AnonymousName()),
      body = Block(Nil)
    )

    val actualDefnDef = CtorSecondaryTransformer.transform(secondaryCtor = secondaryCtor, ctorContext = CtorContext(Type.Name(ClassName), Nil))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and no body") {
    javaScope = JavaTreeType.Class

    val secondaryCtor = Ctor.Secondary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams),
      init = ThisInit,
      stats = Nil
    )

    val expectedDefnDef = Defn.Def(
      mods = secondaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = Some(Type.AnonymousName()),
      body = Block(Nil)
    )

    val actualDefnDef = CtorSecondaryTransformer.transform(secondaryCtor = secondaryCtor, ctorContext = CtorContext(Type.Name(ClassName), Nil))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has no params and has body") {
    javaScope = JavaTreeType.Class

    val stats = List(
      Term.Apply(Term.Name("doSomething"), List(Lit.String("param5"), Lit.String("param6"))),
      Term.Apply(Term.Name("doSomethingElse"), List(Lit.String("param7"), Lit.String("param8")))
    )

    val secondaryCtor = Ctor.Secondary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(Nil),
      init = ThisInit,
      stats = stats
    )

    val expectedDefnDef = Defn.Def(
      mods = secondaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(Nil),
      decltpe = Some(Type.AnonymousName()),
      body = Block(stats)
    )

    val actualDefnDef = CtorSecondaryTransformer.transform(secondaryCtor = secondaryCtor, ctorContext = CtorContext(Type.Name(ClassName), Nil))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and body") {
    javaScope = JavaTreeType.Class

    val stats = List(
      Term.Apply(Term.Name("doSomething"), List(Lit.String("param5"), Lit.String("param6"))),
      Term.Apply(Term.Name("doSomethingElse"), List(Lit.String("param7"), Lit.String("param8")))
    )

    val secondaryCtor = Ctor.Secondary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams),
      init = ThisInit,
      stats = stats
    )

    val expectedDefnDef = Defn.Def(
      mods = secondaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = Some(Type.AnonymousName()),
      body = Block(stats)
    )

    val actualDefnDef = CtorSecondaryTransformer.transform(secondaryCtor = secondaryCtor, ctorContext = CtorContext(Type.Name(ClassName), Nil))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }
}
