/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.numbers.primitives

import org.jetbrains.kotlin.generators.builtins.PrimitiveType
import java.io.PrintWriter
import java.util.*

class WasmPrimitivesGenerator(writer: PrintWriter) : BasePrimitivesGenerator(writer) {
    override fun FileBuilder.modifyGeneratedFile() {
        suppress("OVERRIDE_BY_INLINE")
        suppress("NOTHING_TO_INLINE")
        suppress("unused")
        suppress("UNUSED_PARAMETER")
        import("kotlin.wasm.internal.*")
    }

    override fun ClassBuilder.modifyGeneratedClass(thisKind: PrimitiveType) {
        annotations += "WasmAutoboxed"
        // used here little hack with name extension just to avoid creation of specialized "ConstructorParameterDescription"
        constructorParam {
            name = "private val value"
            type = thisKind.capitalized
        }
    }

    override fun CompanionObjectBuilder.modifyGeneratedCompanionObject(thisKind: PrimitiveType) {
        isPublic = true
    }

    override fun primitiveConstants(type: PrimitiveType): List<Any> {
        return when (type) {
            PrimitiveType.FLOAT -> listOf(
                String.format(Locale.US, "%.17eF", java.lang.Float.MIN_VALUE),
                String.format(Locale.US, "%.17eF", java.lang.Float.MAX_VALUE),
                "1.0F/0.0F", "-1.0F/0.0F", "-(0.0F/0.0F)"
            )
            else -> super.primitiveConstants(type)
        }
    }

    override fun PropertyBuilder.modifyGeneratedCompanionObjectProperty(thisKind: PrimitiveType) {
        if (this.name in setOf("POSITIVE_INFINITY", "NEGATIVE_INFINITY", "NaN")) {
            annotations += "Suppress(\"DIVISION_BY_ZERO\")"
        }
    }

    override fun MethodBuilder.modifyGeneratedCompareTo(thisKind: PrimitiveType, otherKind: PrimitiveType) {
        if (thisKind != otherKind || thisKind !in PrimitiveType.floatingPoint) {
            modifySignature { isInline = true }
        }

        if (otherKind == thisKind) {
            if (thisKind in PrimitiveType.floatingPoint) {
                """
                    // if any of values in NaN both comparisons return false
                    if (this > $parameterName) return 1
                    if (this < $parameterName) return -1
            
                    val thisBits = this.toBits()
                    val otherBits = $parameterName.toBits()
            
                    // Canonical NaN bit representation is higher than any other value's bit representation
                    return thisBits.compareTo(otherBits)
                """.trimIndent().addAsMultiLineBody()
            } else {
                val body = when (thisKind) {
                    PrimitiveType.BYTE -> "wasm_i32_compareTo(this.toInt(), $parameterName.toInt())"
                    PrimitiveType.SHORT -> "this.toInt().compareTo($parameterName.toInt())"
                    PrimitiveType.INT, PrimitiveType.LONG -> "wasm_${thisKind.prefixLowercase}_compareTo(this, $parameterName)"
                    else -> throw IllegalArgumentException("Unsupported type $thisKind for generation `compareTo` method")
                }
                body.addAsSingleLineBody(bodyOnNewLine = true)
            }
            return
        }

        val thisCasted = "this" + thisKind.castToIfNecessary(otherKind)
        val otherCasted = parameterName + otherKind.castToIfNecessary(thisKind)
        when {
            thisKind == PrimitiveType.FLOAT && otherKind == PrimitiveType.DOUBLE -> "-${otherCasted}.compareTo(this)"
            else -> "$thisCasted.compareTo($otherCasted)"
        }.addAsSingleLineBody(bodyOnNewLine = true)
    }

