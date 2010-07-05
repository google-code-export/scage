package su.msk.dunno.scage.handlers.tracer;

import su.msk.dunno.scage.support.Vec;

public class StateData 
{
	private int int_num;
	public StateData(int i)
	{
		int_num = i;
	}
	public int getInt()
	{
		return int_num;
	}
	public StateData setInt(int int_num)
	{
		this.int_num = int_num;
		return this;
	}

	private float float_num;
	public StateData(float f)
	{
		float_num = f;
	}
	public float getFloat()
	{
		return float_num;
	}
	public StateData setFloat(float f)
	{
		float_num = f;
		return this;
	}

	private String message;
	public StateData(String s)
	{
		message = s;
	}
	public String getString()
	{
		return message;
	}
	public StateData setString(String s)
	{
		message = s;
		return this;
	}

    private Vec vec;
    public StateData(Vec v)
    {
        vec = v;
    }
    public Vec getVec()
    {
        return vec;
    }
    public StateData setVec(Vec v)
    {
        vec = v;
        return this;
    }
}
