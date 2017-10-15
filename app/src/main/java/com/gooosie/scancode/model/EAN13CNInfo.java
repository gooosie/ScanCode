package com.gooosie.scancode.model;

import android.text.TextUtils;

import com.gooosie.scancode.util.StringAppendUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * EAN13CNInfo
 */

public class EAN13CNInfo extends BaseModel {
    private static final String STATUS_CODE = "c";
    private static final String DETAIL = "d";
    private static final String ITEM_SPECIFICATION = "ItemSpecification";
    private static final String ITEM_DESCRIPTION = "ItemDescription";
    private static final String KEEP_ON_RECORD = "keepOnRecord";
    private static final String ITEM_WIDTH = "ItemWidth";
    private static final String ITEM_HEIGHT = "ItemHeight";
    private static final String ITEM_DEPTH = "ItemDepth";
    private static final String BRAND_NAME = "BrandName";
    private static final String ITEM_NAME = "ItemName";
    private static final String FIRM_NAME = "FirmName";
    private static final String FIRM_LOGOUT_DATE = "FirmLogoutDate";

    private String itemSpecification;
    private String itemDescription;
    private String brandName;
    private String itemWidth;
    private String itemHeight;
    private String itemDepth;
    private String itemName;
    private String keepOnRecord;
    private String firmName;
    private String firmLogoutDate;

    public String getItemSpecification() {
        return itemSpecification;
    }

    public void setItemSpecification(String itemSpecification) {
        this.itemSpecification = itemSpecification;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(String itemWidth) {
        this.itemWidth = itemWidth;
    }

    public String getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(String itemHeight) {
        this.itemHeight = itemHeight;
    }

    public String getItemDepth() {
        return itemDepth;
    }

    public void setItemDepth(String itemDepth) {
        this.itemDepth = itemDepth;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getKeepOnRecord() {
        return keepOnRecord;
    }

    public void setKeepOnRecord(String keepOnRecord) {
        this.keepOnRecord = keepOnRecord;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getFirmLogoutDate() {
        return firmLogoutDate;
    }

    public void setFirmLogoutDate(String firmLogoutDate) {
        this.firmLogoutDate = firmLogoutDate;
    }

    public static EAN13CNInfo parse(String jsonString) {
        try {
            JSONObject json = (JSONObject) new JSONTokener(jsonString).nextValue();

            if (!json.optString(STATUS_CODE).equals("200")) {
                return null;
            }

            json = json.optJSONObject(DETAIL);
            if (json == null) {
                return null;
            }

            EAN13CNInfo info = new EAN13CNInfo();
            info.setItemSpecification(json.optString(ITEM_SPECIFICATION));
            info.setItemDescription(json.optString(ITEM_DESCRIPTION));
            info.setKeepOnRecord(json.optString(KEEP_ON_RECORD));
            info.setItemWidth(json.optString(ITEM_WIDTH));
            info.setItemHeight(json.optString(ITEM_HEIGHT));
            info.setItemDepth(json.optString(ITEM_DEPTH));
            info.setBrandName(json.optString(BRAND_NAME));
            info.setItemName(json.optString(ITEM_NAME));
            info.setFirmName(json.optString(FIRM_NAME));
            info.setFirmLogoutDate(json.optString(FIRM_LOGOUT_DATE));
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(1024);
        StringAppendUtil.appendDetail(builder, "商品", itemName);
        StringAppendUtil.appendDetail(builder, "商标", brandName);
        StringAppendUtil.appendDetail(builder, "规格", itemSpecification);
        StringAppendUtil.appendDetail(builder, "宽度", itemWidth);
        StringAppendUtil.appendDetail(builder, "高度", itemHeight);
        StringAppendUtil.appendDetail(builder, "深度", itemDepth);
        StringAppendUtil.appendDetail(builder, "更多", itemDescription);
        StringAppendUtil.appendDetail(builder, "企业名称", firmName);
        StringAppendUtil.appendDetail(builder, "注销时间", firmLogoutDate);
        if (!TextUtils.isEmpty(keepOnRecord)) {
            StringAppendUtil.appendDetail(builder, "子公司", keepOnRecord.replace(',', '\n'));
        }
        return builder.toString();
    }
}