    override fun MethodBuilder.modifyGeneratedBinaryOperation(thisKind: PrimitiveType, otherKind: PrimitiveType) {
        val sign = operatorSign(methodName)
        if (thisKind != PrimitiveType.BYTE && thisKind != PrimitiveType.SHORT && thisKind == otherKind) {
            val type = thisKind.capitalized

            when (methodName) {
                "div" -> {
                    val oneConst = if (thisKind == PrimitiveType.LONG) "-1L" else "-1"
                    when (thisKind) {
                        PrimitiveType.INT, PrimitiveType.LONG -> "if (this == $type.MIN_VALUE && $parameterName == $oneConst) $type.MIN_VALUE " +
                                "else wasm_${thisKind.prefixLowercase}_div_s(this, $parameterName)"
                        else -> return implementAsIntrinsic(thisKind, methodName)
                    }
                }
                "rem" -> when (thisKind) {
                    in PrimitiveType.floatingPoint -> "this - (wasm_${thisKind.prefixLowercase}_nearest(this / $parameterName) * $parameterName)"
                    else -> return implementAsIntrinsic(thisKind, methodName)
                }
                else -> return implementAsIntrinsic(thisKind, methodName)
            }.addAsSingleLineBody(bodyOnNewLine = true)
            return
        }

        modifySignature { isInline = true }
        val returnTypeAsPrimitive = PrimitiveType.valueOf(returnType.uppercase())
        val thisCasted = "this" + thisKind.castToIfNecessary(returnTypeAsPrimitive)
        val otherCasted = parameterName + parameterType.toPrimitiveType().castToIfNecessary(returnTypeAsPrimitive)
        "$thisCasted $sign $otherCasted".addAsSingleLineBody(bodyOnNewLine = true)
    }

    override fun MethodBuilder.modifyGeneratedUnaryOperation(thisKind: PrimitiveType) {
        if (thisKind == PrimitiveType.INT && methodName == "dec") {
            additionalDoc = "TODO: Fix test compiler/testData/codegen/box/functions/invoke/invoke.kt with inline dec"
        } else {
            modifySignature { isInline = true }
        }

        if (methodName in setOf("inc", "dec")) {
            val sign = if (methodName == "inc") "+" else "-"
            when (thisKind) {
                PrimitiveType.BYTE, PrimitiveType.SHORT -> "(this $sign 1).to${thisKind.capitalized}()".addAsSingleLineBody(bodyOnNewLine = true)
                PrimitiveType.INT -> "this $sign 1".addAsSingleLineBody(bodyOnNewLine = true)
                PrimitiveType.LONG -> "this $sign 1L".addAsSingleLineBody(bodyOnNewLine = true)
                PrimitiveType.FLOAT -> "this $sign 1.0f".addAsSingleLineBody(bodyOnNewLine = true)
                PrimitiveType.DOUBLE -> "this $sign 1.0".addAsSingleLineBody(bodyOnNewLine = true)
                else -> Unit
            }
        }

        if (methodName in setOf("unaryMinus", "unaryPlus")) {
            if (thisKind in PrimitiveType.floatingPoint && methodName == "unaryMinus") {
                return implementAsIntrinsic(thisKind, methodName)
            }

            val returnTypeAsPrimitive = PrimitiveType.valueOf(returnType.uppercase())
            val thisCasted = "this" + thisKind.castToIfNecessary(returnTypeAsPrimitive)
            val sign = if (methodName == "unaryMinus") {
                when (thisKind) {
                    PrimitiveType.INT -> "0 - "
                    PrimitiveType.LONG -> "0L - "
                    else -> "-"
                }
            } else ""
            "$sign$thisCasted".addAsSingleLineBody(bodyOnNewLine = true)
        }
    }

    override fun MethodBuilder.modifyGeneratedRangeTo(thisKind: PrimitiveType) {
        val rangeType = PrimitiveType.valueOf(returnType.replace("Range", "").uppercase())
        val thisCasted = "this" + thisKind.castToIfNecessary(rangeType)
        val otherCasted = parameterName + parameterType.toPrimitiveType().castToIfNecessary(rangeType)
        "return ${returnType}($thisCasted, $otherCasted)".addAsMultiLineBody()
    }

    override fun MethodBuilder.modifyGeneratedRangeUntil(thisKind: PrimitiveType) {
        "this until $parameterName".addAsSingleLineBody(bodyOnNewLine = false)
    }

    override fun MethodBuilder.modifyGeneratedBitShiftOperators(thisKind: PrimitiveType) {
        if (thisKind == PrimitiveType.INT) {
            implementAsIntrinsic(thisKind, methodName)
        } else if (thisKind == PrimitiveType.LONG) {
            modifySignature { isInline = true }
            "wasm_i64_${methodName.toWasmOperator().lowercase()}(this, ${parameterName}.toLong())".addAsSingleLineBody(bodyOnNewLine = true)
        }
    }

    override fun MethodBuilder.modifyGeneratedBitwiseOperators(thisKind: PrimitiveType) {
        if (methodName == "inv") {
            modifySignature { isInline = true }
            val oneConst = if (thisKind == PrimitiveType.LONG) "-1L" else "-1"
            "this.xor($oneConst)".addAsSingleLineBody(bodyOnNewLine = true)
            return
        }

        implementAsIntrinsic(thisKind, methodName)
    }

