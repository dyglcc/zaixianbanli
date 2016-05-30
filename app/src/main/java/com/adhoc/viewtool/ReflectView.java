package com.adhoc.viewtool;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.adhoc.adhocsdk.AdhocTracker;
import com.adhoc.adhocsdk.AdhocWindowCallback;
import com.adhoc.adhocsdk.ScrollAbslistview;
import com.adhoc.utils.T;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 * Created by dongyuangui on 15-5-7.
 */
public class ReflectView {


    public static View.OnClickListener invokeListener(final Context context, final View view, final String key) {
        View.OnClickListener listener_src = null;
        try {
            if (view == null) {
                return null;
            }
            Field field = ReflectionUtil.getFieldByName(view.getClass(), "mListenerInfo", "android.view.View");
            if (field == null) {
                T.w("set stats could not find field!");
                return null;
            }
            view.setClickable(true);
            try {
                Object listenerObj = ReflectionUtil.getFieldValue(field, view);
                Class clazz_infoListener = Class.forName("android.view.View$ListenerInfo");
                Field mOnclickListenerField = ReflectionUtil.getField(clazz_infoListener, "mOnClickListener");
                if (listenerObj != null) {
                    listener_src = (View.OnClickListener) ReflectionUtil.getFieldValue(mOnclickListenerField, listenerObj);
                }

                final View.OnClickListener finalListener_src = listener_src;
                View.OnClickListener newListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 追踪代码
                        AdhocTracker.incrementStat(context, key, 1);

//                        Toaster.toast(context, "view is tracked " + view.getClass().getName());
                        T.i("已经被跟踪" + view.getClass().getName());

                        if (finalListener_src != null) {

                            finalListener_src.onClick(view);
                        }

                    }
                };
                if (listenerObj != null) {
                    ReflectionUtil.setFieldValue(mOnclickListenerField, listenerObj, newListener);
                } else {
                    view.setOnClickListener(newListener);
                }


            } catch (IllegalAccessException e) {
                T.e(e);
            } catch (ClassNotFoundException e) {
                T.e(e);
            } catch (Throwable ex) {
                T.e(ex);
            }

        } catch (NoSuchFieldException e) {
            T.e(e);
        } catch (Throwable ex) {
            T.e(ex);
        }

        return listener_src;
    }

    public static AdhocWindowCallback setAdhocWindowCallback(Window window, Activity context) {
        if (window == null) {
            return null;
        }
        Field field = null;
        try {
            field = ReflectionUtil.getField(window.getClass(), "mCallback");
        } catch (NoSuchFieldException e) {
            T.e(e);
        }
        if (field == null) {
            T.w("set stats could not find field!");
            return null;
        }
        Window.Callback listenerObj = null;
        try {
            listenerObj = (Window.Callback) ReflectionUtil.getFieldValue(field, window);
        } catch (IllegalAccessException e) {
            T.e(e);
        }
        // create callback
        AdhocWindowCallback callback = new AdhocWindowCallback(listenerObj);
        callback.setWindowManager(window.getWindowManager());
        callback.setContext(context);
        window.setCallback(callback);
        return callback;
    }

    public static Object getPagerView(Object pager, int currentPos) {
        try {
            Field field = pager.getClass().getDeclaredField("mItems");
            field.setAccessible(true);
            Object arraylist = ReflectionUtil.getFieldValue(field, pager);
            Field arrayfield = arraylist.getClass().getDeclaredField("array");
            Object array = ReflectionUtil.getFieldValue(arrayfield, arraylist);
            Object[] arrays = (Object[]) array;
            int count = arrays.length;
            T.i("array length :" + count);
            for (int i = 0; i < count; i++) {

                Object iteminfo = arrays[i];
                if (iteminfo == null) {
                    continue;
                }
                Field positionField = iteminfo.getClass().getDeclaredField("position");
                Object positionValue = ReflectionUtil.getFieldValue(positionField, iteminfo);
                T.i("check values :" + positionValue.toString());
                if ((Integer) positionValue == currentPos) {

                    Field viewField = iteminfo.getClass().getDeclaredField("object");
                    Object viewValue = ReflectionUtil.getFieldValue(viewField, iteminfo);
                    if (viewValue != null) {
                        T.i("find values :" + viewValue.getClass().getName() + " value : " + viewValue.toString());
                        return viewValue;
                    }
                }

            }
        } catch (NoSuchFieldException e) {
            T.e(e);
        } catch (IllegalAccessException e) {
            T.e(e);
        } catch(Throwable e){
            T.e(e);
        }
        return null;
    }

    public static void invokeListViewOnscrollListener(final AbsListView absListView,
                                                      ScrollAbslistview newListener) {
        try {
            if (absListView == null) {
                return;
            }
            Field field = ReflectionUtil.getFieldByName(absListView.getClass(), "mOnScrollListener", "android.widget.AbsListView");
            if (field == null) {
                T.w("set stats could not find field!");
                return;
            }
            try {
                Object listenerObj = ReflectionUtil.getFieldValue(field, absListView);
                if (listenerObj != null) {
                    newListener.setOldListener((AbsListView.OnScrollListener) listenerObj);
                    ReflectionUtil.setFieldValue(field, absListView, newListener);
                } else {
                    absListView.setOnScrollListener((AbsListView.OnScrollListener) newListener);
                }


            } catch (IllegalAccessException e) {
                T.e(e);
            }


        } catch (Throwable e) {
            T.e(e);
        }
    }

    public static int getViewPagerCurrentItemPos(final View view) throws IllegalAccessException {

        Field field = null;
        try {
            field = ReflectionUtil.getField(view.getClass(), "mCurItem");
        } catch (Throwable e) {
            T.e(e);
        }
        if (field == null) {
            T.w("set stats could not find field!");
            return -1;
        }

        return (Integer) ReflectionUtil.getFieldValue(field, view);

    }

    // 代理的方式来获得对象，上面使用新建对象，原理一样，下面的方法更通用
    public static Object invokeListenerByProxy(Object target) {

        ReplaceHandler handler = new ReplaceHandler(target);

        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), handler);

    }

    public static void getViewParents(Class clazz, ArrayList<String> parents) {
        if (clazz != null) {
            getViewParents(clazz.getSuperclass(), parents);
            if (!clazz.getName().equals("java.lang.Object")) {
                parents.add(clazz.getName());
            }
        }
    }

    public static View[] getRootView(WindowManager manager) {
        View[] views = null;
        try {
            if (manager == null) {
                return null;
            }
            Field field = ReflectionUtil.getFieldByName(manager.getClass(), "mGlobal", "android.view.WindowManagerImpl");
            if (field == null) {
                T.w("set stats could not find field!");
            }
            try {
                Object globalValue = ReflectionUtil.getFieldValue(field, manager);
                Class clazz_Global = Class.forName("android.view.WindowManagerGlobal");
                Field mViewFields = ReflectionUtil.getField(clazz_Global, "mViews");
                // todo views 5.0.2是arraylist 4.3是views
                Object objValue = null;
                if (globalValue != null) {
                    objValue = ReflectionUtil.getFieldValue(mViewFields, globalValue);
                }
                if (objValue != null) {
                    if (objValue instanceof View[]) {
                        views = (View[]) objValue;
                    } else if (objValue instanceof ArrayList) {
                        ArrayList list = ((ArrayList) objValue);
                        if (list != null && list.size() > 0) {
                            if (views == null) {
                                views = new View[list.size()];
                            }
                            list.toArray(views);
                        }
                    }
                }
//                T.i("vvvvvvvvvvvvvvview size " + views.length);

            } catch (IllegalAccessException e) {
                T.e(e);
            } catch (ClassNotFoundException e) {
                T.e(e);
            }


        } catch (NoSuchFieldException e) {
            T.e(e);
        }

        return views;

    }
}
