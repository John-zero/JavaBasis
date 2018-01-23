package java.lang;

/**
 * Created by John_zero on 2018/1/23.
 */
public final class String implements java.io.Serializable, Comparable<String>, CharSequence
{

    public String ()
    {
        System.out.println("java.lang.String");
    }

    public String (String string)
    {
        System.out.println("java.lang.String parameter: " + string);
    }

    @Override
    public int length()
    {
        return 0;
    }

    @Override
    public char charAt(int index)
    {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        return null;
    }

    @Override
    public int compareTo(String o)
    {
        return 0;
    }

}
