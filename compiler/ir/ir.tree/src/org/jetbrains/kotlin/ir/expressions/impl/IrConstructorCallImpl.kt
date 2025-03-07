/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.initializeParameterArguments
import org.jetbrains.kotlin.ir.util.initializeTypeArguments
import org.jetbrains.kotlin.ir.util.parentAsClass

class IrConstructorCallImpl(
    override val startOffset: Int,
    override val endOffset: Int,
    override var type: IrType,
    override val symbol: IrConstructorSymbol,
    typeArgumentsCount: Int,
    override var constructorTypeArgumentsCount: Int,
    valueArgumentsCount: Int,
    override var origin: IrStatementOrigin? = null,
    override var source: SourceElement = SourceElement.NO_SOURCE
) : IrConstructorCall() {
    override val typeArguments: Array<IrType?> = initializeTypeArguments(typeArgumentsCount)

    override val valueArguments: Array<IrExpression?> = initializeParameterArguments(valueArgumentsCount)

    override var contextReceiversCount = 0

    companion object {
        @ObsoleteDescriptorBasedAPI
        fun fromSymbolDescriptor(
            startOffset: Int,
            endOffset: Int,
            type: IrType,
            constructorSymbol: IrConstructorSymbol,
            origin: IrStatementOrigin? = null
        ): IrConstructorCallImpl {
            val constructorDescriptor = constructorSymbol.descriptor
            val classTypeParametersCount = constructorDescriptor.constructedClass.original.declaredTypeParameters.size
            val totalTypeParametersCount = constructorDescriptor.typeParameters.size
            val valueParametersCount = constructorDescriptor.valueParameters.size + constructorDescriptor.contextReceiverParameters.size
            return IrConstructorCallImpl(
                startOffset, endOffset,
                type,
                constructorSymbol,
                typeArgumentsCount = totalTypeParametersCount,
                constructorTypeArgumentsCount = totalTypeParametersCount - classTypeParametersCount,
                valueArgumentsCount = valueParametersCount,
                origin = origin
            )
        }

        fun fromSymbolOwner(
            startOffset: Int,
            endOffset: Int,
            type: IrType,
            constructorSymbol: IrConstructorSymbol,
            classTypeParametersCount: Int,
            origin: IrStatementOrigin? = null
        ): IrConstructorCallImpl {
            val constructor = constructorSymbol.owner
            val constructorTypeParametersCount = constructor.typeParameters.size
            val totalTypeParametersCount = classTypeParametersCount + constructorTypeParametersCount
            val valueParametersCount = constructor.valueParameters.size

            return IrConstructorCallImpl(
                startOffset, endOffset,
                type,
                constructorSymbol,
                totalTypeParametersCount,
                constructorTypeParametersCount,
                valueParametersCount,
                origin
            )
        }

        fun fromSymbolOwner(
            startOffset: Int,
            endOffset: Int,
            type: IrType,
            constructorSymbol: IrConstructorSymbol,
            origin: IrStatementOrigin? = null
        ): IrConstructorCallImpl {
            val constructedClass = constructorSymbol.owner.parentAsClass
            val classTypeParametersCount = constructedClass.typeParameters.size
            return fromSymbolOwner(startOffset, endOffset, type, constructorSymbol, classTypeParametersCount, origin)
        }

        fun fromSymbolOwner(
            type: IrType,
            constructorSymbol: IrConstructorSymbol,
            origin: IrStatementOrigin? = null
        ): IrConstructorCallImpl =
            fromSymbolOwner(
                UNDEFINED_OFFSET, UNDEFINED_OFFSET, type, constructorSymbol, constructorSymbol.owner.parentAsClass.typeParameters.size,
                origin
            )
    }
}
