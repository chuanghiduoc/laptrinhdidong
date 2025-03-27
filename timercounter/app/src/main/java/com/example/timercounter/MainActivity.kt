package com.example.timercounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var isRunning = false

    // Runnable để cập nhật thời gian
    private val timeRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                seconds++
                updateTimeDisplay()
                // Lập lịch chạy lại sau 1 giây
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo các thành phần giao diện
        timeTextView = findViewById(R.id.timeTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)

        // Thiết lập sự kiện cho nút Bắt đầu
        startButton.setOnClickListener {
            startTimer()
        }

        // Thiết lập sự kiện cho nút Dừng
        stopButton.setOnClickListener {
            stopTimer()
        }

        // Thiết lập sự kiện cho nút Đặt lại
        resetButton.setOnClickListener {
            resetTimer()
        }

        // Hiển thị thời gian ban đầu
        updateTimeDisplay()
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            // Bắt đầu đếm thời gian
            handler.post(timeRunnable)

            // Cập nhật trạng thái các nút
            startButton.isEnabled = false
            stopButton.isEnabled = true
            resetButton.isEnabled = true
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            isRunning = false
            // Dừng đếm thời gian
            handler.removeCallbacks(timeRunnable)

            // Cập nhật trạng thái các nút
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }

    private fun resetTimer() {
        // Dừng đếm thời gian nếu đang chạy
        stopTimer()

        // Đặt lại giá trị thời gian
        seconds = 0
        updateTimeDisplay()

        // Cập nhật trạng thái các nút
        startButton.isEnabled = true
        stopButton.isEnabled = false
        resetButton.isEnabled = false
    }

    private fun updateTimeDisplay() {
        // Chuyển đổi tổng số giây thành giờ:phút:giây
        val hours = TimeUnit.SECONDS.toHours(seconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong()) % 60
        val secs = seconds % 60

        // Hiển thị thời gian với định dạng HH:MM:SS
        val timeString = String.format("%02d:%02d:%02d", hours, minutes, secs)
        timeTextView.text = timeString
    }

    override fun onDestroy() {
        super.onDestroy()
        // Đảm bảo dừng handler khi activity bị hủy
        handler.removeCallbacks(timeRunnable)
    }
}
