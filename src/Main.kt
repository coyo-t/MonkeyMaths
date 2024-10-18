import org.joml.Matrix4d
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_DOUBLE

inline fun MemorySegment.gm (i:Int)
	= getAtIndex(JAVA_DOUBLE, i.toLong())

inline fun MemorySegment.sm (i:Int, v:Double)
	= setAtIndex(JAVA_DOUBLE, i.toLong(), v)

inline fun MemorySegment.ga (i:Long)
	= get(JAVA_DOUBLE, i)

inline fun MemorySegment.sa (i:Long, v:Double)
	= set(JAVA_DOUBLE, i, v)


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

private const val MA00 = M00.toLong() * Double.SIZE_BYTES
private const val MA10 = M10.toLong() * Double.SIZE_BYTES
private const val MA20 = M20.toLong() * Double.SIZE_BYTES
private const val MA30 = M30.toLong() * Double.SIZE_BYTES
private const val MA01 = M01.toLong() * Double.SIZE_BYTES
private const val MA11 = M11.toLong() * Double.SIZE_BYTES
private const val MA21 = M21.toLong() * Double.SIZE_BYTES
private const val MA31 = M31.toLong() * Double.SIZE_BYTES
private const val MA02 = M02.toLong() * Double.SIZE_BYTES
private const val MA12 = M12.toLong() * Double.SIZE_BYTES
private const val MA22 = M22.toLong() * Double.SIZE_BYTES
private const val MA32 = M32.toLong() * Double.SIZE_BYTES
private const val MA03 = M03.toLong() * Double.SIZE_BYTES
private const val MA13 = M13.toLong() * Double.SIZE_BYTES
private const val MA23 = M23.toLong() * Double.SIZE_BYTES
private const val MA33 = M33.toLong() * Double.SIZE_BYTES

private fun mMul (l:Mat, r:Mat, d:Mat)
{
	val mr = r.address
	val rm00 = mr.ga(MA00);val rm10 = mr.ga(MA10);val rm20 = mr.ga(MA20);val rm30 = mr.ga(MA30)
	val rm01 = mr.ga(MA01);val rm11 = mr.ga(MA11);val rm21 = mr.ga(MA21);val rm31 = mr.ga(MA31)
	val rm02 = mr.ga(MA02);val rm12 = mr.ga(MA12);val rm22 = mr.ga(MA22);val rm32 = mr.ga(MA32)
	val rm03 = mr.ga(MA03);val rm13 = mr.ga(MA13);val rm23 = mr.ga(MA23);val rm33 = mr.ga(MA33)

	val ml = l.address
	val lm00 = ml.ga(MA00);val lm10 = ml.ga(MA10);val lm20 = ml.ga(MA20);val lm30 = ml.ga(MA30)
	val lm01 = ml.ga(MA01);val lm11 = ml.ga(MA11);val lm21 = ml.ga(MA21);val lm31 = ml.ga(MA31)
	val lm02 = ml.ga(MA02);val lm12 = ml.ga(MA12);val lm22 = ml.ga(MA22);val lm32 = ml.ga(MA32)
	val lm03 = ml.ga(MA03);val lm13 = ml.ga(MA13);val lm23 = ml.ga(MA23);val lm33 = ml.ga(MA33)

	val md = d.address
	md.sa(MA00,lm00*rm00 + lm10*rm01 + lm20*rm02 + lm30*rm03)
	md.sa(MA10,lm01*rm00 + lm11*rm01 + lm21*rm02 + lm31*rm03)
	md.sa(MA20,lm02*rm00 + lm12*rm01 + lm22*rm02 + lm32*rm03)
	md.sa(MA30,lm03*rm00 + lm13*rm01 + lm23*rm02 + lm33*rm03)
	md.sa(MA01,lm00*rm10 + lm10*rm11 + lm20*rm12 + lm30*rm13)
	md.sa(MA11,lm01*rm10 + lm11*rm11 + lm21*rm12 + lm31*rm13)
	md.sa(MA21,lm02*rm10 + lm12*rm11 + lm22*rm12 + lm32*rm13)
	md.sa(MA31,lm03*rm10 + lm13*rm11 + lm23*rm12 + lm33*rm13)
	md.sa(MA02,lm00*rm20 + lm10*rm21 + lm20*rm22 + lm30*rm23)
	md.sa(MA12,lm01*rm20 + lm11*rm21 + lm21*rm22 + lm31*rm23)
	md.sa(MA22,lm02*rm20 + lm12*rm21 + lm22*rm22 + lm32*rm23)
	md.sa(MA32,lm03*rm20 + lm13*rm21 + lm23*rm22 + lm33*rm23)
	md.sa(MA03,lm00*rm30 + lm10*rm31 + lm20*rm32 + lm30*rm33)
	md.sa(MA13,lm01*rm30 + lm11*rm31 + lm21*rm32 + lm31*rm33)
	md.sa(MA23,lm02*rm30 + lm12*rm31 + lm22*rm32 + lm32*rm33)
	md.sa(MA33,lm03*rm30 + lm13*rm31 + lm23*rm32 + lm33*rm33)
}

