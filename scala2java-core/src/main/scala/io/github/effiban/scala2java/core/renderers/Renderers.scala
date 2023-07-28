package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.{ClassClassifier, JavaStatClassifier, TermTreeClassifier}
import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Assign
import scala.meta.{Term, Type}

class Renderers(implicit javaWriter: JavaWriter) {

  private[renderers] lazy val alternativeRenderer: AlternativeRenderer = new AlternativeRendererImpl(patRenderer)

  private[renderers] lazy val annotListRenderer: AnnotListRenderer = new AnnotListRendererImpl(annotRenderer)

  private[renderers] lazy val annotRenderer: AnnotRenderer = new AnnotRendererImpl(initRenderer)

  private[renderers] lazy val applyUnaryRenderer: ApplyUnaryRenderer = new ApplyUnaryRendererImpl(termNameRenderer, expressionTermRenderer)

  private[renderers] val argumentListRenderer: ArgumentListRenderer = new ArgumentListRendererImpl()

  private[renderers] lazy val arrayInitializerRenderer: ArrayInitializerRenderer = new ArrayInitializerRendererImpl(
    typeRenderer,
    expressionTermRenderer,
    new SimpleArgumentRenderer(expressionTermRenderer),
    argumentListRenderer)

  private[renderers] lazy val ascribeRenderer: AscribeRenderer = new AscribeRendererImpl(
    typeRenderer,
    expressionTermRenderer
  )

  private[renderers] lazy val assignInvocationArgRenderer: InvocationArgRenderer[Assign] = new AssignInvocationArgRenderer(
    assignLHSRenderer,
    expressionTermRenderer
  )

  private[renderers] lazy val assignLHSRenderer: AssignLHSRenderer = new AssignLHSRendererImpl(expressionTermRenderer)

  private[renderers] lazy val assignRenderer: AssignRenderer = new AssignRendererImpl(
    assignLHSRenderer,
    expressionTermRenderer
  )

  private[renderers] lazy val blockCoercingTermRenderer: BlockCoercingTermRenderer = new BlockCoercingTermRendererImpl(blockRenderer)

  private[renderers] lazy val blockRenderer: BlockRenderer = new BlockRendererImpl(blockStatRenderer)

  private[renderers] lazy val blockStatRenderer: BlockStatRenderer = new BlockStatRendererImpl(
    statTermRenderer,
    ifRenderer,
    tryRenderer,
    tryWithHandlerRenderer,
    defnVarRenderer,
    declVarRenderer,
    TermTreeClassifier,
    JavaStatClassifier
  )

