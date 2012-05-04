package org.bullet.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;


import org.objectweb.asm.*;

public class DynamicBencher implements Opcodes {
    
    public interface IExecutable {
        public long execute(long a,long b);
    }
    
    public static class DynamicLoader extends URLClassLoader {
        public DynamicLoader(URL[] urls)
        {
            super(urls);
        }        
        
        public Class<?> loadFromBytes(byte[] classDefinition)
        {
            Class<?> clazz=defineClass(null,classDefinition, 0,classDefinition.length);
            resolveClass(clazz);
            return clazz;
        }
        
    }

    public static class BootStrap
    {
        public static void main(DynamicLoader dl)
        {
            try 
            {
                (new BootStrap()).execute(dl);
            } catch (Exception e) 
            {
                e.printStackTrace();
            }
                   
        }

        public void execute(DynamicLoader dl)
        {
            byte[] classFile;
            try
            {
                classFile = dump();
                Class<?> onTheFly = dl.loadFromBytes(classFile);                
                Constructor<?> cons=onTheFly.getConstructor(new Class<?>[0]);
                IExecutable toExecute = (IExecutable) cons.newInstance(new Object[0]);
                System.out.println("Perform with invokedynamic");
                for(int i=0;i<10;++i)
                {
                    long t = System.currentTimeMillis();
                    long[] ar=new long[10240];
                    for(int x=0;x<ar.length;++x)
                    {
                        for(int y=0;y<ar.length;++y)
                        {
                            ar[x]=toExecute.execute(ar[y], 1l);
                        }
                    }
                    System.out.println(".... took: " + (System.currentTimeMillis()-t) + " milli seconds");
                }
                System.out.println("Perform with invokestatic");
                for(int i=0;i<10;++i)
                {
                    long t = System.currentTimeMillis();
                    long[] ar=new long[10240];
                    for(int x=0;x<ar.length;++x)
                    {
                        for(int y=0;y<ar.length;++y)
                        {
                            ar[x]=wrapCalc(ar[y], 1l);
                        }
                    }
                    System.out.println(".... took: " + (System.currentTimeMillis()-t) + " milli seconds");
                }
                System.out.println("Perform with reflection");
                for(int i=0;i<10;++i)
                {
                    long t = System.currentTimeMillis();
                    long[] ar=new long[10240];
                    for(int x=0;x<ar.length;++x)
                    {
                        for(int y=0;y<ar.length;++y)
                        {
                            ar[x]=wrapCalcReflect(ar[y], 1l);
                        }
                    }
                    System.out.println(".... took: " + (System.currentTimeMillis()-t) + " milli seconds");
                }
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            } 
        }

        /** The method which is invoked statically and dynamically */
        public long wrapCalc(long a,long b) {
            return performCalcLong(a, b);
        }
        
        static Object ab[]=new Object[2];
        static java.lang.reflect.Method m = null;
        static
        {
            try
            {
                m=BootStrap.class.getDeclaredMethod("performCalcLong",new Class<?>[]{long.class,long.class});
            }
            catch (NoSuchMethodException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SecurityException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        /** The method which is invoked statically and dynamically */
        public long wrapCalcReflect(long a,long b)
        {
            ab[0]=a;
            ab[1]=b;
            try
            {
                return ((Long)m.invoke(a,ab)).longValue();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        }
        

    }
    
    // Boot strap myself
    public static void main(String args[])
    {
        try
        {
            DynamicLoader dl = new DynamicLoader(new URL[0]);
            Class<?> clazz = dl.loadClass("org.bullet.asm.DynamicBencher$BootStrap");
            java.lang.reflect.Method m=clazz.getDeclaredMethod("main", new Class<?>[]{DynamicLoader.class});
            m.invoke(null,new Object[]{dl});
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        
    }

    public static byte[] dump () throws Exception 
    {

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit
        (
            V1_7, ACC_PUBLIC + ACC_SUPER, 
            "org/bullet/asm/dynamics/Simple", 
            null, 
            "java/lang/Object", 
            new String[]{"org/bullet/asm/DynamicBencher$IExecutable"}
        );

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "execute", "(JJ)J", null, null);
            mv.visitCode();
            mv.visitVarInsn(LLOAD, 1);
            mv.visitVarInsn(LLOAD, 3);
            //mv.visitMethodInsn(INVOKESTATIC, "Simple", "add", "(JJ)J");
            makeInvokeDynamic(mv);
            mv.visitInsn(LRETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
    
    static MutableCallSite _callSite = null;
    static MethodHandles.Lookup _lookup = null;
    public static CallSite bootstrap(MethodHandles.Lookup lookup, 
    		String name, java.lang.invoke.MethodType type)
    {
        System.out.println(">>>> Entering Boot Strap");
        _lookup = lookup;
        // that to which we bootstrap depends on the first parameter
        // passed to the application!
        java.lang.reflect.Method m=null;
        try
        {
            m=BootStrap.class.getMethod("performCalcLong", new Class[]{long.class,long.class});
            System.out.println("<<<<< Leaving Boot Strap");
            return _callSite=(new MutableCallSite(lookup.unreflect(m)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // Blow up here rather than let then thing go on
            // throwing more exceptions
            System.exit(-1);
        }
        // This code path never gets reached but the compiler
        // is not clever enough to know that :(
        return _callSite;
        
    }    

    
    /** The method which is invoked statically and dynamically */
    public static long performCalcLong(long a,long b)
    {
        return a + b;
    }
        
    private static void  makeInvokeDynamic(MethodVisitor mv)
    {
        // This creates a boot strap definition
        try
        {
            java.lang.invoke.MethodType mt = java.lang.invoke.MethodType.methodType
            (
                CallSite.class,
                MethodHandles.Lookup.class,
                String.class,
                java.lang.invoke.MethodType.class
            );
            System.out.println(mt.toMethodDescriptorString());
            Handle bootstrap = new Handle
            (
                Opcodes.INVOKESTATIC, 
                "org/bullet/asm/DynamicBencher", 
                "bootstrap",
                mt.toMethodDescriptorString()
            );
            mv.visitInvokeDynamicInsn("runCalculation", "(JJ)J", bootstrap,new Object[0]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}