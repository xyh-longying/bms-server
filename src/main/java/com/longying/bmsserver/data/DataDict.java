package com.longying.bmsserver.data;

import cn.hutool.json.JSONArray;
import com.longying.bmsbase.base.DataDictItem;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by chenglong on 2021/2/28
 */
@Slf4j
@Data
@Builder
public class DataDict {

    private String name;
    private String status;
    private List<DataDictItem> items;


    public static List<DataDictItem> getDataItemList(JSONArray items){
        List<DataDictItem> itemList = new ArrayList<>();
        for(int i=0; i<items.size(); i++){
            DataDictItem item = DataDictItem.builder()
                    .itemName(items.getJSONObject(i).getStr("itemName"))
                    .itemValue(items.getJSONObject(i).getStr("itemValue"))
                    .chooseable(items.getJSONObject(i).getBool("chooseable"))
                    .build();
            itemList.add(item);
        }
        return itemList;
    }
}
