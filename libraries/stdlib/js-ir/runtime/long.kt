/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE")

package kotlin

/**
 * Represents a 64-bit signed integer.
 */
public class Long internal constructor(
    internal val low: Int,
    internal val high: Int
) : Number(), Comparable<Long> {

    companion object {
        /**
         * A constant holding the minimum value an instance of Long can have.
         */
        public const val MIN_VALUE: Long = -9223372036854775807L - 1L

        /**
         * A constant holding the maximum value an instance of Long can have.
         */
        public const val MAX_VALUE: Long = 9223372036854775807L

        /**
         * The number of bytes used to represent an instance of Long in a binary form.
         */
        @SinceKotlin("1.3")
        public const val SIZE_BYTES: Int = 8

        /**
         * The number of bits used to represent an instance of Long in a binary form.
         */
        @SinceKotlin("1.3")
        public const val SIZE_BITS: Int = 64
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun compareTo(other: Byte): Int = compareTo(other.toLong())

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun compareTo(other: Short): Int = compareTo(other.toLong())

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun compareTo(other: Int): Int = compareTo(other.toLong())

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public override operator fun compareTo(other: Long): Int = compare(other)

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun compareTo(other: Float): Int = toFloat().compareTo(other)

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun compareTo(other: Double): Int = toDouble().compareTo(other)

    /** Adds the other value to this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun plus(other: Byte): Long = plus(other.toLong())

    /** Adds the other value to this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun plus(other: Short): Long = plus(other.toLong())

    /** Adds the other value to this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun plus(other: Int): Long = plus(other.toLong())

    /** Adds the other value to this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun plus(other: Long): Long = add(other)

    /** Adds the other value to this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun plus(other: Float): Float = toFloat() + other

    /** Adds the other value to this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun plus(other: Double): Double = toDouble() + other

    /** Subtracts the other value from this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun minus(other: Byte): Long = minus(other.toLong())

    /** Subtracts the other value from this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun minus(other: Short): Long = minus(other.toLong())

    /** Subtracts the other value from this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun minus(other: Int): Long = minus(other.toLong())

    /** Subtracts the other value from this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun minus(other: Long): Long = subtract(other)

    /** Subtracts the other value from this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun minus(other: Float): Float = toFloat() - other

    /** Subtracts the other value from this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun minus(other: Double): Double = toDouble() - other

    /** Multiplies this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun times(other: Byte): Long = times(other.toLong())

    /** Multiplies this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun times(other: Short): Long = times(other.toLong())

    /** Multiplies this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun times(other: Int): Long = times(other.toLong())

    /** Multiplies this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun times(other: Long): Long = multiply(other)

    /** Multiplies this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun times(other: Float): Float = toFloat() * other

    /** Multiplies this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun times(other: Double): Double = toDouble() * other

    /** Divides this value by the other value, truncating the result to an integer that is closer to zero. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun div(other: Byte): Long = div(other.toLong())

    /** Divides this value by the other value, truncating the result to an integer that is closer to zero. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun div(other: Short): Long = div(other.toLong())

    /** Divides this value by the other value, truncating the result to an integer that is closer to zero. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun div(other: Int): Long = div(other.toLong())

    /** Divides this value by the other value, truncating the result to an integer that is closer to zero. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun div(other: Long): Long = divide(other)

    /** Divides this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun div(other: Float): Float = toFloat() / other

    /** Divides this value by the other value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun div(other: Double): Double = toDouble() / other

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     *
     * The result is either zero or has the same sign as the _dividend_ and has the absolute value less than the absolute value of the divisor.
     */
    @SinceKotlin("1.1")
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun rem(other: Byte): Long = rem(other.toLong())

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     *
     * The result is either zero or has the same sign as the _dividend_ and has the absolute value less than the absolute value of the divisor.
     */
    @SinceKotlin("1.1")
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun rem(other: Short): Long = rem(other.toLong())

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     *
     * The result is either zero or has the same sign as the _dividend_ and has the absolute value less than the absolute value of the divisor.
     */
    @SinceKotlin("1.1")
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun rem(other: Int): Long = rem(other.toLong())

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     *
     * The result is either zero or has the same sign as the _dividend_ and has the absolute value less than the absolute value of the divisor.
     */
    @SinceKotlin("1.1")
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun rem(other: Long): Long = modulo(other)

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     *
     * The result is either zero or has the same sign as the _dividend_ and has the absolute value less than the absolute value of the divisor.
     */
    @SinceKotlin("1.1")
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun rem(other: Float): Float = toFloat() % other

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     *
     * The result is either zero or has the same sign as the _dividend_ and has the absolute value less than the absolute value of the divisor.
     */
    @SinceKotlin("1.1")
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun rem(other: Double): Double = toDouble() % other

    /**
     * Returns this value incremented by one.
     *
     * @sample samples.misc.Builtins.inc
     */
    public operator fun inc(): Long = this + 1L

    /**
     * Returns this value decremented by one.
     *
     * @sample samples.misc.Builtins.dec
     */
    public operator fun dec(): Long = this - 1L

    /** Returns this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public inline operator fun unaryPlus(): Long = this

    /** Returns the negative of this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun unaryMinus(): Long = inv() + 1L

    /** Creates a range from this value to the specified [other] value. */
    public operator fun rangeTo(other: Byte): LongRange = rangeTo(other.toLong())

    /** Creates a range from this value to the specified [other] value. */
    public operator fun rangeTo(other: Short): LongRange = rangeTo(other.toLong())

    /** Creates a range from this value to the specified [other] value. */
    public operator fun rangeTo(other: Int): LongRange = rangeTo(other.toLong())

    /** Creates a range from this value to the specified [other] value. */
    public operator fun rangeTo(other: Long): LongRange = LongRange(this, other)

    /**
     * Creates a range from this value up to but excluding the specified [other] value.
     *
     * If the [other] value is less than or equal to `this` value, then the returned range is empty.
     */
    @SinceKotlin("1.7")
    @ExperimentalStdlibApi
    public operator fun rangeUntil(other: Byte): LongRange = this until other

    /**
     * Creates a range from this value up to but excluding the specified [other] value.
     *
     * If the [other] value is less than or equal to `this` value, then the returned range is empty.
     */
    @SinceKotlin("1.7")
    @ExperimentalStdlibApi
    public operator fun rangeUntil(other: Short): LongRange = this until other

    /**
     * Creates a range from this value up to but excluding the specified [other] value.
     *
     * If the [other] value is less than or equal to `this` value, then the returned range is empty.
     */
    @SinceKotlin("1.7")
    @ExperimentalStdlibApi
    public operator fun rangeUntil(other: Int): LongRange = this until other

    /**
     * Creates a range from this value up to but excluding the specified [other] value.
     *
     * If the [other] value is less than or equal to `this` value, then the returned range is empty.
     */
    @SinceKotlin("1.7")
    @ExperimentalStdlibApi
    public operator fun rangeUntil(other: Long): LongRange = this until other

    /**
     * Shifts this value left by the [bitCount] number of bits.
     *
     * Note that only the six lowest-order bits of the [bitCount] are used as the shift distance.
     * The shift distance actually used is therefore always in the range `0..63`.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public infix fun shl(bitCount: Int): Long = shiftLeft(bitCount)

    /**
     * Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with copies of the sign bit.
     *
     * Note that only the six lowest-order bits of the [bitCount] are used as the shift distance.
     * The shift distance actually used is therefore always in the range `0..63`.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public infix fun shr(bitCount: Int): Long = shiftRight(bitCount)

    /**
     * Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with zeros.
     *
     * Note that only the six lowest-order bits of the [bitCount] are used as the shift distance.
     * The shift distance actually used is therefore always in the range `0..63`.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public infix fun ushr(bitCount: Int): Long = shiftRightUnsigned(bitCount)

    /** Performs a bitwise AND operation between the two values. */
    @kotlin.internal.IntrinsicConstEvaluation
    public infix fun and(other: Long): Long = Long(low and other.low, high and other.high)

    /** Performs a bitwise OR operation between the two values. */
    @kotlin.internal.IntrinsicConstEvaluation
    public infix fun or(other: Long): Long = Long(low or other.low, high or other.high)

    /** Performs a bitwise XOR operation between the two values. */
    @kotlin.internal.IntrinsicConstEvaluation
    public infix fun xor(other: Long): Long = Long(low xor other.low, high xor other.high)

    /** Inverts the bits in this value. */
    @kotlin.internal.IntrinsicConstEvaluation
    public fun inv(): Long = Long(low.inv(), high.inv())

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toByte(): Byte = low.toByte()

    @Deprecated("Direct conversion to Char is deprecated. Use toInt().toChar() or Char constructor instead.", ReplaceWith("this.toInt().toChar()"))
    @DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "2.3")
    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toChar(): Char = low.toChar()

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toShort(): Short = low.toShort()

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toInt(): Int = low

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toLong(): Long = this

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toFloat(): Float = toDouble().toFloat()

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toDouble(): Double = toNumber()

    // This method is used by JavaScript to convert objects of type Long to primitives.
    // This is essential for the JavaScript interop.
    // JavaScript functions that expect `number` are imported in Kotlin as expecting `kotlin.Number`
    // (in our standard library, and also in user projects if they use Dukat for generating external declarations).
    // Because `kotlin.Number` is a supertype of `Long` too, there has to be a way for JS to know how to handle Longs.
    // See KT-50202
    @JsName("valueOf")
    internal fun valueOf() = toDouble()

    @kotlin.internal.IntrinsicConstEvaluation
    override fun equals(other: Any?): Boolean = other is Long && equalsLong(other)

    override fun hashCode(): Int = hashCode(this)

    @kotlin.internal.IntrinsicConstEvaluation
    override fun toString(): String = this.toStringImpl(radix = 10)
}