    override fun MethodBuilder.modifyGeneratedConversions(thisKind: PrimitiveType) {
        val returnTypeAsPrimitive = PrimitiveType.valueOf(returnType.uppercase())
        if (returnTypeAsPrimitive == thisKind) {
            modifySignature { isInline = true }
            "this".addAsSingleLineBody(bodyOnNewLine = true)
            return
        }

        when (thisKind) {
            PrimitiveType.BYTE, PrimitiveType.SHORT -> when (returnTypeAsPrimitive) {
                // byte to byte conversion impossible here due to earlier check on type equality
                PrimitiveType.BYTE -> "this.toInt().toByte()".also { modifySignature { isInline = true } }
                PrimitiveType.CHAR -> "reinterpretAsInt().reinterpretAsChar()"
                PrimitiveType.SHORT -> "reinterpretAsInt().reinterpretAsShort()"
                PrimitiveType.INT -> "reinterpretAsInt()"
                PrimitiveType.LONG -> "wasm_i64_extend_i32_s(this.toInt())"
                PrimitiveType.FLOAT -> "wasm_f32_convert_i32_s(this.toInt())"
                PrimitiveType.DOUBLE -> "wasm_f64_convert_i32_s(this.toInt())"
                else -> throw IllegalArgumentException("Unsupported type $returnTypeAsPrimitive for generation conversion method from type $thisKind")
            }
            PrimitiveType.INT -> when (returnTypeAsPrimitive) {
                PrimitiveType.BYTE -> "((this shl 24) shr 24).reinterpretAsByte()"
                PrimitiveType.CHAR -> "(this and 0xFFFF).reinterpretAsChar()"
                PrimitiveType.SHORT -> "((this shl 16) shr 16).reinterpretAsShort()"
                PrimitiveType.LONG -> "wasm_i64_extend_i32_s(this)"
                PrimitiveType.FLOAT -> "wasm_f32_convert_i32_s(this)"
                PrimitiveType.DOUBLE -> "wasm_f64_convert_i32_s(this)"
                else -> throw IllegalArgumentException("Unsupported type $returnTypeAsPrimitive for generation conversion method from type $thisKind")
            }
            PrimitiveType.LONG -> when (returnTypeAsPrimitive) {
                PrimitiveType.BYTE, PrimitiveType.CHAR, PrimitiveType.SHORT -> "this.toInt().to${returnTypeAsPrimitive.capitalized}()"
                    .also { modifySignature { isInline = true } }
                PrimitiveType.INT -> "wasm_i32_wrap_i64(this)"
                PrimitiveType.FLOAT -> "wasm_f32_convert_i64_s(this)"
                PrimitiveType.DOUBLE -> "wasm_f64_convert_i64_s(this)"
                else -> throw IllegalArgumentException("Unsupported type $returnTypeAsPrimitive for generation conversion method from type $thisKind")
            }
            in PrimitiveType.floatingPoint -> when (returnTypeAsPrimitive) {
                PrimitiveType.BYTE, PrimitiveType.CHAR, PrimitiveType.SHORT -> "this.toInt().to${returnTypeAsPrimitive.capitalized}()"
                    .also { modifySignature { isInline = true } }
                PrimitiveType.INT -> "wasm_i32_trunc_sat_${thisKind.prefixLowercase}_s(this)"
                PrimitiveType.LONG -> "wasm_i64_trunc_sat_${thisKind.prefixLowercase}_s(this)"
                PrimitiveType.FLOAT -> "wasm_f32_demote_f64(this)"
                PrimitiveType.DOUBLE -> "wasm_f64_promote_f32(this)"
                else -> throw IllegalArgumentException("Unsupported type $returnTypeAsPrimitive for generation conversion method from type $thisKind")
            }
            else -> throw IllegalArgumentException("Unsupported type $thisKind to generate conversion methods")
        }.addAsSingleLineBody(bodyOnNewLine = false)
    }

    override fun MethodBuilder.modifyGeneratedEquals(thisKind: PrimitiveType) {
        val additionalCheck = when (thisKind) {
            PrimitiveType.LONG -> "wasm_i64_eq(this, $parameterName)"
            PrimitiveType.FLOAT -> "this.equals(other)"
            PrimitiveType.DOUBLE -> "this.toBits() == other.toBits()"
            else -> {
                "wasm_i32_eq(this${thisKind.castToIfNecessary(PrimitiveType.INT)}, $parameterName${thisKind.castToIfNecessary(PrimitiveType.INT)})"
            }
        }
        "$parameterName is ${thisKind.capitalized} && $additionalCheck".addAsSingleLineBody(bodyOnNewLine = true)
    }

