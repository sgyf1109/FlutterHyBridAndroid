package com.sgyf.flutterhybridandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StringCodec


class FlutterPageActivity : AppCompatActivity() {
    // 定义Channel名称
    private val CHANNEL_NATIVE = "com.example.flutter/native"
    private val CHANNEL_FLUTTER = "com.example.flutter/flutter"
    private lateinit var flutterEngine:FlutterEngine
    private lateinit var mBasicMessageChannel:BasicMessageChannel<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flutter_second)

//        方式一、FlutterFragment.createDefault(),createDefault()方法创建出的Fragment显示的路由名称为"/"，如果我们需要指定其他路由名称就不能使用这个方法了。
    /*    val flutterFragment = FlutterFragment.createDefault()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, flutterFragment)
            .commit()*/

//        方式二、FlutterFragment.withNewEngine(),这会导致屏幕呈现短暂的空白
/*
        val flutterFragment = FlutterFragment.withNewEngine()
            .initialRoute("route1")
            .build<FlutterFragment>()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, flutterFragment)
            .commit()
*/

//        方式三、FlutterFragment.withCachedEngine,用缓存的方式
      /*  val flutterEngine = FlutterEngine(this)
        flutterEngine.navigationChannel.setInitialRoute("route1")
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put("my_engine_id", flutterEngine)*/
        val flutterFragment = FlutterFragment.withCachedEngine("my_engine_id")
            .build<FlutterFragment>()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, flutterFragment)
            .commit()
        flutterEngine= FlutterEngineCache.getInstance().get("my_engine_id")as FlutterEngine //首页提前初始化
        createMethodChannel()
        createBaseMessageChannel()
        createEventChannel()
    }


    //flutter页面调用nativeChannel.invokeMethod('jumpToNative', result);
    private fun createMethodChannel() {
        val nativeChannel = MethodChannel(flutterEngine.dartExecutor, CHANNEL_NATIVE)
        nativeChannel.setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                "jumpToNative" -> {
                    // 跳转原生页面
                    val jumpToNativeIntent = Intent(
                        this@FlutterPageActivity,
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

    private fun createBaseMessageChannel() {
        mBasicMessageChannel = BasicMessageChannel(
            flutterEngine.dartExecutor,
            "basic_channel",
            StringCodec.INSTANCE
        )
        //适合flutter主动发消息然后回馈
        mBasicMessageChannel.setMessageHandler(object : BasicMessageChannel.MessageHandler<String?> {
            override fun onMessage(message: String?, reply: BasicMessageChannel.Reply<String?>) {
                Log.d("向flutter发送消息===========", "接收到来自flutter的消息:"+message.toString());
                reply.reply("回馈消息");
            }
        })
    }

    //适合监听类操作，比如电池电量变化等
    private fun createEventChannel() {
        val eventChannel = EventChannel(flutterEngine.dartExecutor, "event_channel")
        eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(o: Any, eventSink: EventSink) {
                Log.d("向flutter发送消息*******", "111111111111");

                eventSink.success("成功")
                //eventSink.error("失败","失败","失败");
            }

            override fun onCancel(o: Any) {
                //取消监听时调用
            }

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//处理下一个页面的回调
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 0 && resultCode === Activity.RESULT_OK) {
            data?.let {
                val message: String = data.getStringExtra("message")
                val result: MutableMap<String, Any> = HashMap()
                result["message"] = message
                // 创建MethodChannel
                val flutterChannel =
                    MethodChannel(flutterEngine.getDartExecutor(), CHANNEL_FLUTTER)
                flutterChannel.invokeMethod("onActivityResult", result)
            }
        }
    }

    override fun onBackPressed() {//解决回退栈直接返回activity问题
        val flutterChannel =
            MethodChannel(flutterEngine.dartExecutor, CHANNEL_FLUTTER)
        flutterChannel.invokeMethod("goBack", null)
    }
}