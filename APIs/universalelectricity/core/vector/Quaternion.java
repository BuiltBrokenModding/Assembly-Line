package universalelectricity.core.vector;

import universalelectricity.core.vector.Vector3;

/** Simple quaternion class designed to be used for rotation of objects.
 *
 * @author DarkGuardsman */
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

    public void set(Quaternion quaternion)
    {
        w = quaternion.w;
        x = quaternion.x;
        y = quaternion.y;
        z = quaternion.z;
    }

    public void set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /** Normalizes the Quaternion only if its outside the min errors range */
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

    /** Gets the inverse of this Quaternion */
    public Quaternion getConj()
    {
        return new Quaternion(-x, -y, -z, w);
    }

    public void conj()
    {
        x = -x;
        y = -y;
        z = -z;
    }

    /** Multiplying q1 with q2 applies the rotation q2 to q1 */
    public Quaternion multi(Quaternion rq)
    {
        return new Quaternion(w * rq.x + x * rq.w + y * rq.z - z * rq.y, w * rq.y + y * rq.w + z * rq.x - x * rq.z, w * rq.z + z * rq.w + x * rq.y - y * rq.x, w * rq.w - x * rq.x - y * rq.y - z * rq.z);
    }

    public void multLocal(Quaternion q)
    {
        Quaternion temp = this.multi(q);
        this.set(temp);
    }

    /** Multi a vector against this in other words applying rotation */
    public Vector3 multi(Vector3 vec)
    {
        Vector3 vn = vec.clone();

        Quaternion vecQuat = new Quaternion(0, 0, 0, 1), resQuat;
        vecQuat.x = (float) vn.x;
        vecQuat.y = (float) vn.y;
        vecQuat.z = (float) vn.z;
        vecQuat.w = 0.0f;

        resQuat = vecQuat.multi(this.getConj());
        resQuat = this.multi(resQuat);

        return new Vector3(resQuat.x, resQuat.y, resQuat.z);
    }

    public void FromAxis(Vector3 v, float angle)
    {
        angle *= 0.5f;
        Vector3 vn = v.clone();
        vn.normalize();

        float sinAngle = (float) Math.sin(angle);

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

    /* Convert to Matrix
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
    }*/

    // Convert to Axis/Angles
    public void getAxisAngle(Vector3 axis, float angle)
    {
        float scale = (float) Math.sqrt(x * x + y * y + z * z);
        x = x / scale;
        y = y / scale;
        z = z / scale;
        angle = (float) (Math.acos(w) * 2.0f);
    }

    @Override
    public String toString()
    {
        return "<" + x + "x " + y + "y " + z + "z @" + w + ">";
    }
}
