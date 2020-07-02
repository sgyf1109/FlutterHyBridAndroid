package com.sgyf.flutterhybridandroid


import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor


class FlutterViewActivity : AppCompatActivity() {
    private lateinit var flutterEngine:FlutterEngine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flutter)

        // 通过FlutterView引入Flutter编写的页面
        val flutterView = FlutterView(this)
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val flContainer =
            findViewById<FrameLayout>(R.id.container)
        flContainer.addView(flutterView, lp)

        flutterEngine= FlutterEngine(this)
        // 设置初始路由
        flutterEngine.getNavigationChannel().setInitialRoute("route1");
        // 开始执行dart代码来pre-warm FlutterEngine
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        flutterView.attachToFlutterEngine(flutterEngine)
    }
    override fun onResume() {
        super.onResume()
        flutterEngine.getLifecycleChannel().appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        flutterEngine.getLifecycleChannel().appIsInactive()
    }

    override fun onStop() {
        super.onStop()
        flutterEngine.getLifecycleChannel().appIsPaused()
    }

}