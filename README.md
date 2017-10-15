# 扫码

一个简单的扫码工具。

[![English](https://img.shields.io/badge/readme-%E4%B8%AD%E6%96%87%E7%89%88-brightgreen.svg?style=flat-square)](/README_EN.md) [![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](/LICENSE) [![release](https://img.shields.io/github/release/gooosie/ScanCode.svg?style=flat-square)](https://github.com/gooosie/ScanCode/releases)

## 简介

- 一个简单的扫码工具。
- 支持闪光灯。
- 可从图库选取识别。
- 可以复制条码。
- 可以保存条码图片。
- 目前支持的格式：UPC A, UPC E, EAN 8, EAN 13, Code 39, Code 93, Code 128, ITF, QR Code。
- 识别到EAN 13时，可以通过亚马逊查询。
- 识别到ISBN时，会调用[豆瓣图书api](https://developers.douban.com/wiki/?title=book_v2#get_isbn_book)查询图书信息。
- 识别到开头为690-695（中国大陆的商品）的EAN 13时，会调用[国家食品（产品）安全追溯平台](http://www.chinatrace.org/)的api查询商品信息。
- 识别到 http:// 或 https:// 开头的QR Code时可以直接访问。
- 识别到ss://开头的QR Code时可以打开Shadowsocks并添加配置。
- 允许其它应用调用。

## 使用

CaptureActivty一共注册了三个Action：

```xml
<activity
    android:name="com.gooosie.scancode.ui.activity.CaptureActivity"
    android:configChanges="orientation|keyboardHidden"
    android:launchMode="singleTask"
    android:screenOrientation="portrait"
    android:windowSoftInputMode="stateAlwaysHidden" >
    <intent-filter>
        <action android:name="com.gooosie.scancode.SCAN"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
    <intent-filter>
        <action android:name="com.google.zxing.client.android.SCAN"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="zxing" android:host="scan" android:path="/"/>
    </intent-filter>
</activity>
```

并在扫码成功时：

```java
@Override
public void onAnalyzeSuccess(Bitmap bitmap, Result result) {
    Intent resultIntent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putInt(CodeUtil.RESULT_TYPE, CodeUtil.RESULT_SUCCESS);
    bundle.putString(Intents.Scan.RESULT, result.getText());
    bundle.putString(Intents.Scan.RESULT_FORMAT, result.getBarcodeFormat().name());
    resultIntent.putExtras(bundle);
    mActivity.setResult(Activity.RESULT_OK, resultIntent);
    mActivity.finish();
}
```

在扫码失败时：

```java
@Override
public void onAnalyzeFailed() {
    Intent resultIntent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putInt(CodeUtil.RESULT_TYPE, CodeUtil.RESULT_FAILED);
    bundle.putString(Intents.Scan.RESULT, "");
    resultIntent.putExtras(bundle);
    mActivity.setResult(Activity.RESULT_OK, resultIntent);
    mActivity.finish();
}
```

其中：

```java
CodeUtil.RESULT_TYPE       // "RESULT_TYPE"
CodeUtil.RESULT_SUCCESS    // 1
CodeUtil.RESULT_FAILED     // 2
Intents.Scan.RESULT        // "SCAN_RESULT"
Intents.Scan.RESULT_FORMAT // "SCAN_RESULT_FORMAT"
```

使用实例：

```java
public void startCaptureActivity() {
    Intent intent = new Intent("com.gooosie.scancode.SCAN");
    startActivityForResult(intent, 100);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 100 && resultCode == RESULT_OK) {
        Bundle bundle = data.getExtras();
        String format = bundle.getString("SCAN_RESULT_FORMAT");
        String code = bundle.getString("SCAN_RESULT");   
        // ...
    }
}
```

## 截图

![Screenshot1](/docs/Screenshot_1.png)

![Screenshot2](/docs/Screenshot_2.png)

![Screenshot3](/docs/Screenshot_3.png)

![Screenshot4](/docs/Screenshot_4.png)

## 第三方库

- [ZXing](https://github.com/zxing/zxing)

## 下载

[下载](https://github.com/gooosie/ScanCode/releases)

## 开源协议

[Apache License 2.0](/LICENSE)