package com.longying.bmsserver.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.longying.bmsbase.base.BaseConstants;
import com.longying.bmsbase.common.api.ApiException;
import com.longying.bmsserver.data.DataDict;
import com.longying.bmsserver.data.SystemSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create by chenglong on 2021/2/25
 */
@Slf4j
public class BaseService {

    @Autowired
    private RedisService redisService;

    protected void throwException(String excCode, Object ... placeholder) throws ApiException{
        ApiException exception = null;
        try {
            //从redis缓存中读取接口异常信息
            String apiExcCache = (String) redisService.get(BaseConstants.API_EXC_CACHE_KEY);
            if(StrUtil.isNotEmpty(apiExcCache)){
                JSONObject apiExcData = JSONUtil.parseObj(apiExcCache);
                JSONObject apiExcModel = apiExcData.getJSONObject(excCode);
                if(apiExcModel!=null){
                    exception = ApiException.builder()
                            .code(excCode)
                            .message(apiExcModel.getStr("print"))
                            .loginfo(getExceptionLogInfo(excCode
                                    ,apiExcModel.getStr("descript")
                                    ,apiExcModel.getStr("reason")
                                    ,apiExcModel.getStr("solution")
                                    ,placeholder))
                            .build();
                    throw exception;
                }
            }
        } catch (ApiException e){
            throw e;
        } catch (Throwable throwable){
            log.error("redis获取缓存接口异常信息发生异常，redis服务器可能宕机导致");
        }
        if(exception==null){
            exception = ApiException.builder()
                    .code(excCode)
                    .message("发生未知异常，请联系管理员")
                    .loginfo(getExceptionLogInfo(excCode
                            ,"未找到编码[{}]的异常信息，请在后台刷新缓存!"
                            ,"1.异常尚未维护到系统；2.未同步redis缓存"
                            ,"1.请查看接口异常信息是否已维护；2.请同步缓存"
                            , new Object[]{excCode}))
                    .build();
            throw exception;
        }
    }

    public static String getExceptionLogInfo(String excCode, String descript, String reason, String solution, Object[] placeholders){
        StringBuffer buffer = new StringBuffer();
        if(placeholders!=null && placeholders.length>0){
            for(Object item : placeholders){
                String itemStr = StrUtil.toString(item);
                itemStr = itemStr.replaceAll("\\$", "RDS_CHAR_DOLLAR");
                descript = descript.replaceFirst("\\{\\}", itemStr);
            }
            descript = descript.replaceAll("RDS_CHAR_DOLLAR","\\$");
        }
        buffer.append("【异常编码】："+excCode+"\r\n");
        buffer.append("【异常描述】："+descript+"\r\n");
        buffer.append("【异常原因分析】："+reason+"\r\n");
        buffer.append("【异常处理建议】："+solution);
        return buffer.toString();
    }

    public DataDict listDataDictByCode(String code){
        String cacheDataDictValue = (String) redisService.get(BaseConstants.DATA_DICT_CACHE_KEY);
        if(StrUtil.isNotEmpty(cacheDataDictValue)){
            JSONObject dataDictJSON = JSONUtil.parseObj(cacheDataDictValue).getJSONObject(code);
            if(dataDictJSON!=null){
                DataDict dataDict = DataDict.builder()
                        .name(dataDictJSON.getStr("name"))
                        .status(dataDictJSON.getStr("status"))
                        .items(DataDict.getDataItemList(dataDictJSON.getJSONArray("items")))
                        .build();
                return dataDict;
            }
        }
        return null;
    }

    public SystemSetting getSystemSettingByCode(String code){
        String cacheSettingValue= (String) redisService.get(BaseConstants.SYSTEM_SETTING_CACHE_KEY);
        if(StrUtil.isNotEmpty(cacheSettingValue)){
            JSONObject settingJSON = JSONUtil.parseObj(cacheSettingValue).getJSONObject(code);
            if(settingJSON!=null){
                SystemSetting setting = SystemSetting.builder()
                        .name(settingJSON.getStr("name"))
                        .value(settingJSON.getStr("value"))
                        .build();
                return setting;
            }
        }
        return null;
    }
}
