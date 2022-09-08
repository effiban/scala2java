package effiban.scala2java.transformers

import effiban.scala2java.entities.CtorContext

import scala.meta.Ctor.Secondary
import scala.meta.Term.Block
import scala.meta.{Ctor, Defn, Term, Type}

trait CtorSecondaryTransformer {
  def transform(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Defn.Def
}

object CtorSecondaryTransformer extends CtorSecondaryTransformer {

  def transform(secondaryCtor: Secondary, ctorContext: CtorContext): Defn.Def = {

    Defn.Def(
      mods = secondaryCtor.mods,
      name = Term.Name(ctorContext.className.value),
      tparams = Nil,
      paramss = secondaryCtor.paramss,
      decltpe = Some(Type.AnonymousName()),
      body = Block(secondaryCtor.stats)
    )
  }
}
