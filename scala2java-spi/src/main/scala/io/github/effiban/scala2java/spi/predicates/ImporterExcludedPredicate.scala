package io.github.effiban.scala2java.spi.predicates

import scala.meta.Importer

/** A predicate which determines whether a given [[Importer]] (Scala `import` statement),
 * should be excluded from the generated Java source file.<br>
 * '''NOTE''' - a Scala `import` statement might be an aggregation of statements having the same suffix ([[scala.meta.Importee]]).<br>
 * In such a case, the framework will split the statements into separate ones and invoke the predicate separately for each, to allow
 * fine-grained exclusion logic.<br>
 * For example, if the Scala file has the statement:<br>
 * `import aaa.bbb.{ccc, ddd}`<br>
 * then the framework will split it into two separate statements:<br>
 * `import aaa.bbb.ccc`<br>
 * `import aaa.bbb.ddd`<br>
 * --> and the predicate will be invoked for each one separately.
 */
trait ImporterExcludedPredicate extends (Importer => Boolean)

object ImporterExcludedPredicate {
  /** The default predicate which does not exclude any [[Importer]] */
  val None: ImporterExcludedPredicate = _ => false
}
