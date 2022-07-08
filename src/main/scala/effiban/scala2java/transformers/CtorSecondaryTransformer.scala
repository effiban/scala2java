package effiban.scala2java.transformers

import scala.meta.Ctor.Secondary
import scala.meta.Term.Block
import scala.meta.{Ctor, Defn, Term, Type}

trait CtorSecondaryTransformer {
  def transform(secondaryCtor: Ctor.Secondary, className: Type.Name): Defn.Def
}

object CtorSecondaryTransformer extends CtorSecondaryTransformer {

  def transform(secondaryCtor: Secondary, className: Type.Name): Defn.Def = {

    Defn.Def(
      mods = secondaryCtor.mods,
      name = Term.Name(className.value),
      tparams = Nil,
      paramss = secondaryCtor.paramss,
      decltpe = None,
      body = Block(secondaryCtor.stats)
    )
  }
}
