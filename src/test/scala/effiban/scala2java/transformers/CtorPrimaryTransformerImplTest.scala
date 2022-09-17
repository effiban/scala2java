package effiban.scala2java.transformers

import effiban.scala2java.contexts.CtorContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers.any

import scala.meta.Term.{Assign, Block, This}
import scala.meta.{Ctor, Defn, Init, Lit, Mod, Name, Term, Type}

class CtorPrimaryTransformerImplTest extends UnitTestSuite {

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

  private val InitArgss = List(List(Lit.String("superArg1"), Lit.String("superArg2")))

  private val ctorInitsToSuperCallTransformer: TemplateInitsToSuperCallTransformer = mock[TemplateInitsToSuperCallTransformer]

  private val ctorPrimaryTransformer = new CtorPrimaryTransformerImpl(ctorInitsToSuperCallTransformer)

  test("traverse() when has no params, no super call, no terms") {
    javaScope = JavaTreeType.Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = Some(Type.AnonymousName()),
      body = Block(Nil)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, CtorContext(Type.Name(ClassName)))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has no params, no super call, but has terms") {
    javaScope = JavaTreeType.Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val terms = List(
      Term.Apply(Term.Name("foo1"), Nil),
      Term.Apply(Term.Name("foo2"), Nil)
    )

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = Some(Type.AnonymousName()),
      body = Block(terms)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, CtorContext(className = Type.Name(ClassName), terms = terms))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has no params and has super call") {
    javaScope = JavaTreeType.Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val inputInit =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = InitArgss
      )

    val expectedSuperCall = Term.Apply(fun = Term.Name("super"), args = InitArgss.flatten)

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = Some(Type.AnonymousName()),
      body = Block(List(expectedSuperCall))
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(Some(expectedSuperCall))

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, CtorContext(Type.Name(ClassName), List(inputInit)))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and no super call") {
    javaScope = JavaTreeType.Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams)
    )

    val expectedAssignments = CtorParams.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Term.Select(This(Name.Anonymous()), paramName), paramName)
    })

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = Some(Type.AnonymousName()),
      body = Block(expectedAssignments)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = CtorPrimaryTransformer.transform(primaryCtor, CtorContext(Type.Name(ClassName)))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and super call") {
    javaScope = JavaTreeType.Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams)
    )

    val inputInit =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = InitArgss
      )

    val expectedSuperCall = Term.Apply(fun = Term.Name("super"), args = InitArgss.flatten)

    val expectedAssignments = CtorParams.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Term.Select(This(Name.Anonymous()), paramName), paramName)
    })

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = Some(Type.AnonymousName()),
      body = Block(expectedSuperCall :: expectedAssignments)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(Some(expectedSuperCall))

    val actualDefnDef = CtorPrimaryTransformer.transform(primaryCtor, CtorContext(Type.Name(ClassName), List(inputInit)))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }
}
