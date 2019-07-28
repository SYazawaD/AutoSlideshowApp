package com.example.autoslideshowapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    private var mTimer: Timer? = null
    private val PERMISSIONS_REQUEST_CODE = 100
    private var mHandler = Handler()
    private val play: String = "再生"
    private val stop :String = "停止"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }else {
                    //権限が許可されていない場合→処理なし
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            Log.d("ANDROIDSHOT", "URI : " + imageUri.toString())
            imageView.setImageURI(imageUri)
        }


        go_button.setOnClickListener {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            try {
                // 進む
                cursor.moveToNext()
                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            } catch (e: Exception) {
                // 進む(最後の画像→最初の画像)
                cursor.moveToFirst()
                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }

        back_button.setOnClickListener {
            try {
                // 戻る
                cursor.moveToPrevious()
                // indexからIDを取得し、そのIDから画像のURIを取得する
                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            } catch (e: Exception) {
                // 戻る(最後の画像→最初の画像)
                cursor.moveToLast()
                // indexからIDを取得し、そのIDから画像のURIを取得する
                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }

        play_stop_button.setOnClickListener() {
            if (mTimer == null) {
             // 再生
                // タイマーの作成
                mTimer = Timer()
                // タイマーの始動
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            try {
                                // 進む
                                cursor.moveToNext()
                                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                var id = cursor.getLong(fieldIndex)
                                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                            } catch (e: Exception) {
                                // 進む(最後の写真→最初の写真)
                                cursor.moveToFirst()
                                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                var id = cursor.getLong(fieldIndex)
                                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで 2,000ミリ秒、ループの間隔を 2,000ミリ秒 に設定
                // 再生/停止ボタン変更
                play_stop_button.text = stop
                // 進むボタン変更
                go_button.isClickable = false
                go_button.setTextColor(Color.parseColor("gray"))
                // 戻るボタン変更
                back_button.isClickable = false
                back_button.setTextColor(Color.parseColor("gray"))
            }else{
             // 停止
                // 処理ストップ&初期化
                mTimer!!.cancel()
                mTimer = null
                // 再生/停止ボタン変更
                play_stop_button.text = play
                // 進むボタン変更
                go_button.isClickable = true
                go_button.setTextColor(Color.parseColor("black"))
                // 戻るボタン変更
                back_button.isClickable = true
                back_button.setTextColor(Color.parseColor("black"))
            }
        }
    }
}