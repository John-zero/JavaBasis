package com.java.io;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by John_zero on 2018/1/23.
 *
 * 性能分析对比
 *  https://github.com/eishay/jvm-serializers/wiki
 *
 * 序列化实现:
 *  1. Java 原生序列化
 *      1.1 implements java.io.Serializable
 *      1.2 implements java.io.Externalizable
 *  2. JSON 序列化 (跨语言)
 *      2.1 Alibaba fastjson https://github.com/alibaba/fastjson
 *      2.2 Google gson http://www.gson.org/
 *      2.3 jackson https://www.jax.org/
 *      2.4 ...
 *  3. Google Protocol Buffer (跨语言)
 *      https://github.com/google/protobuf
 *  4. Protostuff
 *      https://github.com/protostuffs
 *  5. Apache Thrift (跨语言)
 *      http://thrift.apache.org/
 *  6. Hessian (跨语言)
 *      Hessian http://hessian.caucho.com/
 *  7. Apache Avro (跨语言)
 *      http://avro.apache.org/
 *  8. Google Kryo (Java)
 *      https://code.google.com/p/kryo/
 *  9. ...
 *      ...
 */
public class _Serializable
{

    public static void main (String [] args)
    {

    }

}


class Serializable_Student implements java.io.Serializable
{

}

/*
public interface Externalizable extends java.io.Serializable
{

    void writeExternal(ObjectOutput out) throws IOException;

    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;

}
*/

class Externalizable_Student implements java.io.Externalizable
{

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {

    }

}