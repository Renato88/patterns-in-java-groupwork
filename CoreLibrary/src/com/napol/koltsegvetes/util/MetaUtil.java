package com.napol.koltsegvetes.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.dbdriver.ISQLCommands;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Oct 17, 2014 8:48:14 PM
 */
public class MetaUtil
{
    public static String ModifierToPlantUML(int mod)
    {
        String pref = "";
        if (Modifier.isPrivate(mod)) pref += "- ";
        if (Modifier.isPublic(mod)) pref += "+ ";
        if (Modifier.isProtected(mod)) pref += "# ";
        if (Modifier.isAbstract(mod)) pref += "{abstract} ";
        if (Modifier.isStatic(mod)) pref += "{static} ";

        return pref;
    }
    
    public static void GeneratePlantUML(Class<?> targetClass)
    {
        System.out.println(targetClass.getCanonicalName() + " {");
        
        for (Field f : targetClass.getDeclaredFields())
        {
            if (f.isEnumConstant())
            {
                System.out.println("\t" + f.getName());
                continue;
            }
            
            String[] name = f.getType().getName().split("[. ]+");

            System.out.println("\t" + ModifierToPlantUML(f.getModifiers()) + f.getName() + " : " + name[name.length - 1]);
        }

        for (Method m : targetClass.getDeclaredMethods())
        {
            if (m.getName().contains("access$")) continue;

            Type[] types = m.getGenericParameterTypes();
            Class<?>[] arg = m.getParameterTypes();

            String function_params = "";
            for (int i = 0; i < types.length; ++i)
            {
                String generic_part = "";
                String simple_type_part = "";
                String type_suf = "";
                try
                {
                    String[] gensplit = types[i].toString().split("[<>]+");
                    String[] generics = gensplit[1].split("[, ]+");
                    simple_type_part = gensplit[0];
                    for (String g : generics)
                    {
                        String[] tmp = g.split("[. ]+");
                        generic_part += ", " + tmp[tmp.length - 1];
                    }
                    generic_part = "<" + generic_part.substring(2) + ">";
                }
                catch (IndexOutOfBoundsException e)
                {
                    simple_type_part = types[i].toString();
                }

                String[] name = simple_type_part.split("[. ]+");
                String typename = name[name.length - 1];

                if (simple_type_part.contains("[L"))
                {
                    type_suf += "[]";
                    typename = typename.substring(0, typename.length() - 1);
                }

                function_params += ", " + typename + generic_part + type_suf;
            }
            try
            {
                function_params = "(" + function_params.substring(2) + ")";
            }
            catch (StringIndexOutOfBoundsException e)
            {
                function_params = "()";
            }

            String[] ret = m.getGenericReturnType().toString().split("[. ]+");
            System.out.println("\t" + ModifierToPlantUML(m.getModifiers()) + m.getName() + function_params + " : " + ret[ret.length - 1]);
        }
        System.out.println("}\n");
    }

    public static void main(String[] args)
    {
        GeneratePlantUML(AbstractDataStore.class);
        GeneratePlantUML(AbstractQuery.class);
        GeneratePlantUML(EColumnNames.class);
        GeneratePlantUML(ETableNames.class);
        GeneratePlantUML(ISQLCommands.class);
        GeneratePlantUML(ISQLiteHelper.class);
    }
}
