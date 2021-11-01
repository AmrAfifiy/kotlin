/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.components

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.analysis.api.analyse
import org.jetbrains.kotlin.analysis.api.fir.FirFrontendApiTestConfiguratorService
import org.jetbrains.kotlin.analysis.api.impl.barebone.test.expressionMarkerProvider
import org.jetbrains.kotlin.analysis.api.impl.base.test.test.framework.AbstractHLApiSingleFileTest
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.elements.KtLightElement
import org.jetbrains.kotlin.light.classes.symbol.classes.getOrCreateFirLightClass
import org.jetbrains.kotlin.light.classes.symbol.classes.getOrCreateFirLightFacade
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions

abstract class AbstractPsiTypeProviderTest : AbstractHLApiSingleFileTest(FirFrontendApiTestConfiguratorService) {

    override fun doTestByFileStructure(ktFile: KtFile, module: TestModule, testServices: TestServices) {
        val mainKtFileFqName = ktFile.packageFqName.child(Name.identifier(ktFile.name))
        val declaration = testServices.expressionMarkerProvider.getElementOfTypAtCaret<KtDeclaration>(ktFile)
        val containingClass =
            declaration.parents.firstOrNull { it is KtClassOrObject }?.let { getOrCreateFirLightClass(it as KtClassOrObject) }
                ?: getOrCreateFirLightFacade(listOf(ktFile), mainKtFileFqName)
                ?: error("Can't get or create containing KtLightClass for $declaration")
        val psiContext = containingClass.findLightDeclarationContext(declaration)
            ?: error("Can't find psi context for $declaration")
        val actual = buildString {
            executeOnPooledThreadInReadAction {
                analyse(declaration) {
                    val ktType = declaration.getReturnKtType()
                    appendLine("KtType: ${ktType.render()}")
                    appendLine("PsiType: ${ktType.asPsiType(psiContext)}")
                }
            }
        }
        testServices.assertions.assertEqualsToTestDataFileSibling(actual)
    }

    private fun KtLightClass.findLightDeclarationContext(ktDeclaration: KtDeclaration): KtLightElement<*, *>? {
        val selfOrParents = listOf(ktDeclaration) + ktDeclaration.parents.filterIsInstance<KtDeclaration>()
        var result: KtLightElement<*, *>? = null
        val visitor = object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element !is KtLightElement<*, *>) return
                // NB: intentionally visit members first so that `self` can be found first if matched
                if (element is PsiClass) {
                    element.fields.forEach { it.accept(this) }
                    element.methods.forEach { it.accept(this) }
                    element.innerClasses.forEach { it.accept(this) }
                }
                if (result == null && element.kotlinOrigin in selfOrParents) {
                    result = element
                    return
                }
            }
        }
        accept(visitor)
        return result
    }

}
