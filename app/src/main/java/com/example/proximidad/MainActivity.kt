package com.example.proximidad

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor ?= null

    private final lateinit var handler: Handler

    private lateinit var txt_prox: TextView
    private lateinit var img: ImageView
    private lateinit var mediaStop: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        img = findViewById(R.id.imgSensor)
        txt_prox=findViewById(R.id.txtSensor)

        mediaStop = MediaPlayer.create(this,R.raw.beep)

        if (sensor==null)
            finish()

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val prox = sensorEvent.values[0]
            txt_prox.text = "Distancia: $prox cm"

            // Verificar si la distancia está en un rango específico
            if (prox < sensor!!.maximumRange) {

                img.setImageResource(R.drawable.stop)

                // Reproducir el sonido solo si no está ya reproduciéndose
                if (!mediaStop.isPlaying) {
                    mediaStop.start()
                }

            } else {

                img.setImageResource(R.drawable.play)

                // Detener el sonido si se está reproduciendo
                if (mediaStop.isPlaying) {
                    mediaStop.pause() // Pausar el audio
                    mediaStop.seekTo(0) // Volver al inicio del audio
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onPause() {
        super.onPause()
        // Cancelar la suscripción al listener del sensor cuando la actividad está en pausa
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // Registrar el listener del sensor de proximidad
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
}