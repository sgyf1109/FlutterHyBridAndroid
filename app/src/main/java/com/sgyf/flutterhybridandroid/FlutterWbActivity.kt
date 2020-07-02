package com.sgyf.flutterhybridandroid

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class FlutterWbActivity : AppCompatActivity() {
    private lateinit var flutterEngine:FlutterEngine
    private val CHANNEL_NATIVE = "com.example.flutter/native"
    private val CHANNEL_FLUTTER = "com.example.flutter/flutter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flutter_wb)

        val flutterFragment = FlutterFragment.withCachedEngine("my_engine_wb")
            .build<FlutterFragment>()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, flutterFragment)
            .commit()
        flutterEngine= FlutterEngineCache.getInstance().get("my_engine_wb")as FlutterEngine //首页提前初始化
        createMethodChannel()
    }
    //flutter页面调用nativeChannel.invokeMethod('jumpToNative', result);
    private fun createMethodChannel() {
        val nativeChannel = MethodChannel(flutterEngine.dartExecutor, CHANNEL_NATIVE)
        nativeChannel.setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                "jumpToNative" -> {
                    // 跳转原生页面
                    val jumpToNativeIntent = Intent(
                        this@FlutterWbActivity,
                        NativePageActivity::class.java
                    )
                    jumpToNativeIntent.putExtra(
                        "name",
                        methodCall.argument<Any>("name") as String?
                    )
                    startActivityForResult(jumpToNativeIntent,0)
                }
                "goBackWithResult"->{
                    // 返回上一页，携带数据
                    val backIntent = Intent()
                    backIntent.putExtra("message", methodCall.argument("message") as String?)
                    setResult(Activity.RESULT_OK, backIntent)
                    finish();
                }
                "goBack"->{//flutter页面有回退栈的时候返回上一个flutter页面
                    finish();
                }
                else -> result.notImplemented()
            }
        }
    }
    override fun onBackPressed() {//解决回退栈直接返回activity问题
        val flutterChannel =
            MethodChannel(flutterEngine.dartExecutor, CHANNEL_FLUTTER)
        flutterChannel.invokeMethod("goBack", null)
    }
}