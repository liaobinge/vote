package com.nokia.vote;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;

public class SharedPrefUtil {
    private final String TAG="SharedPrefUtil";
    public final static String DEFAULT_CONFIG_NAME = "DEFAULT_CONFIG_NAME";
    protected SharedPreferences defaultPref = null;
    protected SharedPreferences sharedPref = null;

    /**
     * @param context
     * @param prefName
     * @return
     */
    public SharedPrefUtil init(Context context, String prefName) {
        if (TextUtils.isEmpty(prefName)) {
            defaultPref = context.getSharedPreferences(DEFAULT_CONFIG_NAME, Context.MODE_PRIVATE);
            sharedPref = defaultPref;
        } else {
            sharedPref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
        return this;
    }

    /**
     * 保存一个对象到sharedPreference
     *
     * @param obj
     * @return
     */
    public SharedPrefUtil save(Object obj) {
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        if (fields.length > 0) {
            try {
                SharedPreferences.Editor editor = sharedPref.edit();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (String.class.isAssignableFrom(field.getType())) {
                        editor.putString(field.getName(), (String) field.get(obj));
                    } else if (Boolean.class.isAssignableFrom(field.getType())
                            || boolean.class.isAssignableFrom(field.getType())) {
                        editor.putBoolean(field.getName(), field.getBoolean(obj));
                    } else if (Long.class.isAssignableFrom(field.getType())
                            || long.class.isAssignableFrom(field.getType())) {
                        editor.putLong(field.getName(), field.getLong(obj));
                    } else if (Integer.class.isAssignableFrom(field.getType()) ||
                            int.class.isAssignableFrom(field.getType())) {
                        editor.putInt(field.getName(), field.getInt(obj));
                    }
                }
                editor.apply();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * 读取一个指定值
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T loadObj(Class<T> tClass) {
        Field[] fields = tClass.getDeclaredFields();
        try {
            T t = tClass.newInstance();
            if (fields.length > 0) {
                try {
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (String.class.isAssignableFrom(field.getType())) {
                            field.set(t, sharedPref.getString(field.getName(), ""));
                        } else if (Boolean.class.isAssignableFrom(field.getType())
                                || boolean.class.isAssignableFrom(field.getType())) {
                            field.set(t, sharedPref.getBoolean(field.getName(), false));
                        } else if (Long.class.isAssignableFrom(field.getType())
                                || long.class.isAssignableFrom(field.getType())) {
                            field.set(t, sharedPref.getLong(field.getName(), 0));
                        } else if (Integer.class.isAssignableFrom(field.getType()) ||
                                int.class.isAssignableFrom(field.getType())) {
                            field.set(t, sharedPref.getInt(field.getName(), 0));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return t;
        } catch (Exception e) {
            Log.e(TAG, "%s没有默认的构造方法"+ tClass.getSimpleName());
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * 清除保存的像数据
     *
     * @param tClass
     * @param <T>
     */
    public <T> void clearObj(Class<T> tClass) {
        Field[] fields = tClass.getDeclaredFields();
        try {
            T t = tClass.newInstance();
            if (fields.length > 0) {
                for (Field field : fields) {
                    clear(field.getName());
                    Log.d(TAG,"清除%s"+field.getName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"%s没有默认的构造方法"+ tClass.getSimpleName());
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public SharedPrefUtil save(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public SharedPrefUtil save(String key, Long value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public SharedPrefUtil save(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
        return this;
    }

    /**
     * @param key
     * @param dvalue
     * @return
     */
    public boolean load(String key, Boolean dvalue) {
        return sharedPref.getBoolean(key, dvalue);
    }

    /**
     * @param key
     * @param dvalue
     * @return
     */
    public long load(String key, Long dvalue) {
        return sharedPref.getLong(key, dvalue);
    }

    /**
     * @param key
     * @return
     */
    public String load(String key) {
        return sharedPref.getString(key, null);
    }

    /**
     * @param key
     */
    public void clear(String key) {
        sharedPref.edit().remove(key).apply();
    }

    protected static class SingletonHolder {
        public static SharedPrefUtil instance = new SharedPrefUtil();
    }

    public static SharedPrefUtil getInstance() {
        SharedPrefUtil prefUtil = SingletonHolder.instance;
        prefUtil.sharedPref = prefUtil.defaultPref;
        return prefUtil;
    }

    public static SharedPrefUtil getDefaultPref(Context context) {
        return SingletonHolder.instance.init(context, DEFAULT_CONFIG_NAME);
    }
    private static final String page_size="PAGE_SIZE";
    private static final String need_switch_page="SWITCH_PAGE";
    private static final String tag_condition="TAG_CONDITION";
    public static int getPageSize(){
        String value=getInstance().load(page_size);
        return Integer.valueOf(value);
    }
    public static void setPageSize(int pageSize){
        getInstance().save(page_size,String.valueOf(pageSize));
    }
    public static boolean needSwitchPage(){
        return getInstance().load(need_switch_page,false);
    }
    public static void setSwitchNeed(boolean switchNeed){
        getInstance().save(need_switch_page,switchNeed);
    }
    public static String getCondition(){

        return getInstance().load(tag_condition);
    }
    public static void setCondition(String condition){
        getInstance().save(tag_condition,condition);
    }

}