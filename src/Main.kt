import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_DOUBLE

private inline fun MemorySegment.gm (i:Int)
	= getAtIndex(JAVA_DOUBLE, i.toLong())

private inline fun MemorySegment.sm (i:Int, v:Double)
	= setAtIndex(JAVA_DOUBLE, i.toLong(), v)


private const val M00 = 0
private const val M10 = 1
private const val M20 = 2
private const val M30 = 3
private const val M01 = 4
private const val M11 = 5
private const val M21 = 6
private const val M31 = 7
private const val M02 = 8
private const val M12 = 9
private const val M22 = 10
private const val M32 = 11
private const val M03 = 12
private const val M13 = 13
private const val M23 = 14
private const val M33 = 15

private fun mMul (l:Mat, r:Mat, d:Mat)
{
	d[M00]=l[M00]*r[M00]+l[M10]*r[M01]+l[M20]*r[M02]+l[M30]*r[M03]
	d[M10]=l[M01]*r[M00]+l[M11]*r[M01]+l[M21]*r[M02]+l[M31]*r[M03]
	d[M20]=l[M02]*r[M00]+l[M12]*r[M01]+l[M22]*r[M02]+l[M32]*r[M03]
	d[M30]=l[M03]*r[M00]+l[M13]*r[M01]+l[M23]*r[M02]+l[M33]*r[M03]

	d[M01]=l[M00]*r[M10]+l[M10]*r[M11]+l[M20]*r[M12]+l[M30]*r[M13]
	d[M11]=l[M01]*r[M10]+l[M11]*r[M11]+l[M21]*r[M12]+l[M31]*r[M13]
	d[M21]=l[M02]*r[M10]+l[M12]*r[M11]+l[M22]*r[M12]+l[M32]*r[M13]
	d[M31]=l[M03]*r[M10]+l[M13]*r[M11]+l[M23]*r[M12]+l[M33]*r[M13]

	d[M02]=l[M00]*r[M20]+l[M10]*r[M21]+l[M20]*r[M22]+l[M30]*r[M23]
	d[M12]=l[M01]*r[M20]+l[M11]*r[M21]+l[M21]*r[M22]+l[M31]*r[M23]
	d[M22]=l[M02]*r[M20]+l[M12]*r[M21]+l[M22]*r[M22]+l[M32]*r[M23]
	d[M32]=l[M03]*r[M20]+l[M13]*r[M21]+l[M23]*r[M22]+l[M33]*r[M23]

	d[M03]=l[M00]*r[M30]+l[M10]*r[M31]+l[M20]*r[M32]+l[M30]*r[M33]
	d[M13]=l[M01]*r[M30]+l[M11]*r[M31]+l[M21]*r[M32]+l[M31]*r[M33]
	d[M23]=l[M02]*r[M30]+l[M12]*r[M31]+l[M22]*r[M32]+l[M32]*r[M33]
	d[M33]=l[M03]*r[M30]+l[M13]*r[M31]+l[M23]*r[M32]+l[M33]*r[M33]
}

fun mMul (lhs:Mat, rhs:Mat): Mat
{
	return Mat.createUninit().also { mMul(lhs, rhs, it) }
}

@JvmInline
value class Mat
internal constructor (val address: MemorySegment)
{
	operator fun get (i:Int) = address.gm(i)
	operator fun get (x:Int, y:Int) = get((y shl 2) +x)

	operator fun set (i:Int, v:Double) = address.sm(i, v)
	operator fun set (x:Int, y:Int, v:Double) = set((y shl 2)+x, v)

	fun mul (rite:Mat, into:Mat)
	{
		mMul(this, rite, into)
	}

	operator fun times (rhs:Mat) = mMul(this, rhs)

	companion object
	{
		private inline fun createMem (arena:Arena)
			= arena.allocate(JAVA_DOUBLE, 4*4)

		internal fun createUninit ()
			= createUninit(Arena.ofAuto())

		internal fun createUninit (arena:Arena)
			= Mat(createMem(arena))

		operator fun invoke () = this(Arena.ofAuto())

		operator fun invoke (arena:Arena): Mat
		{
			return Mat(createMem(arena).apply {
				fill(0)
				sm(M00, 1.0)
				sm(M11, 1.0)
				sm(M22, 1.0)
				sm(M33, 1.0)
			})
		}

		operator fun invoke (
			arena:Arena,
			i0:Double, i1:Double, i2:Double, i3:Double,
			i4:Double, i5:Double, i6:Double, i7:Double,
			i8:Double, i9:Double, iA:Double, iB:Double,
			iC:Double, iD:Double, iE:Double, iF:Double,
		): Mat
		{
			return Mat(arena.allocate(JAVA_DOUBLE, 4*4).apply {
				sm(M00, i0);sm(M10, i1);sm(M20, i2);sm(M30, i3)
				sm(M01, i4);sm(M11, i5);sm(M21, i6);sm(M31, i7)
				sm(M02, i8);sm(M12, i9);sm(M22, iA);sm(M32, iB)
				sm(M03, iC);sm(M13, iD);sm(M23, iE);sm(M33, iF)
			})
		}

		operator fun invoke (
			m00:Double, m10:Double, m20:Double, m30:Double,
			m01:Double, m11:Double, m21:Double, m31:Double,
			m02:Double, m12:Double, m22:Double, m32:Double,
			m03:Double, m13:Double, m23:Double, m33:Double,
		): Mat
		{
			return this(
				Arena.ofAuto(),
				m00,m10,m20,m30,
				m01,m11,m21,m31,
				m02,m12,m22,m32,
				m03,m13,m23,m33,
			)
		}
	}
}

fun matrixIdentity () = Mat()
fun matrixOf (
	m00:Double, m10:Double, m20:Double, m30:Double,
	m01:Double, m11:Double, m21:Double, m31:Double,
	m02:Double, m12:Double, m22:Double, m32:Double,
	m03:Double, m13:Double, m23:Double, m33:Double,
) = Mat(
	m00,m10,m20,m30,
	m01,m11,m21,m31,
	m02,m12,m22,m32,
	m03,m13,m23,m33,
)

fun matStr (m:Mat):String
{
	return """
		[ ${m[0]}, ${m[1]}, ${m[2]}, ${m[3]} ]
		[ ${m[4]}, ${m[5]}, ${m[6]}, ${m[7]} ]
		[ ${m[8]}, ${m[9]}, ${m[10]}, ${m[11]} ]
		[ ${m[12]}, ${m[13]}, ${m[14]}, ${m[15]} ]
	""".trimIndent()
}

fun main ()
{
	val m = matrixIdentity()

	val m2 = matrixOf(
		2.0, 0.0, 0.0, 0.0,
		0.0, 3.0, 0.0, 0.0,
		0.0, 0.0, 4.0, 0.0,
		0.0, 0.0, 0.0, 5.0,
	)

	println(matStr(m * m2))

}
