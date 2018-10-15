package com.taoyr.app.utility;

import java.util.Collection;
import java.util.Map;

public class ValidUtil {

    /**
     * 进行空参校验,可以检验 null 空字符串,undefined字符串,空集合,空map
     * @param args 可以传多个参数进行空参检验
     * @return
     */
    public static boolean isEmpty(Object ... args){
        for(Object o :args){
            System.out.println(o);
            //如果是null,返回true
            if(o==null){
                return true;
            }
            //如果是集合类
            if (o instanceof Collection) {
                Collection c = (Collection)o;
                //集合大小为0  返回true
                if(c.size()==0){
                    return true;
                }
            }
            //如果是Map集合
            if (o instanceof Map) {
                Map m = (Map) o;
                if(m.size()==0){
                    return true;
                }
            }
            //其他对象
            if ("".equals(o.toString()) || "null".equals(o.toString()) || "undefined".equals(o.toString())) {
                return true;
            }
        }
        return false;
    }



}