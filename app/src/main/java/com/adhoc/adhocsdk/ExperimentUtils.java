package com.adhoc.adhocsdk;

import android.content.Context;

import com.adhoc.http.internal.Util;
import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dongyuangui on 16/2/23.
 */
public class ExperimentUtils {
    private static ExperimentUtils instance = null;
    public static final String EXPERIMENTS = "experiments";
    public static final String FLAGS = "flags";
    public static final String ALLCALLED = "allcalled";
    public static final String CONTROL = "CONTROL";
    public static final String ID = "id";
    public static final String NAME = "name";
    private Context context;

    private HashMap<String, Experiment> exps = new HashMap<String,Experiment>();
    private HashMap<String, Experiment> currentExpeiriment = new HashMap<String, Experiment>();
//    private HashMap<String, Object> keysCalc = new HashMap<>();

    public static ExperimentUtils getInstance() {

        if (instance == null) {
            instance = new ExperimentUtils();
        }
        return instance;
    }

    private ExperimentUtils() {
    }


    public boolean flagAreCalled(String key) {
        Experiment experiment = exps.get(key);
        return experiment.isAllcalled();
    }

    public void setContext(Context context) {

        this.context = context;
    }

    public void setCalled(String key) {
        try {
            Experiment experiment = exps.get(key);
            if (experiment == null) {
                return;
            }
            experiment.setCalled(key);
            // 检查是否都已经都被调用了
            if (experiment.checkAllCalled()) {

                // 如果都被调用了 设置value true
                // 保存到本地信息
                experiment.setIsAllcalled(true);

            }
            try {
                T.i("触发保存used flag " + key);
                setShareExperiment();
            } catch (JSONException e) {
                T.e(e);
            }
        } catch (Throwable e) {
            T.e(e);
        }

    }

    protected void setShareExperiment() throws JSONException {
        if (context == null) {
            return;
        }
        JSONArray array = new JSONArray();

        for (Map.Entry<String, Experiment> entry : currentExpeiriment.entrySet()) {
            JSONObject experimentObj = new JSONObject();
            String id = entry.getKey();
            Experiment experiment = entry.getValue();
            String name = experiment.getName();
            HashMap<String, Boolean> usedkeys = experiment.getUsedFlags();
            JSONArray usedflagsObj = new JSONArray();
            for (Map.Entry<String, Boolean> keys : usedkeys.entrySet()) {

                JSONObject flags = new JSONObject();
                flags.put(keys.getKey(), keys.getValue());
                usedflagsObj.put(flags);
            }
            experimentObj.put(ExperimentUtils.FLAGS, usedflagsObj);
            experimentObj.put(ExperimentUtils.ALLCALLED, experiment.isAllcalled());
            experimentObj.put(ExperimentUtils.ID, id);
            experimentObj.put(ExperimentUtils.NAME,name);
            array.put(experimentObj);

        }
        Utils.saveStringShareData(Utils.getSharePreference(context),
                ExperimentUtils.EXPERIMENTS, array.toString());
        T.i("save str :" + array.toString());
    }

