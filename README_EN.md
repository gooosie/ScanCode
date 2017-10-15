# Scan Code

A simple scanning tool.

[![中文版](https://img.shields.io/badge/readme-%E4%B8%AD%E6%96%87%E7%89%88-brightgreen.svg?style=flat-square)](/README.md) [![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](/LICENSE) [![release](https://img.shields.io/github/release/gooosie/ScanCode.svg?style=flat-square)](https://github.com/gooosie/ScanCode/releases)

## Introduction

- A simple scanning tool.
- Support flashlight..
- You can select an image from the gallery to analyze it.
- You can copy barcodes.
- You can save barcode image.
- Currently supported formats：UPC A, UPC E, EAN 8, EAN 13, Code 39, Code 93, Code 128, ITF, QR Code.
- Allow other app calls.

## Usage

CaptureActivty has registered three actions：

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

And when the scan is successful：

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

When the scan fails：

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

Among them：

```java
CodeUtil.RESULT_TYPE       // "RESULT_TYPE"
CodeUtil.RESULT_SUCCESS    // 1
CodeUtil.RESULT_FAILED     // 2
Intents.Scan.RESULT        // "SCAN_RESULT"
Intents.Scan.RESULT_FORMAT // "SCAN_RESULT_FORMAT"
```

Usage examples：

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

## Screenshots

![Screenshot1](/docs/screenshot_1.png)

![Screenshot2](/docs/Screenshot_2.png)

![Screenshot3](/docs/screenshot_3.png)

![Screenshot4](/docs/screenshot_4.png)

## Third party

- [ZXing](https://github.com/zxing/zxing)

## Release

[Release](https://github.com/gooosie/ScanCode/releases)

## License

[Apache License 2.0](/LICENSE)