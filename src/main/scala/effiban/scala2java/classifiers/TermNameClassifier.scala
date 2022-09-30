package effiban.scala2java.classifiers

import effiban.scala2java.entities.TermNameValues

import scala.meta.Term

trait TermNameClassifier {
  def isJavaStreamLike(termName: Term.Name): Boolean

  def isJavaListLike(termName: Term.Name): Boolean

  def isJavaSetLike(termName: Term.Name): Boolean

  def isJavaMapLike(termName: Term.Name): Boolean

  def isScalaObject(termName: Term.Name): Boolean

  def isInstantiatedByName(termName: Term.Name): Boolean
}

object TermNameClassifier extends TermNameClassifier {

  final val JavaStreamLike: Set[String] = Set(
    "LazyList",
    TermNameValues.Stream,
  )

  final val JavaListLike: Set[String] = Set(
    TermNameValues.Seq,
    "LinearSeq",
    "IndexedSeq",
    "ArraySeq",
    TermNameValues.List,
    TermNameValues.ScalaVector
  )

  final val JavaSetLike: Set[String] = Set(
    TermNameValues.Set,
    "HashSet"
  )

  final val JavaMapLike: Set[String] = Set(
    TermNameValues.Map,
    "HashMap"
  )

  final val InstantiatedByName: Set[String] = Set(
    TermNameValues.Try,
    TermNameValues.Future
  )

  final val ScalaObjects = Set(
    "Range",
    TermNameValues.ScalaOption,
    TermNameValues.ScalaSome,
    TermNameValues.ScalaRight,
    TermNameValues.ScalaLeft,
    TermNameValues.Try,
    TermNameValues.ScalaSuccess,
    TermNameValues.ScalaFailure,
    TermNameValues.Future
  ) ++ JavaStreamLike ++ JavaListLike ++ JavaSetLike ++ JavaMapLike

  override def isJavaStreamLike(termName: Term.Name): Boolean = JavaStreamLike.contains(termName.value)

  override def isJavaListLike(termName: Term.Name): Boolean = JavaListLike.contains(termName.value)

  override def isJavaSetLike(termName: Term.Name): Boolean = JavaSetLike.contains(termName.value)

  override def isJavaMapLike(termName: Term.Name): Boolean = JavaMapLike.contains(termName.value)

  override def isScalaObject(termName: Term.Name): Boolean = ScalaObjects.contains(termName.value)

  override def isInstantiatedByName(termName: Term.Name): Boolean = InstantiatedByName.contains(termName.value)
}
