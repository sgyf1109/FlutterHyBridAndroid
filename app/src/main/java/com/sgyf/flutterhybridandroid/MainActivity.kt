package com.sgyf.flutterhybridandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFlutterEngine()
        initFlutterWbEngine()
        initView()
    }



    private fun initFlutterEngine() {
//                方式三、FlutterFragment.withCachedEngine,用缓存的方式
        val flutterEngine = FlutterEngine(this)
        flutterEngine.navigationChannel.setInitialRoute("route1?{\"name\":\"传递的参数\"}")//直接就传递过去了
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put("my_engine_id", flutterEngine)
    }

    private fun initFlutterWbEngine() {
        val flutterEngine = FlutterEngine(this)
        flutterEngine.navigationChannel.setInitialRoute("route2?{\"name\":\"传递的参数\"}")//直接就传递过去了
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put("my_engine_wb", flutterEngine)

    }

    private fun initView() {
        navigationFlutterOne.setOnClickListener {
            startActivity(Intent(this@MainActivity,FlutterViewActivity::class.java))
        }
        navigationFlutterTwo.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity,FlutterPageActivity::class.java),1)
        }
        navigationFlutterThird.setOnClickListener {
            // 方式一、FutterActivity显示的路由名称为"/"，不可设置
           /* startActivity(
                io.flutter.embedding.android.FlutterActivity.createDefaultIntent(this)
            );*/

// 方式二、FutterActivity显示的路由名称可设置，每次都创建一个新的FlutterEngine对象
        /*    startActivity(
                io.flutter.embedding.android.FlutterActivity
                    .withNewEngine()
                    .initialRoute("route1")
                    .build(this)
            );*/

// 方式三、FutterActivity显示的路由名称可设置，使用缓存好的FlutterEngine对象
            startActivity(
                io.flutter.embedding.android.FlutterActivity
                    .withCachedEngine("my_engine_id")
                    .build(this)
            );
        }

        navigationWb.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity,FlutterWbActivity::class.java),1)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode){
            Activity.RESULT_OK->{
                data?.let {
                    Toast.makeText(
                        this,
                        data.getStringExtra("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}