  private[renderers] lazy val caseClassRenderer: CaseClassRenderer = new CaseClassRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termParamListRenderer,
    templateRenderer
  )

  private[renderers] lazy val caseRenderer: CaseRenderer = new CaseRendererImpl(patRenderer, expressionTermRenderer)

  private[renderers] lazy val catchArgumentRenderer: CatchArgumentRenderer = new CatchArgumentRendererImpl(patRenderer)

  private[renderers] lazy val catchHandlerRenderer: CatchHandlerRenderer = new CatchHandlerRendererImpl(catchArgumentRenderer, blockRenderer)

  private[renderers] lazy val classOfRenderer: ClassOfRenderer = new ClassOfRendererImpl(typeRenderer)

  private lazy val compositeApplyTypeRenderer: CompositeApplyTypeRenderer = new CompositeApplyTypeRendererImpl(
    classOfRenderer,
    standardApplyTypeRenderer
  )

  private[renderers] lazy val compositeInvocationArgRenderer: InvocationArgRenderer[Term] = new CompositeInvocationArgRenderer(
    assignInvocationArgRenderer,
    expressionTermRenderer
  )

  private[renderers] lazy val ctorSecondaryRenderer: CtorSecondaryRenderer = new CtorSecondaryRendererImpl(
    modListRenderer,
    typeNameRenderer,
    termParamListRenderer,
    initRenderer,
    blockStatRenderer
  )

  private[renderers] lazy val declDefRenderer: DeclDefRenderer = new DeclDefRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termNameRenderer,
    typeRenderer,
    termParamListRenderer
  )

  private[renderers] lazy val declRenderer: DeclRenderer = new DeclRendererImpl(declVarRenderer, declDefRenderer)

  private[renderers] lazy val declVarRenderer: DeclVarRenderer = new DeclVarRendererImpl(
    modListRenderer,
    typeRenderer,
    patListRenderer
  )

  private[renderers] lazy val defaultStatRenderer: DefaultStatRenderer = new DefaultStatRendererImpl(
    statTermRenderer,
    importRenderer,
    pkgRenderer,
    declRenderer,
    defnRenderer
  )

  private[renderers] lazy val defaultTermRefRenderer: DefaultTermRefRenderer = new DefaultTermRefRendererImpl(
    thisRenderer,
    superRenderer,
    termNameRenderer,
    defaultTermSelectRenderer
  )

  private[renderers] lazy val defaultTermRenderer: DefaultTermRenderer = new DefaultTermRendererImpl(
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

  private[renderers] lazy val defaultTermSelectRenderer: DefaultTermSelectRenderer = new DefaultTermSelectRendererImpl(
    defaultTermRefRenderer,
    termNameRenderer
  )

  private[renderers] lazy val defnDefRenderer: DefnDefRenderer = new DefnDefRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termNameRenderer,
    typeRenderer,
    termParamListRenderer,
    blockCoercingTermRenderer
  )

  private[renderers] lazy val defnRenderer: DefnRenderer = new DefnRendererImpl(
    defnVarRenderer,
    defnDefRenderer,
    caseClassRenderer,
    regularClassRenderer,
    traitRenderer,
    objectRenderer,
    ClassClassifier
  )

  private[renderers] lazy val defnVarTypeRenderer: DefnVarTypeRenderer = new DefnVarTypeRendererImpl(typeRenderer)

  private[renderers] lazy val defnVarRenderer: DefnVarRenderer = new DefnVarRendererImpl(
    modListRenderer,
    defnVarTypeRenderer,
    patListRenderer,
    expressionTermRenderer
  )

  private[renderers] lazy val doRenderer: DoRenderer = new DoRendererImpl(expressionTermRenderer, defaultTermRenderer)

  private[renderers] lazy val enumConstantListRenderer: EnumConstantListRenderer = new EnumConstantListRendererImpl(argumentListRenderer)

  private[renderers] lazy val etaRenderer: EtaRenderer = new EtaRendererImpl(expressionTermRenderer)

  private[renderers] lazy val expressionTermRenderer: ExpressionTermRenderer = new ExpressionTermRendererImpl(
    expressionTermRefRenderer,
    ifRenderer,
    defaultTermRenderer
  )

  private[renderers] lazy val expressionTermRefRenderer: ExpressionTermRefRenderer = new ExpressionTermRefRendererImpl(
    expressionTermSelectRenderer,
    applyUnaryRenderer,
    defaultTermRefRenderer
  )

  private[renderers] lazy val expressionTermSelectRenderer: ExpressionTermSelectRenderer = new ExpressionTermSelectRendererImpl(
    expressionTermRenderer,
    typeListRenderer,
    termNameRenderer
  )

  private[renderers] lazy val finallyRenderer: FinallyRenderer = new FinallyRendererImpl(blockRenderer)

  private[renderers] lazy val ifRenderer: IfRenderer = new IfRendererImpl(
    expressionTermRenderer,
    blockRenderer,
    defaultTermRenderer
  )

  private[renderers] lazy val importeeRenderer: ImporteeRenderer = new ImporteeRendererImpl(nameIndeterminateRenderer)

  private[renderers] lazy val importRenderer: ImportRenderer = new ImportRendererImpl(importerRenderer)

  private[renderers] lazy val importerRenderer: ImporterRenderer = new ImporterRendererImpl(defaultTermRefRenderer, importeeRenderer)

  private[renderers] lazy val initArgRendererFactory: InitArgRendererFactory = new InitArgRendererFactoryImpl(initRenderer)

  private[renderers] lazy val initListRenderer: InitListRenderer = new InitListRendererImpl(argumentListRenderer, initArgRendererFactory)

  private[renderers] lazy val initRenderer: InitRenderer = new InitRendererImpl(
    typeRenderer,
    argumentListRenderer,
    compositeInvocationArgRenderer
  )

  private[renderers] val litRenderer: LitRenderer = new LitRendererImpl()

  private[renderers] lazy val modListRenderer: ModListRenderer = new ModListRendererImpl(annotListRenderer, JavaModifierOrdering)

  private[renderers] lazy val nameIndeterminateRenderer: NameIndeterminateRenderer = new NameIndeterminateRendererImpl()

  private[renderers] val nameRenderer: NameRenderer = new NameRendererImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameRenderer
  )

  private[renderers] lazy val newRenderer: NewRenderer = new NewRendererImpl(
    initRenderer,
    arrayInitializerRenderer,
    ArrayInitializerRenderContextResolver
  )

  private[renderers] lazy val objectRenderer: ObjectRenderer = new ObjectRendererImpl(modListRenderer, templateRenderer)

  private[renderers] lazy val patListRenderer: PatListRenderer = new PatListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer(patRenderer)
  )

  private[renderers] lazy val patRenderer: PatRenderer = new PatRendererImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patVarRenderer,
    alternativeRenderer,
    patTypedRenderer
  )

  private[renderers] val patVarRenderer: PatVarRenderer = new PatVarRendererImpl(termNameRenderer)

  private[renderers] lazy val patTypedRenderer: PatTypedRenderer = new PatTypedRendererImpl(typeRenderer, patRenderer)

  private[renderers] val patWildcardRenderer: PatWildcardRenderer = new PatWildcardRendererImpl()

  private[renderers] lazy val pkgRenderer: PkgRenderer = new PkgRendererImpl(defaultTermRefRenderer, defaultStatRenderer)

  private[renderers] lazy val permittedSubTypeNameListRenderer = new PermittedSubTypeNameListRendererImpl(argumentListRenderer)

  private[renderers] lazy val regularClassRenderer: RegularClassRenderer = new RegularClassRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    templateRenderer
  )

  private[renderers] lazy val returnRenderer: ReturnRenderer = new ReturnRendererImpl(expressionTermRenderer)

  private[renderers] val selfRenderer: SelfRenderer = new SelfRendererImpl()

  val sourceRenderer: SourceRenderer = new SourceRendererImpl(defaultStatRenderer)

  private[renderers] lazy val standardApplyTypeRenderer: StandardApplyTypeRenderer = new StandardApplyTypeRendererImpl(
    expressionTermSelectRenderer,
    typeListRenderer,
    expressionTermRenderer
  )

  private[renderers] lazy val statTermRenderer: StatTermRenderer = new StatTermRendererImpl(
    expressionTermRefRenderer,
    defaultTermRenderer
  )

  private[renderers] val superRenderer: SuperRenderer = new SuperRendererImpl(nameRenderer)

  private[renderers] lazy val templateBodyRenderer: TemplateBodyRenderer = new TemplateBodyRendererImpl(templateStatRenderer)

  private[renderers] lazy val templateRenderer: TemplateRenderer = new TemplateRendererImpl(
    initListRenderer,
    selfRenderer,
    templateBodyRenderer,
    permittedSubTypeNameListRenderer
  )

  private[renderers] lazy val templateStatRenderer: TemplateStatRenderer = new TemplateStatRendererImpl(
    enumConstantListRenderer,
    ctorSecondaryRenderer,
    defaultStatRenderer,
    JavaStatClassifier
  )

  private[renderers] lazy val termAnnotateRenderer: TermAnnotateRenderer = new TermAnnotateRendererImpl(annotListRenderer, expressionTermRenderer)

  private[renderers] lazy val termApplyInfixRenderer: TermApplyInfixRenderer = new TermApplyInfixRendererImpl(
    expressionTermRenderer,
    termNameRenderer
  )

  private[renderers] lazy val termApplyRenderer: TermApplyRenderer = new TermApplyRendererImpl(
    expressionTermRenderer,
    arrayInitializerRenderer,
    argumentListRenderer,
    compositeInvocationArgRenderer,
    ArrayInitializerRenderContextResolver
  )

  private[renderers] lazy val termFunctionRenderer: TermFunctionRenderer = new TermFunctionRendererImpl(
    termParamRenderer,
    termParamListRenderer,
    blockRenderer,
    defaultTermRenderer
  )

  private[renderers] lazy val termMatchRenderer: TermMatchRenderer = new TermMatchRendererImpl(expressionTermRenderer, caseRenderer)

  private[renderers] lazy val termNameRenderer: TermNameRenderer = new TermNameRendererImpl()

  private[renderers] lazy val termParamArgumentRenderer: TermParamArgumentRenderer = new TermParamArgumentRenderer(termParamRenderer)

  private[renderers] lazy val termParamListRenderer: TermParamListRenderer = new TermParamListRendererImpl(
    argumentListRenderer,
    termParamArgumentRenderer
  )

  private[renderers] lazy val termParamRenderer: TermParamRenderer = new TermParamRendererImpl(
    modListRenderer,
    typeRenderer,
    nameRenderer
  )

  private[renderers] val termPlaceholderRenderer: TermPlaceholderRenderer = new TermPlaceholderRendererImpl()

  private[renderers] val thisRenderer: ThisRenderer = new ThisRendererImpl(nameRenderer)

  private[renderers] lazy val throwRenderer: ThrowRenderer = new ThrowRendererImpl(expressionTermRenderer)

  private[renderers] lazy val traitRenderer: TraitRenderer = new TraitRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    templateRenderer
  )

  private[renderers] lazy val tryRenderer: TryRenderer = new TryRendererImpl(
    blockRenderer,
    catchHandlerRenderer,
    finallyRenderer
  )

  private[renderers] lazy val tryWithHandlerRenderer: TryWithHandlerRenderer = new TryWithHandlerRendererImpl(blockRenderer, finallyRenderer)

  private[renderers] lazy val typeAnnotateRenderer: TypeAnnotateRenderer = new TypeAnnotateRendererImpl(typeRenderer)

  private[renderers] val typeAnonymousParamRenderer: TypeAnonymousParamRenderer = new TypeAnonymousParamRendererImpl()

  private[renderers] val typeApplyInfixRenderer: TypeApplyInfixRenderer = new TypeApplyInfixRendererImpl()

  private[renderers] lazy val typeApplyRenderer: TypeApplyRenderer = new TypeApplyRendererImpl(typeRenderer, typeListRenderer)

  private[renderers] lazy val typeBoundsRenderer: TypeBoundsRenderer = new TypeBoundsRendererImpl(typeRenderer)

  private[renderers] lazy val typeExistentialRenderer: TypeExistentialRenderer = new TypeExistentialRendererImpl(typeRenderer)

  private[renderers] lazy val typeListRenderer: TypeListRenderer = new TypeListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer(typeRenderer)
  )

  private[renderers] lazy val typeNameRenderer: TypeNameRenderer = new TypeNameRendererImpl()

  private[renderers] lazy val typeParamRenderer: TypeParamRenderer = new TypeParamRendererImpl(
    nameRenderer,
    typeParamListRenderer,
    typeBoundsRenderer
  )

  private[renderers] lazy val typeParamListRenderer: TypeParamListRenderer = new TypeParamListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer[Type.Param](typeParamRenderer)
  )

  private[renderers] lazy val typeProjectRenderer: TypeProjectRenderer = new TypeProjectRendererImpl(
    typeRenderer,
    typeNameRenderer
  )

  private[renderers] lazy val typeRefRenderer: TypeRefRenderer = new TypeRefRendererImpl(
    typeNameRenderer,
    typeSelectRenderer,
    typeProjectRenderer,
    typeSingletonRenderer
  )

  private[renderers] lazy val typeRefineRenderer: TypeRefineRenderer = new TypeRefineRendererImpl(typeRenderer)

  private[renderers] lazy val typeRenderer: TypeRenderer = new TypeRendererImpl(
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

  private[renderers] lazy val typeRepeatedRenderer: TypeRepeatedRenderer = new TypeRepeatedRendererImpl(typeRenderer)

  private[renderers] lazy val typeSelectRenderer: TypeSelectRenderer = new TypeSelectRendererImpl(defaultTermRefRenderer, typeNameRenderer)

  private[renderers] val typeSingletonRenderer: TypeSingletonRenderer = new TypeSingletonRendererImpl(thisRenderer)

  private[renderers] val typeVarRenderer: TypeVarRenderer = new TypeVarRendererImpl()

  private[renderers] lazy val typeWildcardRenderer: TypeWildcardRenderer = new TypeWildcardRendererImpl(typeBoundsRenderer)

  private[renderers] lazy val typeWithRenderer: TypeWithRenderer = new TypeWithRendererImpl(typeRenderer)

  private[renderers] lazy val whileRenderer: WhileRenderer = new WhileRendererImpl(expressionTermRenderer, defaultTermRenderer)
}
