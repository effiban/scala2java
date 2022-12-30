package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer

import scala.meta.Term.Block
import scala.meta.{Term, Type}

trait BlockTypeInferrer extends TypeInferrer[Block]

private[typeinference] class BlockTypeInferrerImpl(termTypeInferrer: TermTypeInferrer) extends BlockTypeInferrer {

  override def infer(block: Block): Option[Type] = {
    block.stats match {
      case _ :+ (lastTerm: Term) => termTypeInferrer.infer(lastTerm)
      case _ => Some(Type.AnonymousName())
    }
  }
}
