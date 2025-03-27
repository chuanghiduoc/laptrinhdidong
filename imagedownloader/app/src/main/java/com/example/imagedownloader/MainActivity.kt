package com.example.imagedownloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrl: EditText
    private lateinit var buttonDownload: Button
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo các thành phần giao diện
        editTextUrl = findViewById(R.id.editTextUrl)
        buttonDownload = findViewById(R.id.buttonDownload)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)

        // Thêm URL mẫu để học sinh dễ thử nghiệm
        editTextUrl.setText("https://picsum.photos/800/600")

        // Xử lý sự kiện khi nhấn nút tải
        buttonDownload.setOnClickListener {
            val url = editTextUrl.text.toString()
            if (url.isNotEmpty()) {
                ImageDownloader().execute(url)
            } else {
                Toast.makeText(this, "Vui lòng nhập URL ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // AsyncTask để tải ảnh trong background
    private inner class ImageDownloader : AsyncTask<String, Void, Bitmap?>() {

        override fun onPreExecute() {
            // Hiển thị progress bar trước khi tải
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageUrl = urls[0]
            var bitmap: Bitmap? = null

            try {
                // Tải ảnh từ URL
                val inputStream: InputStream = URL(imageUrl).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            // Ẩn progress bar sau khi tải xong
            progressBar.visibility = View.GONE

            if (result != null) {
                // Hiển thị ảnh đã tải
                imageView.setImageBitmap(result)
                Toast.makeText(this@MainActivity, "Tải ảnh thành công!", Toast.LENGTH_SHORT).show()
            } else {
                // Thông báo lỗi nếu tải thất bại
                Toast.makeText(this@MainActivity, "Không thể tải ảnh. Kiểm tra URL và kết nối internet!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