    public void reportRenderRequest(JSONObject mFlags, String key) {
        try {
            if (!Utils.isCanConnectionNetWork(context)) {
                return;
            }
//            T.i(mFlags == null ? "mflag is null " : mFlags.toString());
            if (mFlags != null) {
                JSONObject flags = mFlags.optJSONObject(ExperimentUtils.FLAGS);
                if (flags == null || !flags.has(key)) {
                    return;
                }
                T.i("used flag " + key);
                // 检查是否已经渲染完成,所有试验中的flag都被调用过.
                // 先检查是否已经上报过
                // 没有上报过,保存key已经被调用.
                if (key != null && exps.containsKey(key)) {
                    boolean flagAreCalled = ExperimentUtils.getInstance().flagAreCalled(key);
                    if (!flagAreCalled) {
                        boolean flagIsCalled = ExperimentUtils.getInstance().checkSingleFlagIsCalled(key);
                        T.i("flag : " + key + " is called " + flagIsCalled);
                        if (!flagIsCalled) {
                            T.i("flag is set true");
                            ExperimentUtils.getInstance().setCalled(key);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            T.e(e);
        }
    }

    private boolean checkSingleFlagIsCalled(String key) {
        Experiment experiment = exps.get(key);
        if (experiment == null) {
            return false;
        }
        return experiment.checkIsCalled(key);
    }

    // 从本地获取flag
    public void loadLocalExperiments() {

        String string = Utils.getStringShareData(Utils.getSharePreference(context), ExperimentUtils.EXPERIMENTS);
        if (string == null || string.equals("")) {
            return;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(string);
        } catch (JSONException e) {
            T.e(e);
        }
        if (jsonArray == null || jsonArray.length() == 0) {
            return;
        }
        T.i("load experiments from loacal");
        if (exps.isEmpty()) {

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object1 = jsonArray.getJSONObject(i);

                    addElement(object1);

                } catch (JSONException e) {
                    T.e(e);
                }
            }
        }
    }

    public synchronized void updateExperiments(JSONObject object) {

        try {
            T.i("current experiment size :" + currentExpeiriment.size());
            JSONArray jsonArray = null;
            if(object == null){
                return ;
            }
            try {
                jsonArray = object.getJSONArray("experiments");
            } catch (JSONException e) {
                T.e(e);
            }
            if (jsonArray == null || jsonArray.length() == 0) {
                return;
            }
            T.i("update Experiment from network");

            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.optString("id", "");
                    if (!currentExpeiriment.containsKey(id)) {
                        Experiment experimentNew = addElementFromNetWork(obj);
                        experimentNew.setIsUpdate(true);
                        T.i("check experiment is new then add ");
                    } else {
                        Experiment experiment = currentExpeiriment.get(id);
                        if (experiment != null) {
                            experiment.setIsUpdate(true);
                        }
                        T.i("check experiment is same");
                    }
                } catch (JSONException e) {
                    T.e(e);
                }
            }
            // 删除sharepref 中对旧的实验的保存
            Iterator it = currentExpeiriment.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Experiment> entry = (Map.Entry<String, Experiment>) it.next();
                Experiment experiment = entry.getValue();
                if (experiment != null) {
                    if (!experiment.isUpdate()) {
                        it.remove();
                        // 删除experiment 需要保存新的json到 Experiment
                        try {
                            setShareExperiment();
                        } catch (JSONException e) {
                            T.w("error save sharepref");
                        }
                        T.i("remove id :" + experiment.getId());
                    }
                }
            }

            // 初始化isUpdate false
            for (Map.Entry<String, Experiment> entry : currentExpeiriment.entrySet()) {
                Experiment experiment = entry.getValue();
                if (experiment != null) {
                    experiment.setIsUpdate(false);
                }
            }
        } catch (Throwable e) {
            T.e(e);
        }
    }

    private Experiment addElement(JSONObject object1) {
        JSONArray keys = null;

        if (object1 == null) {
            return null;
        }
        T.i(object1.toString());
        try {
            keys = object1.getJSONArray("flags");
        } catch (JSONException e) {
            T.e(e);
        }
        if (keys == null) {
            return null;
        }
        int size = keys.length();

        Experiment experiment = new Experiment();
        String id = object1.optString("id");
        String name = object1.optString("name");
        experiment.setId(id);
        experiment.setName(name);
//        experiment.setIsAllcalled();
        currentExpeiriment.put(id, experiment);
        for (int i = 0; i < size; i++) {
            try {
                JSONObject object = keys.getJSONObject(i);
                Iterator<String> iterator = object.keys();
                String key = iterator.next();
                boolean value = object.optBoolean(key, false);
                exps.put(key, experiment);
                experiment.initKeys(key, value);
            } catch (JSONException e) {
                T.e(e);
            }
        }
        // 获取本地allcalled
        experiment.setIsAllcalled(object1.optBoolean(ALLCALLED, false));
        T.i("load experiment from local : " + experiment.isAllcalled());

        return experiment;
    }

    private Experiment addElementFromNetWork(JSONObject object1) {
        T.i("add Experiment form network" + object1.toString());
        JSONArray keys = null;

        if (object1 == null) {
            return null;
        }
        T.i(object1.toString());
        try {
            keys = object1.getJSONArray(ExperimentUtils.FLAGS);
        } catch (JSONException e) {
            T.e(e);
        }
        if (keys == null) {
            return null;
        }
        int size = keys.length();

        Experiment experiment = new Experiment();
        String id = object1.optString("id");
        String name = object1.optString("name");
        experiment.setId(id);
        experiment.setName(name);
//        experiment.setIsAllcalled();
        currentExpeiriment.put(id, experiment);
        for (int i = 0; i < size; i++) {
            try {
                JSONObject object = keys.getJSONObject(i);
                Iterator<String> iterator = object.keys();
                String key = iterator.next();
                boolean value = object.optBoolean(key, false);
                exps.put(key, experiment);
                experiment.initKeys(key, value);
            } catch (JSONException e) {
                T.e(e);
            }
        }
        // 从网络获取
        experiment.setIsAllcalled(experiment.checkAllCalled());
        try {
            T.i("add element net work : " + experiment.isAllcalled());
            setShareExperiment();
        } catch (JSONException e) {
            T.e(e);
        }
        T.i("add element net work : " + experiment.isAllcalled());

        return experiment;
    }


    public JSONArray getExperimetnsStrs() {
        JSONArray array = new JSONArray();

        for (Map.Entry<String, Experiment> entry : currentExpeiriment.entrySet()) {
            Experiment currExperiment = entry.getValue();
            if (currExperiment.isAllcalled() || CONTROL.equals(currExperiment.getId())) {
                array.put(currExperiment.getId());
            }
        }
        if (array.length() == 0) {
            array.put(ExperimentUtils.CONTROL);
        }
        return array;

    }

    public JSONArray getCurrentExperimetnsStrs() {
        JSONArray array = new JSONArray();

        for (Map.Entry<String, Experiment> entry : currentExpeiriment.entrySet()) {
            Experiment currExperiment = entry.getValue();
            if (currExperiment.isAllcalled() || CONTROL.equals(currExperiment.getId())) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", currExperiment.getName());
                    obj.put("id", currExperiment.getId());
                    array.put(obj);
                } catch (Throwable e) {
                    T.e(e);
                }

            }
        }
        if (array.length() == 0) {
            try {
                JSONObject object = new JSONObject();
                object.put("name", ExperimentUtils.CONTROL);
                object.put("id", ExperimentUtils.CONTROL);
                array.put(object);
            } catch (Throwable e) {
                T.e(e);
            }
        }
        return array;

    }

    public Double getAllValue(Context context, String key, String experimentid) {
        try {
            String keyexpeimentid = key + AdhocConstants.fgf + experimentid;
            String sharevalue = Utils.getStringShareData(Utils.getSharePreference(context), keyexpeimentid);
            if (sharevalue.equals("")) {
                return 0d;
            }
            Double count = Double.parseDouble(sharevalue);
            return count;
        } catch (Throwable e) {
            T.e(e);
        }
        return 0d;
    }

    public Double saveAllvalue(Context context, String key, String expId, Object value, Double allvalue) {
        try {
            String keyid = key + AdhocConstants.fgf + expId;

            if (value == null) {
                return 0d;
            }
            Double current = Double.parseDouble(value.toString());
            Double count = allvalue.doubleValue() + current.doubleValue();
            Utils.saveStringShareData(Utils.getSharePreference(context), keyid, count.toString());
            return count;
        } catch (Throwable e) {
            T.e(e);
        }
        return allvalue;
    }

}
