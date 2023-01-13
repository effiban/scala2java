package io.github.effiban.scala2java.spi.transformers

import scala.meta.Importer

/** A transformer which can modify a given Scala [[Importer]]
 * '''NOTE''' - a Scala `import` statement might be an aggregation of statements having the same suffix ([[scala.meta.Importee]]).<br>
 * In such a case, the framework will split the statements into separate ones and invoke the transformer separately for each,
 * which conveniently allows the implementation to treat each one separately.<br>
 * For example, if the Scala file has the statement:<br>
 * `import aaa.bbb.{ccc, ddd}`<br>
 * then the framework will split it into two separate statements:<br>
 * `import aaa.bbb.ccc`<br>
 * `import aaa.bbb.ddd`<br>
 * Therefore, if the extension wishes to modify only `aaa.bbb.ccc` - it can simply check for it directly,
 * without worrying about having to extract it from a composite statement.
 */
trait ImporterTransformer extends SameTypeTransformer0[Importer]

object ImporterTransformer {

  /** The default transformer which returns the importer unchanged, indicating that no transformation is needed */
  def Identity: ImporterTransformer = identity[Importer]
}