package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.{ClassClassifier, JavaStatClassifier, TermTreeClassifier}
import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Assign
import scala.meta.{Term, Type}

class Renderers(implicit javaWriter: JavaWriter) {

  lazy val alternativeRenderer: AlternativeRenderer = new AlternativeRendererImpl(patRenderer)

  lazy val annotListRenderer: AnnotListRenderer = new AnnotListRendererImpl(annotRenderer)

  lazy val annotRenderer: AnnotRenderer = new AnnotRendererImpl(initRenderer)

  lazy val applyUnaryRenderer: ApplyUnaryRenderer = new ApplyUnaryRendererImpl(termNameRenderer, expressionTermRenderer)

  val argumentListRenderer: ArgumentListRenderer = new ArgumentListRendererImpl()

  lazy val arrayInitializerRenderer: ArrayInitializerRenderer = new ArrayInitializerRendererImpl(
    typeRenderer,
    expressionTermRenderer,
    new SimpleArgumentRenderer(expressionTermRenderer),
    argumentListRenderer)

  lazy val ascribeRenderer: AscribeRenderer = new AscribeRendererImpl(
    typeRenderer,
    expressionTermRenderer
  )

  lazy val assignInvocationArgRenderer: InvocationArgRenderer[Assign] = new AssignInvocationArgRenderer(
    assignLHSRenderer,
    expressionTermRenderer
  )

  lazy val assignLHSRenderer: AssignLHSRenderer = new AssignLHSRendererImpl(expressionTermRenderer)

  lazy val assignRenderer: AssignRenderer = new AssignRendererImpl(
    assignLHSRenderer,
    expressionTermRenderer
  )

  lazy val blockCoercingTermRenderer: BlockCoercingTermRenderer = new BlockCoercingTermRendererImpl(blockRenderer)

  lazy val blockRenderer: BlockRenderer = new BlockRendererImpl(blockStatRenderer)

  lazy val blockStatRenderer: BlockStatRenderer = new BlockStatRendererImpl(
    statTermRenderer,
    ifRenderer,
    tryRenderer,
    tryWithHandlerRenderer,
    defnVarRenderer,
    declVarRenderer,
    TermTreeClassifier,
    JavaStatClassifier
  )

