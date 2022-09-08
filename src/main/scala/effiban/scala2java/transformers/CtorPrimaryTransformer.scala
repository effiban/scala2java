package effiban.scala2java.transformers

import effiban.scala2java.entities.CtorContext

import scala.meta.Term.{Assign, Block, Select, This}
import scala.meta.{Ctor, Defn, Name, Term, Type}

trait CtorPrimaryTransformer {
  def transform(primaryCtor: Ctor.Primary, ctorContext: CtorContext): Defn.Def
}

private[transformers] class CtorPrimaryTransformerImpl(templateInitsToSuperCallTransformer: TemplateInitsToSuperCallTransformer)
  extends CtorPrimaryTransformer {

  def transform(primaryCtor: Ctor.Primary, ctorContext: CtorContext): Defn.Def = {
    import ctorContext._
    // Initialize members explicitly (what is done implicitly for Java records and Scala classes)
    val assignments = primaryCtor.paramss.flatten.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Select(This(Name.Anonymous()), paramName), paramName)
    })

    val maybeSuperCall = templateInitsToSuperCallTransformer.transform(inits)

    Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(className.value),
      tparams = Nil,
      paramss = primaryCtor.paramss,
      decltpe = Some(Type.AnonymousName()),
      body = Block(maybeSuperCall.toList ++ assignments ++ terms)
    )
  }
}

object CtorPrimaryTransformer extends CtorPrimaryTransformerImpl(TemplateInitsToSuperCallTransformer)
