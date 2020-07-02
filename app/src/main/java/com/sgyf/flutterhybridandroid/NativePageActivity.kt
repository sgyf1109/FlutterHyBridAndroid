package com.sgyf.flutterhybridandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec
import kotlinx.android.synthetic.main.activity_native_page.*


class NativePageActivity : AppCompatActivity() {
    private lateinit var flutterEngine: FlutterEngine
    private lateinit var mBasicMessageChannel:BasicMessageChannel<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_page)
        flutterEngine =
            FlutterEngineCache.getInstance().get("my_engine_id") as FlutterEngine //首页提前初始化
        createBaseMessageChannel()


        tv.text = "Flutter页面传过来的参数：name=" + getIntent().getStringExtra("name")

        btnBack.setOnClickListener {
            // 上一个页面是FlutterPageActivity

            // 上一个页面是FlutterPageActivity
            val intent = Intent()
            intent.putExtra("message", "我从原生页面回来了")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        basicMessageChannel.setOnClickListener {
//        //发送消息
            mBasicMessageChannel.send("向flutter发送消息")//onMessage不会回调，onMessage只能接收到send发来的消息，适合native主动发消息不接收回馈
        }

        basicMessageChannel2.setOnClickListener {
            //发送消息并接受flutter的回馈，接受flutter return的数据，适合native主动发消息然后回馈
            mBasicMessageChannel.send("向flutter发送消息",
                object :BasicMessageChannel.Reply<String>{
                    override fun reply(reply: String?) {
                        Log.d("向flutter发送消息----------", "接收到来自flutter的消息:"+reply.toString());
                    }
                }
            )
        }
    }

    private fun createBaseMessageChannel() {
        mBasicMessageChannel = BasicMessageChannel(
            flutterEngine.dartExecutor,
            "basic_channel",
            StringCodec.INSTANCE
        )
    }
}