  lazy val caseClassRenderer: CaseClassRenderer = new CaseClassRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termParamListRenderer,
    templateRenderer
  )

  lazy val caseRenderer: CaseRenderer = new CaseRendererImpl(patRenderer, expressionTermRenderer)

  lazy val catchArgumentRenderer: CatchArgumentRenderer = new CatchArgumentRendererImpl(patRenderer)

  lazy val catchHandlerRenderer: CatchHandlerRenderer = new CatchHandlerRendererImpl(catchArgumentRenderer, blockRenderer)

  lazy val classOfRenderer: ClassOfRenderer = new ClassOfRendererImpl(typeRenderer)

  private lazy val compositeApplyTypeRenderer: CompositeApplyTypeRenderer = new CompositeApplyTypeRendererImpl(
    classOfRenderer,
    standardApplyTypeRenderer
  )

  lazy val compositeInvocationArgRenderer: InvocationArgRenderer[Term] = new CompositeInvocationArgRenderer(
    assignInvocationArgRenderer,
    expressionTermRenderer
  )

  lazy val ctorSecondaryRenderer: CtorSecondaryRenderer = new CtorSecondaryRendererImpl(
    modListRenderer,
    typeNameRenderer,
    termParamListRenderer,
    initRenderer,
    blockStatRenderer
  )

  lazy val declDefRenderer: DeclDefRenderer = new DeclDefRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termNameRenderer,
    typeRenderer,
    termParamListRenderer
  )

  lazy val declRenderer: DeclRenderer = new DeclRendererImpl(declVarRenderer, declDefRenderer)

  lazy val declVarRenderer: DeclVarRenderer = new DeclVarRendererImpl(
    modListRenderer,
    typeRenderer,
    patListRenderer
  )

  lazy val defaultStatRenderer: DefaultStatRenderer = new DefaultStatRendererImpl(
    statTermRenderer,
    importRenderer,
    declRenderer,
    defnRenderer
  )

  lazy val defaultTermRefRenderer: DefaultTermRefRenderer = new DefaultTermRefRendererImpl(
    thisRenderer,
    superRenderer,
    termNameRenderer,
    defaultTermSelectRenderer
  )

  lazy val defaultTermRenderer: DefaultTermRenderer = new DefaultTermRendererImpl(
    defaultTermRefRenderer,
    termApplyRenderer,
    compositeApplyTypeRenderer,
    termApplyInfixRenderer,
    assignRenderer,
    returnRenderer,
    throwRenderer,
    ascribeRenderer,
    termAnnotateRenderer,
    blockRenderer,
    ifRenderer,
    termMatchRenderer,
    tryRenderer,
    tryWithHandlerRenderer,
    termFunctionRenderer,
    whileRenderer,
    doRenderer,
    newRenderer,
    termPlaceholderRenderer,
    etaRenderer,
    litRenderer
  )

  lazy val defaultTermSelectRenderer: DefaultTermSelectRenderer = new DefaultTermSelectRendererImpl(
    defaultTermRefRenderer,
    termNameRenderer
  )

  lazy val defnDefRenderer: DefnDefRenderer = new DefnDefRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termNameRenderer,
    typeRenderer,
    termParamListRenderer,
    blockCoercingTermRenderer
  )

  lazy val defnRenderer: DefnRenderer = new DefnRendererImpl(
    defnVarRenderer,
    defnDefRenderer,
    caseClassRenderer,
    regularClassRenderer,
    traitRenderer,
    objectRenderer,
    ClassClassifier
  )

  lazy val defnVarTypeRenderer: DefnVarTypeRenderer = new DefnVarTypeRendererImpl(typeRenderer)

  lazy val defnVarRenderer: DefnVarRenderer = new DefnVarRendererImpl(
    modListRenderer,
    defnVarTypeRenderer,
    patListRenderer,
    expressionTermRenderer
  )

  lazy val doRenderer: DoRenderer = new DoRendererImpl(expressionTermRenderer, defaultTermRenderer)

  lazy val enumConstantListRenderer: EnumConstantListRenderer = new EnumConstantListRendererImpl(argumentListRenderer)

  lazy val etaRenderer: EtaRenderer = new EtaRendererImpl(expressionTermRenderer)

  lazy val expressionTermRenderer: ExpressionTermRenderer = new ExpressionTermRendererImpl(
    expressionTermRefRenderer,
    ifRenderer,
    defaultTermRenderer
  )

  lazy val expressionTermRefRenderer: ExpressionTermRefRenderer = new ExpressionTermRefRendererImpl(
    expressionTermSelectRenderer,
    applyUnaryRenderer,
    defaultTermRefRenderer
  )

  lazy val expressionTermSelectRenderer: ExpressionTermSelectRenderer = new ExpressionTermSelectRendererImpl(
    expressionTermRenderer,
    typeListRenderer,
    termNameRenderer
  )

  lazy val finallyRenderer: FinallyRenderer = new FinallyRendererImpl(blockRenderer)

  lazy val ifRenderer: IfRenderer = new IfRendererImpl(
    expressionTermRenderer,
    blockRenderer,
    defaultTermRenderer
  )

  lazy val importeeRenderer: ImporteeRenderer = new ImporteeRendererImpl(nameIndeterminateRenderer)

  lazy val importRenderer: ImportRenderer = new ImportRendererImpl(importerRenderer)

  lazy val importerRenderer: ImporterRenderer = new ImporterRendererImpl(defaultTermRefRenderer, importeeRenderer)

  lazy val initArgRendererFactory: InitArgRendererFactory = new InitArgRendererFactoryImpl(initRenderer)

  lazy val initListRenderer: InitListRenderer = new InitListRendererImpl(argumentListRenderer, initArgRendererFactory)

  lazy val initRenderer: InitRenderer = new InitRendererImpl(
    typeRenderer,
    argumentListRenderer,
    compositeInvocationArgRenderer
  )

  val litRenderer: LitRenderer = new LitRendererImpl()

  lazy val modListRenderer: ModListRenderer = new ModListRendererImpl(annotListRenderer, JavaModifierOrdering)

  lazy val nameIndeterminateRenderer: NameIndeterminateRenderer = new NameIndeterminateRendererImpl()

  val nameRenderer: NameRenderer = new NameRendererImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameRenderer
  )

  lazy val newRenderer: NewRenderer = new NewRendererImpl(
    initRenderer,
    arrayInitializerRenderer,
    ArrayInitializerRenderContextResolver
  )

  lazy val objectRenderer: ObjectRenderer = new ObjectRendererImpl(modListRenderer, templateRenderer)

  lazy val patListRenderer: PatListRenderer = new PatListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer(patRenderer)
  )

  lazy val patRenderer: PatRenderer = new PatRendererImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patVarRenderer,
    alternativeRenderer,
    patTypedRenderer
  )

  val patVarRenderer: PatVarRenderer = new PatVarRendererImpl(termNameRenderer)

  lazy val patTypedRenderer: PatTypedRenderer = new PatTypedRendererImpl(typeRenderer, patRenderer)

  val patWildcardRenderer: PatWildcardRenderer = new PatWildcardRendererImpl()

  lazy val pkgRenderer: PkgRenderer = new PkgRendererImpl(defaultTermRefRenderer, defaultStatRenderer)

  lazy val permittedSubTypeNameListRenderer = new PermittedSubTypeNameListRendererImpl(argumentListRenderer)

  lazy val regularClassRenderer: RegularClassRenderer = new RegularClassRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    templateRenderer
  )

  lazy val returnRenderer: ReturnRenderer = new ReturnRendererImpl(expressionTermRenderer)

  val selfRenderer: SelfRenderer = new SelfRendererImpl()

  lazy val standardApplyTypeRenderer: StandardApplyTypeRenderer = new StandardApplyTypeRendererImpl(
    expressionTermSelectRenderer,
    typeListRenderer,
    expressionTermRenderer
  )

  lazy val statTermRenderer: StatTermRenderer = new StatTermRendererImpl(
    expressionTermRefRenderer,
    defaultTermRenderer
  )

  val superRenderer: SuperRenderer = new SuperRendererImpl(nameRenderer)

  lazy val templateBodyRenderer: TemplateBodyRenderer = new TemplateBodyRendererImpl(templateStatRenderer)

  lazy val templateRenderer: TemplateRenderer = new TemplateRendererImpl(
    initListRenderer,
    selfRenderer,
    templateBodyRenderer,
    permittedSubTypeNameListRenderer
  )

  lazy val templateStatRenderer: TemplateStatRenderer = new TemplateStatRendererImpl(
    enumConstantListRenderer,
    defaultStatRenderer,
    JavaStatClassifier
  )

  lazy val termAnnotateRenderer: TermAnnotateRenderer = new TermAnnotateRendererImpl(annotListRenderer, expressionTermRenderer)

  lazy val termApplyInfixRenderer: TermApplyInfixRenderer = new TermApplyInfixRendererImpl(
    expressionTermRenderer,
    termNameRenderer
  )

  lazy val termApplyRenderer: TermApplyRenderer = new TermApplyRendererImpl(
    expressionTermRenderer,
    arrayInitializerRenderer,
    argumentListRenderer,
    compositeInvocationArgRenderer,
    ArrayInitializerRenderContextResolver
  )

  lazy val termFunctionRenderer: TermFunctionRenderer = new TermFunctionRendererImpl(
    termParamRenderer,
    termParamListRenderer,
    blockRenderer,
    defaultTermRenderer
  )

  lazy val termMatchRenderer: TermMatchRenderer = new TermMatchRendererImpl(expressionTermRenderer, caseRenderer)

  lazy val termNameRenderer: TermNameRenderer = new TermNameRendererImpl()

  lazy val termParamArgumentRenderer: TermParamArgumentRenderer = new TermParamArgumentRenderer(termParamRenderer)

  lazy val termParamListRenderer: TermParamListRenderer = new TermParamListRendererImpl(
    argumentListRenderer,
    termParamArgumentRenderer
  )

  lazy val termParamRenderer: TermParamRenderer = new TermParamRendererImpl(
    modListRenderer,
    typeRenderer,
    nameRenderer
  )

  val termPlaceholderRenderer: TermPlaceholderRenderer = new TermPlaceholderRendererImpl()

  val thisRenderer: ThisRenderer = new ThisRendererImpl(nameRenderer)

  lazy val throwRenderer: ThrowRenderer = new ThrowRendererImpl(expressionTermRenderer)

  lazy val traitRenderer: TraitRenderer = new TraitRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    templateRenderer
  )

  lazy val tryRenderer: TryRenderer = new TryRendererImpl(
    blockRenderer,
    catchHandlerRenderer,
    finallyRenderer
  )

  lazy val tryWithHandlerRenderer: TryWithHandlerRenderer = new TryWithHandlerRendererImpl(blockRenderer, finallyRenderer)

  lazy val typeAnnotateRenderer: TypeAnnotateRenderer = new TypeAnnotateRendererImpl(typeRenderer)

  val typeAnonymousParamRenderer: TypeAnonymousParamRenderer = new TypeAnonymousParamRendererImpl()

  val typeApplyInfixRenderer: TypeApplyInfixRenderer = new TypeApplyInfixRendererImpl()

  lazy val typeApplyRenderer: TypeApplyRenderer = new TypeApplyRendererImpl(typeRenderer, typeListRenderer)

  lazy val typeBoundsRenderer: TypeBoundsRenderer = new TypeBoundsRendererImpl(typeRenderer)

  lazy val typeExistentialRenderer: TypeExistentialRenderer = new TypeExistentialRendererImpl(typeRenderer)

  lazy val typeListRenderer: TypeListRenderer = new TypeListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer(typeRenderer)
  )

  lazy val typeNameRenderer: TypeNameRenderer = new TypeNameRendererImpl()

  lazy val typeParamRenderer: TypeParamRenderer = new TypeParamRendererImpl(
    nameRenderer,
    typeParamListRenderer,
    typeBoundsRenderer
  )

  lazy val typeParamListRenderer: TypeParamListRenderer = new TypeParamListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer[Type.Param](typeParamRenderer)
  )

  lazy val typeProjectRenderer: TypeProjectRenderer = new TypeProjectRendererImpl(
    typeRenderer,
    typeNameRenderer
  )

  lazy val typeRefRenderer: TypeRefRenderer = new TypeRefRendererImpl(
    typeNameRenderer,
    typeSelectRenderer,
    typeProjectRenderer,
    typeSingletonRenderer
  )

  lazy val typeRefineRenderer: TypeRefineRenderer = new TypeRefineRendererImpl(typeRenderer)

  lazy val typeRenderer: TypeRenderer = new TypeRendererImpl(
    typeRefRenderer,
    typeApplyRenderer,
    typeApplyInfixRenderer,
    typeWithRenderer,
    typeRefineRenderer,
    typeExistentialRenderer,
    typeAnnotateRenderer,
    typeAnonymousParamRenderer,
    typeWildcardRenderer,
    typeRepeatedRenderer,
    typeVarRenderer
  )

  lazy val typeRepeatedRenderer: TypeRepeatedRenderer = new TypeRepeatedRendererImpl(typeRenderer)

  lazy val typeSelectRenderer: TypeSelectRenderer = new TypeSelectRendererImpl(defaultTermRefRenderer, typeNameRenderer)

  val typeSingletonRenderer: TypeSingletonRenderer = new TypeSingletonRendererImpl(thisRenderer)

  val typeVarRenderer: TypeVarRenderer = new TypeVarRendererImpl()

  lazy val typeWildcardRenderer: TypeWildcardRenderer = new TypeWildcardRendererImpl(typeBoundsRenderer)

  lazy val typeWithRenderer: TypeWithRenderer = new TypeWithRendererImpl(typeRenderer)

  lazy val whileRenderer: WhileRenderer = new WhileRendererImpl(expressionTermRenderer, defaultTermRenderer)
}
