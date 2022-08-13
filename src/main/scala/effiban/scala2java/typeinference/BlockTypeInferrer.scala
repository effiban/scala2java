package effiban.scala2java.typeinference

import scala.meta.Term.Block
import scala.meta.{Term, Type}

trait BlockTypeInferrer extends TypeInferrer[Block]

private[typeinference] class BlockTypeInferrerImpl(termTypeInferrer: TermTypeInferrer) extends BlockTypeInferrer {

  override def infer(block: Block): Option[Type] = {
    block.stats match {
      case _ :+ last => last match {
        case lastTerm: Term => termTypeInferrer.infer(lastTerm)
        case _ => None
      }
      case Nil => None
    }
  }
}
