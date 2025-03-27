package com.example.audiorecorder

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 200
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false
    private var recordingFile: File? = null
    private lateinit var btnRecord: Button
    private lateinit var btnStop: Button
    private lateinit var recordingsList: ListView
    private val recordings = mutableListOf<Recording>()
    private lateinit var adapter: ArrayAdapter<Recording>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.btnRecord)
        btnStop = findViewById(R.id.btnStop)
        recordingsList = findViewById(R.id.recordingsList)

        // Thiết lập adapter cho ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recordings)
        recordingsList.adapter = adapter

        // Cập nhật danh sách bản ghi
        loadRecordings()

        // Xử lý sự kiện nút Ghi âm
        btnRecord.setOnClickListener {
            if (checkPermissions()) {
                startRecording()
            } else {
                requestPermissions()
            }
        }

        // Xử lý sự kiện nút Dừng
        btnStop.setOnClickListener {
            if (isRecording) {
                stopRecording()
            }
        }

        // Xử lý sự kiện khi chọn một bản ghi
        recordingsList.setOnItemClickListener { _, _, position, _ ->
            playRecording(recordings[position].filePath)
        }
    }

    private fun checkPermissions(): Boolean {
        // Kiểm tra quyền ghi âm
        val recordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        // Với Android 10 trở lên không cần kiểm tra quyền bộ nhớ ngoài khi lưu vào app-specific storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return recordPermission == PackageManager.PERMISSION_GRANTED
        }

        // Android 9 trở xuống cần kiểm tra cả quyền ghi âm và quyền bộ nhớ
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return recordPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    private fun startRecording() {
        // Tạo thư mục để lưu bản ghi âm
        val recordingsDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "MyRecordings")
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }

        // Tạo tên file với timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        recordingFile = File(recordingsDir, "recording_$timestamp.mp3")

        // Thiết lập MediaRecorder
        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(recordingFile?.absolutePath)
                prepare()
                start()

                isRecording = true
                btnRecord.isEnabled = false
                btnStop.isEnabled = true
                Toast.makeText(this@MainActivity, "Đang ghi âm...", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this@MainActivity, "Lỗi khi ghi âm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder?.apply {
                    stop()
                    reset()
                    release()
                }
                mediaRecorder = null
                isRecording = false
                btnRecord.isEnabled = true
                btnStop.isEnabled = false

                // Lưu thông tin bản ghi vào MediaStore
                saveToMediaStore()

                // Cập nhật danh sách
                loadRecordings()

                Toast.makeText(this, "Đã lưu bản ghi âm", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Lỗi khi dừng ghi âm: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToMediaStore() {
        recordingFile?.let { file ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, file.name)
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/MyRecordings")
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

                uri?.let {
                    values.clear()
                    values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                }
            }
            // Với Android 9 trở xuống, file đã được lưu trực tiếp vào bộ nhớ ngoài
        }
    }

    private fun loadRecordings() {
        recordings.clear()
        val recordingsDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "MyRecordings")

        if (recordingsDir.exists()) {
            recordingsDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".mp3")) {
                    recordings.add(Recording(file.name, file.absolutePath))
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun playRecording(filePath: String) {
        // Dừng phát nếu đang phát
        mediaPlayer?.release()

        // Tạo MediaPlayer mới
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                start()
                Toast.makeText(this@MainActivity, "Đang phát...", Toast.LENGTH_SHORT).show()

                // Xử lý khi phát xong
                setOnCompletionListener {
                    Toast.makeText(this@MainActivity, "Phát xong", Toast.LENGTH_SHORT).show()
                    release()
                }
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Lỗi khi phát: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Đã được cấp quyền", Toast.LENGTH_SHORT).show()
                // Tự động bắt đầu ghi âm khi được cấp quyền
                startRecording()
            } else {
                Toast.makeText(this, "Cần cấp quyền để ghi âm", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Lớp đối tượng để lưu thông tin bản ghi
    data class Recording(val name: String, val filePath: String) {
        override fun toString(): String = name
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}