fun mMul (lhs:Mat, rhs:Mat): Mat
{
	return Mat.createUninit().also { mMul(lhs, rhs, it) }
}

@JvmInline
value class Mat
internal constructor (val address: MemorySegment)
{
	inline operator fun get (i:Int) = address.gm(i)
	inline operator fun get (x:Int, y:Int) = get((y shl 2) +x)

	inline operator fun set (i:Int, v:Double) = address.sm(i, v)
	inline operator fun set (x:Int, y:Int, v:Double) = set((y shl 2)+x, v)

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

		fun createIdent () = createIdent(Arena.ofAuto())

		fun createIdent (arena:Arena): Mat
		{
			return Mat(createMem(arena).apply {
				fill(0)
				sm(M00, 1.0)
				sm(M11, 1.0)
				sm(M22, 1.0)
				sm(M33, 1.0)
			})
		}

		fun fromArray (a:DoubleArray):Mat
		{
			return Mat(Arena.ofAuto().allocateFrom(JAVA_DOUBLE, *a.copyOf(16)))
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

fun matrixIdentity () = Mat.createIdent()
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

fun matrixOf (data:DoubleArray) = Mat.fromArray(data)

fun matStr (m:Mat):String
{
	return """
		[ ${m[M00]}, ${m[M10]}, ${m[M20]}, ${m[M30]} ]
		[ ${m[M01]}, ${m[M11]}, ${m[M21]}, ${m[M31]} ]
		[ ${m[M02]}, ${m[M12]}, ${m[M22]}, ${m[M32]} ]
		[ ${m[M03]}, ${m[M13]}, ${m[M23]}, ${m[M33]} ]
	""".trimIndent()
}

inline fun timez (bloc:()->Unit):Long
{
	val begin = System.nanoTime()
	bloc()
	return System.nanoTime()-begin
}

fun cmp (cMat:Mat, jMat:Matrix4d)
{
	check(cMat[M00] == jMat.m00())
	check(cMat[M10] == jMat.m10())
	check(cMat[M20] == jMat.m20())
	check(cMat[M30] == jMat.m30())

	check(cMat[M01] == jMat.m01())
	check(cMat[M11] == jMat.m11())
	check(cMat[M21] == jMat.m21())
	check(cMat[M31] == jMat.m31())

	check(cMat[M02] == jMat.m02())
	check(cMat[M12] == jMat.m12())
	check(cMat[M22] == jMat.m22())
	check(cMat[M32] == jMat.m32())

	check(cMat[M03] == jMat.m03())
	check(cMat[M13] == jMat.m13())
	check(cMat[M23] == jMat.m23())
	check(cMat[M33] == jMat.m33())
}

fun main ()
{
	val iterates = 0..(10000000)
//	val iterates = 0..292202*2

	val data_a = doubleArrayOf(
		1.0, 0.0, 0.0, 0.0,
		0.0, 1.0, 0.0, 0.0,
		0.0, 0.0, 1.0, 0.0,
		0.0, 0.0, 0.0, 1.0,
	)

	val data_b = doubleArrayOf(
		2.0, 0.0, 0.0, 0.0,
		2.9, 2.2, 0.2, 0.0,
		0.0, 0.0, 4.0, 0.0,
		0.0, 0.0, 0.0, 5.0,
	)

	val m = matrixOf(data_a)
	val m2 = matrixOf(data_b)
	val cDst = matrixIdentity()

	val coyoteTime = timez {
		for (i in iterates)
		{
			m.mul(m2, cDst)
		}
	}
	println(coyoteTime / 1000000000.0)
	println(matStr(cDst))

	val jm1 = Matrix4d().set(data_a)
	val jm2 = Matrix4d().set(data_b)
	val jDst = Matrix4d()

	val jomlTime = timez {
		for (i in iterates)
		{
			jm1.mul0(jm2, jDst)
		}
	}
	println(jomlTime / 1000000000.0)

	println(jDst.toString(fmt))

	cmp(cDst, jDst)

}