    override fun MethodBuilder.modifyGeneratedToString(thisKind: PrimitiveType) {
        when (thisKind) {
            in PrimitiveType.floatingPoint -> "dtoa(this${thisKind.castToIfNecessary(PrimitiveType.DOUBLE)})"
            PrimitiveType.INT, PrimitiveType.LONG -> "itoa${thisKind.bitSize}(this, 10)"
            else -> "this.toInt().toString()"
        }.addAsSingleLineBody(bodyOnNewLine = true)
    }

    override fun ClassBuilder.generateAdditionalMethods(thisKind: PrimitiveType) {
        generateCustomEquals(thisKind)
        generateHashCode(thisKind)
        when {
            thisKind == PrimitiveType.BYTE || thisKind == PrimitiveType.SHORT -> generateReinterpret(PrimitiveType.INT)
            thisKind == PrimitiveType.INT -> {
                setOf(PrimitiveType.BOOLEAN, PrimitiveType.BYTE, PrimitiveType.SHORT, PrimitiveType.CHAR)
                    .forEach { generateReinterpret(it) }
            }
        }
    }

    private fun ClassBuilder.generateHashCode(thisKind: PrimitiveType) {
        method {
            signature {
                isOverride = true
                methodName = "hashCode"
                returnType = PrimitiveType.INT.capitalized
            }

            when (thisKind) {
                PrimitiveType.LONG -> "((this ushr 32) xor this).toInt()"
                PrimitiveType.FLOAT -> "toBits()"
                PrimitiveType.DOUBLE -> "toBits().hashCode()"
                else -> "this${thisKind.castToIfNecessary(PrimitiveType.INT)}"
            }.addAsSingleLineBody()
        }
    }

    private fun ClassBuilder.generateCustomEquals(thisKind: PrimitiveType) {
        method {
            annotations += "kotlin.internal.IntrinsicConstEvaluation"
            signature {
                isInline = thisKind in PrimitiveType.floatingPoint
                methodName = "equals"
                parameter {
                    name = "other"
                    type = thisKind.capitalized
                }
                returnType = PrimitiveType.BOOLEAN.capitalized
            }
            when (thisKind) {
                in PrimitiveType.floatingPoint -> "toBits() == other.toBits()".addAsSingleLineBody(bodyOnNewLine = false)
                else -> implementAsIntrinsic(thisKind, methodName)
            }
        }
    }

    private fun ClassBuilder.generateReinterpret(otherKind: PrimitiveType) {
        method {
            annotations += "WasmNoOpCast"
            annotations += "PublishedApi"
            signature {
                visibility = MethodVisibility.INTERNAL
                methodName = "reinterpretAs${otherKind.capitalized}"
                returnType = otherKind.capitalized
            }
            "implementedAsIntrinsic".addAsSingleLineBody(bodyOnNewLine = true)
        }
    }

    companion object {
        private fun String.toWasmOperator(): String {
            return when (this) {
                "plus" -> "ADD"
                "minus" -> "SUB"
                "times" -> "MUL"
                "rem" -> "REM_S"
                "unaryMinus" -> "NEG"
                "shr" -> "SHR_S"
                "ushr" -> "SHR_U"
                "equals" -> "EQ"
                else -> this.uppercase()
            }
        }

        private fun MethodBuilder.implementAsIntrinsic(thisKind: PrimitiveType, methodName: String) {
            modifySignature { isInline = false }
            annotations += "WasmOp(WasmOp.${thisKind.prefixUppercase}_${methodName.toWasmOperator()})"
            "implementedAsIntrinsic".addAsSingleLineBody(bodyOnNewLine = true)
        }

        private val PrimitiveType.prefixUppercase: String
            get() = when (this) {
                PrimitiveType.BYTE, PrimitiveType.SHORT, PrimitiveType.INT -> "I32"
                PrimitiveType.LONG -> "I64"
                PrimitiveType.FLOAT -> "F32"
                PrimitiveType.DOUBLE -> "F64"
                else -> ""
            }

        private val PrimitiveType.prefixLowercase: String
            get() = prefixUppercase.lowercase()
    }
}