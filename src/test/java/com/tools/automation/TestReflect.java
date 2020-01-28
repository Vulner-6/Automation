package com.tools.automation;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class TestReflect
{
    OkHttpClient okHttpClient;
    /**
     * 读取配置文件，加载类、方法，并在不改变源码，只改变配置文件的前提下，让代码运行
     */
    @Test
    public void reflect()
    {
        //1.使用类加载器，将配置文件，转换成字节流加载进内存
        //获取当前class目录下的配置文件，即reflect.properties
        ClassLoader classLoader=TestReflect.class.getClassLoader();
        InputStream is=classLoader.getResourceAsStream("reflect.properties");
        //2.创建properties对象，载入配置文件
        Properties properties=new Properties();
        try
        {
            if(is!=null)
            {
                properties.load(is);
                //3.获取配置文件中定义的数据
                String className=properties.getProperty("className");
                String methodName=properties.getProperty("methodName");
                //4.利用反射加载该类
                Class cls=Class.forName(className);
                Object obj=cls.newInstance();
                Method method=cls.getMethod(methodName);  //获取方法对象
                //5.有方法对象，有类对象，那么就可以执行方法了
                Class<OkHttpClient> c=OkHttpClient.class;
                okHttpClient=c.cast(method.invoke(obj));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
