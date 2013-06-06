package dark.library.math;

import universalelectricity.core.vector.Vector3;

/**
 * This code is converted from C code to java based off of this tutorial
 * http://content.gpwiki.org/index.php/OpenGL:Tutorials:Using_Quaternions_to_represent_rotation
 * 
 * @author DarkGuardsman
 * 
 */
public class Quaternion
{
	public static final float TOLERANCE = 0.00001f;
	float x, y, z, w;

	public Quaternion()
	{
		this(0, 0, 0, 1);
	}

	public Quaternion(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternion(Vector3 vec, float w)
	{
		this((float) vec.x, (float) vec.y, (float) vec.z, w);
	}

	// normalising a quaternion works similar to a vector. This method will not do anything
	// if the quaternion is close enough to being unit-length. define TOLERANCE as something
	// small like 0.00001f to get accurate results
	public void normalise()
	{
		// Don't normalize if we don't have to
		double mag2 = w * w + x * x + y * y + z * z;
		if (Math.abs(mag2) > TOLERANCE && Math.abs(mag2 - 1.0f) > TOLERANCE)
		{
			float mag = (float) Math.sqrt(mag2);
			w /= mag;
			x /= mag;
			y /= mag;
			z /= mag;
		}
	}

	// We need to get the inverse of a quaternion to properly apply a quaternion-rotation to a
	// vector
	// The conjugate of a quaternion is the same as the inverse, as long as the quaternion is
	// unit-length
	public Quaternion getConjugate()
	{
		return new Quaternion(-x, -y, -z, w);
	}

	// Multiplying q1 with q2 applies the rotation q2 to q1
	public Quaternion multi(Quaternion rq)
	{
		return new Quaternion(w * rq.x + x * rq.w + y * rq.z - z * rq.y, w * rq.y + y * rq.w + z * rq.x - x * rq.z, w * rq.z + z * rq.w + x * rq.y - y * rq.x, w * rq.w - x * rq.x - y * rq.y - z * rq.z);
	}

	// Multiplying a quaternion q with a vector v applies the q-rotation to v
	public Vector3 multi(Vector3 vec)
	{
		Vector3 vn = vec.clone();
		vn.normalize();

		Quaternion vecQuat = new Quaternion(0, 0, 0, 1), resQuat;
		vecQuat.x = (float) vn.x;
		vecQuat.y = (float) vn.y;
		vecQuat.z = (float) vn.z;
		vecQuat.w = 0.0f;

		resQuat = vecQuat.multi(this.getConjugate());
		resQuat = this.multi(resQuat);

		return new Vector3(resQuat.x, resQuat.y, resQuat.z);
	}

	public void FromAxis(Vector3 v, float angle)
	{
		float sinAngle;
		angle *= 0.5f;
		Vector3 vn = v.clone();
		vn.normalize();

		sinAngle = (float) Math.sin(angle);

		x = (float) (vn.x * sinAngle);
		y = (float) (vn.y * sinAngle);
		z = (float) (vn.z * sinAngle);
		w = (float) Math.cos(angle);
	}

	// Convert from Euler Angles
	public void FromEuler(float pitch, float yaw, float roll)
	{
		// Basically we create 3 Quaternions, one for pitch, one for yaw, one for roll
		// and multiply those together.
		// the calculation below does the same, just shorter

		float p = (float) (pitch * (Math.PI / 180) / 2.0);
		float y = (float) (yaw * (Math.PI / 180) / 2.0);
		float r = (float) (roll * (Math.PI / 180) / 2.0);

		float sinp = (float) Math.sin(p);
		float siny = (float) Math.sin(y);
		float sinr = (float) Math.sin(r);
		float cosp = (float) Math.cos(p);
		float cosy = (float) Math.cos(y);
		float cosr = (float) Math.cos(r);

		x = sinr * cosp * cosy - cosr * sinp * siny;
		y = cosr * sinp * cosy + sinr * cosp * siny;
		z = cosr * cosp * siny - sinr * sinp * cosy;
		w = cosr * cosp * cosy + sinr * sinp * siny;

		normalise();
	}

	// Convert to Matrix
	public Matrix4 getMatrix()
	{
		float x2 = (float) (x * x);
		float y2 = (float) (y * y);
		float z2 = (float) (z * z);
		float xy = (float) (x * y);
		float xz = (float) (x * z);
		float yz = (float) (y * z);
		float wx = (float) (w * x);
		float wy = (float) (w * y);
		float wz = (float) (w * z);

		// This calculation would be a lot more complicated for non-unit length quaternions
		// Note: The constructor of Matrix4 expects the Matrix in column-major format like expected
		// by
		// OpenGL
		return new Matrix4(1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy), 0.0f, 2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx), 0.0f, 2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
	}

	// Convert to Axis/Angles
	public void getAxisAngle(Vector3 axis, float angle)
	{
		float scale = (float) Math.sqrt(x * x + y * y + z * z);
		x = x / scale;
		y = y / scale;
		z = z / scale;
		angle = (float) (Math.acos(w) * 2.0f);
	}
}
