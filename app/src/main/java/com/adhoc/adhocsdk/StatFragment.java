package com.adhoc.adhocsdk;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import com.adhoc.utils.Utils;
import com.adhoc.viewtool.ReflectionUtil;
import com.adhoc.utils.T;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by dongyuangui on 15-5-27.
 */
public class StatFragment implements PageStat {
    private Context context;
    private static StatFragment ourInstance = new StatFragment();
    private static final int DETER = 3000;

    public static StatFragment getInstance() {
        return ourInstance;
    }

    public boolean resumeForeground;
    private ArrayList<String> listenedActivitys = null;

    private StatFragment() {
        listenedActivitys = new ArrayList<>();
    }

    public static ArrayList<Object> list = new ArrayList<Object>();

    final class FragmentStatBean {

        private String name;
        private long t1;
        private int id;

    }

    private FragmentStatBean from;
    private FragmentStatBean to;


    /**
     * 查看Fragment的状态
     **/
    public synchronized void dump() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DETER);
                    // 去掉父类的fragment，保留叶子Fragment 避免统计的重复
                    reMoveParents();
                    T.i("list size after delete:" + list.size());

                    // fragment加载顺序，父类的fragment首先加载

                    for (int i = 0; i < list.size(); i++) {
                        Object fragment = list.get(i);
                        // null able
                        if (fragment == null) {
                            T.w("fragment null ! position: " + i);
                            continue;
                        }
                        // 判断当前fragment是否可见
                        boolean visiable = ifVisiable(fragment);

                        // 父类fragment也可见的情况下
                        if (visiable && fatherIsVisiable(fragment)) {

                            // 有可能有两个fragment true；fatherIsVisiable可以去掉伪可见
                            to = new FragmentStatBean();
                            to.id = fragment.hashCode();
                            to.name = fragment.getClass().getSimpleName();
                            // 延迟，为了fragment的状态的准确
                            // fragment onResume 判断不了fragment的状态
                            // 包括visiable，hidden，hasMenu,isHitUser都不能单独判断
                            // ishitUser在ViewPager中才回调用，万幸的时默认值是true
                            // isVisiable方法执行时机不对，不能得到正确结果，需要Fragment op 操作之后delay的原因
                            to.t1 = System.currentTimeMillis() - DETER;

                            T.i(fragment.getClass().getSimpleName() + " visiable : hituser & fathervisiable all  true");
                            // 发送请求
                            sendRequest(context);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    T.e(e);
                } catch (Throwable e) {
                    T.e(e);
                }
            }
        }).start();

    }

    /**
     * 查看Fragment是否可见
     */
    private boolean ifVisiable(Object fragment) {
        Method visiableMethod = ReflectionUtil.getMethodByClassName(fragment.getClass(), "isVisible", "Fragment");
        Method hituserMethod = ReflectionUtil.getMethodByClassName(fragment.getClass(), "getUserVisibleHint", "Fragment");
//        boolean fatherVisiable = fatherIsVisiable(fragment);
        boolean visiable = false;
        boolean hituser = false;
        try {
            visiable = (Boolean) visiableMethod.invoke(fragment);
            hituser = (Boolean) hituserMethod.invoke(fragment);
        } catch (IllegalAccessException e) {
            T.e(e);
        } catch (Throwable e) {
            T.e(e);
        }
        return visiable && hituser;
    }

    /**
     * 查看Fragment 父、爷、曾爷是否可见
     */
    private boolean fatherIsVisiable(Object fragment) {


        Object parent = null;
        while ((parent = getParentObj(fragment)) != null) {
            boolean result = ifVisiable(parent);
            T.i("father : isvisiable" + result + " " + fragment.getClass().getName());
            if (result == false) {
                return false;
            } else {
                return fatherIsVisiable(parent);
            }
        }
        return true;
    }


    /**
     * Fragment的父类对象
     */
    private Object getParentObj(Object fragment) {
        // 去掉父fragment
        try {
            Field field = ReflectionUtil.getFieldByClassName(fragment.getClass(), "mParentFragment", "Fragment");

            return ReflectionUtil.getFieldValue(field, fragment);

        } catch (NoSuchFieldException e) {
            T.e(e);
        } catch (Throwable e) {
            T.e(e);
        }
        return null;
    }

    /**
     * 去掉dump列表中父类的Fragment
     */
    private void reMoveParents() {

        ArrayList<Object> parents = getParents();

        for (Iterator<Object> iterator = list.iterator(); iterator.hasNext(); ) {

            Object obj = iterator.next();

            if (isParent(obj, parents)) {

                iterator.remove();

            }
        }
    }

    /**
     * 查看Fragment是否是在Parents列表中
     */
    private boolean isParent(Object obj, ArrayList<Object> parents) {
        for (Object item : parents) {

            if (item.hashCode() == obj.hashCode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 得到父类的列表
     */
    private ArrayList<Object> getParents() {

        ArrayList<Object> parents = new ArrayList<Object>();

        for (int i = 0; i < list.size(); i++) {
            Object obj = getParentObj(list.get(i));
            if (obj != null) {
                parents.add(obj);
            }
        }
        return parents;
    }

    private class ViewDrawChange implements ViewTreeObserver.OnGlobalLayoutListener {
        long lastTime = 0L;

        @Override
        public void onGlobalLayout() {
            T.i("global view  complete-------------fragment ! ");
//            统计fragment 页面切换。
//            todo Result: only 执行一次
            if ((System.currentTimeMillis() - lastTime) > 3000) {
                lastTime = System.currentTimeMillis();
                T.i("to dump ----------------");
                StatFragment.getInstance().dump();
            }
        }
    }

    /**
     * Oncreate 中加入fragment，作为dump列表
     */
    public void add(Context context, Object fragment) throws IllegalAccessException {
        T.i("fragment onCreate" + fragment.getClass().getName());
        this.context = context;
        if (context instanceof Activity) {
            String className = context.getClass().getName();
            T.i("className is " + className);
            if (!checkListened(className)) {
                listenedActivitys.add(className);
                View view = ((Activity) context).getWindow().getDecorView();
                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewDrawChange());
            }
        }

        list.add(fragment);
    }

    private boolean checkListened(String className) {

        for (int i = 0; i < listenedActivitys.size(); i++) {
            if (className.equals(listenedActivitys.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * destory Fragment 从dump列表去掉fragment
     */
    public void onFragmentDestory(Object fragment) {
        list.remove(fragment);
        if (list.size() == 0) {
            T.i("退出程序clearlistener");
            listenedActivitys.clear();
        }
        T.i("list-----------size : " + list.size());
    }


    public void onBack2Menu(Context context) {
        if (from != null) {
            T.i("onBack2Menu : from " + from.name + "is null");
        }
        to = null;
        on2MenuSendStay(context);
        from = null;
        // 如果应用切换到后台,并且是tester,用户可能会去扫码,这时候设置获取flag的needrefresh = true;
        if (Utils.checkIsTesterDevice(context, AdhocConstants.TEST_PACKAGE_NAME)) {
            GetExperimentFlag.getInstance(context).neeedRefreshRightNow = true;
        }
    }


    @Override
    public void sendRequest(Context context) {
//        T.i("send request method!");
        if (from != null) {

            T.i("send request to " + to.id + " to .name " + to.name);
            if (from.id != to.id) { // 避免从A到A

                long duration = to.t1 - from.t1;
                // 小于200毫秒不记录统计
                if (duration < 200) {
                    T.i("小于 200 毫秒 忽略页面跳转");
                }
                T.i("from " + from.name + "id:" + from.id);
                double staytime = (to.t1 - from.t1) / 1000.0f;

                BigDecimal bg = new BigDecimal(staytime);
                double f1 = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

                AdhocTracker.incrementStat(context, "Staytime-" + from.name, f1);

                // request staytime
                AdhocTracker.incrementStat(context, "Event-" + from.name +
                        "-" + to.name, 1);
                from = to;
                T.i("set from is " + to.name);
            } else {
                T.i("id 相同");
            }

        } else {
            T.i("from  null");
            AdhocTracker.incrementStat(context, "Event-null-" + to.name, 1);
            from = to;
            T.i("set from is " + to.name);
        }
        T.i("to " + to.name + "id:" + to.id);
    }

    private void on2MenuSendStay(Context context) {
        if (from != null) {
            T.i("2 munu send request");
            double statytime = (System.currentTimeMillis() - from.t1) / 1000.0f;
            BigDecimal bg = new BigDecimal(statytime);
            double f1 = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            AdhocTracker.incrementStat(context, "Staytime-" + from.name,
                    f1);
        }
    }
}

