package com.example.atividade3

import android.content.Context
import android.database.DatabaseUtils
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.atividade3.databinding.ActivityMainBinding
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class MainActivity : AppCompatActivity(),SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var brightness: Sensor? = null
    private var proximidade: Sensor? = null
   lateinit var list:Sensor

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

       binding.buttonLuz.setOnClickListener {

          binding.textViewSensor.text="Luz"
           binding.tvProx.isVisible=false
           binding.tvText.isVisible=true;
           binding.circularProgressBar.isVisible=true
           binding.tvSquare.isVisible= false
           binding.tvGiro.isVisible=false
           setUpSensor(1)
       }

        binding.buttonAcelerometro.setOnClickListener {
            binding.textViewSensor.text="Acelerometro"
            setUpSensor(2)
            binding.tvProx.isVisible=false
            binding.tvSquare.isVisible= true
            binding.circularProgressBar.isVisible=false
            binding.tvText.isVisible=false
            binding.tvGiro.isVisible=false
        }

        binding.buttonproximidade.setOnClickListener {
            binding.textViewSensor.text="Proximidade"
            binding.tvProx.isVisible=true
            binding.circularProgressBar.isVisible=false
            binding.tvSquare.isVisible= false
            binding.tvText.isVisible=false
            binding.tvGiro.isVisible=false
            setUpSensor(3)
        }

        binding.buttonGiro.setOnClickListener {
            binding.textViewSensor.text="Giroscopio"

            binding.circularProgressBar.isVisible=false
            binding.tvSquare.isVisible= false
            binding.tvProx.isVisible=false
            binding.tvText.isVisible=false
            binding.tvGiro.isVisible=true
            setUpSensor(4)
        }

        setUpSensor(1)

    }

    private  fun setUpSensor(event: Int){

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        when(event){
            1 -> {
                brightness=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            }
            2 ->{
                brightness =sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).also {
                    sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_FASTEST,SensorManager.SENSOR_DELAY_FASTEST)
                }
            }
            3 ->{

                brightness =sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY).also {
                    sensorManager.registerListener(
                        this,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }
            4 ->{
                 list =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE).also {
                     sensorManager.registerListener(
                         this,
                         it,
                         SensorManager.SENSOR_DELAY_FASTEST,
                         SensorManager.SENSOR_DELAY_FASTEST
                     )
                 }
            }
        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onSensorChanged(event: SensorEvent?) {
      when(event?.sensor?.type){

          Sensor.TYPE_LIGHT ->{
              val light = event.values[0]
              binding.tvText.text="Sensor: $light\n${brightness(light)}"
              binding.circularProgressBar.setProgressWithAnimation(light)
          }
          Sensor.TYPE_ACCELEROMETER ->{

              val sides= event.values[0]
              val upDown=event.values[1]

              binding.tvSquare.apply {
                  rotationX = upDown* 3f
                  rotationY =sides * 3f
                  rotation = - sides
                  translationX=sides *  -10
                  translationY=upDown * 10
              }

              val color = if (upDown.toInt() == 0 && sides.toInt() ==0 ) Color.GREEN else Color.RED
              binding.tvSquare.setBackgroundColor(color)

              binding.tvSquare.text="up/down ${(upDown.toInt())}\n left/rigth ${(sides.toInt())}"


          }
          Sensor.TYPE_PROXIMITY ->{
              val params = this@MainActivity.window.attributes
           //   binding.tvProx.text = event.values[0].toString()
              if (event.values[0] == 0f) {

                  params.flags = params.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                  params.screenBrightness = 0f
                  window.attributes = params
                  binding.tvProx.text = "Too Near"
                  binding.tvProx.setTextColor(resources.getColor(android.R.color.black))
              } else {
                  params.flags = params.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                  params.screenBrightness = -1f
                  window.attributes = params
                  binding.tvProx.text = "Far Away"
                  binding.tvProx.setTextColor(resources.getColor(android.R.color.holo_red_dark))
              }


          }
          Sensor.TYPE_GYROSCOPE ->{
              var s = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)

              val x= event.values[0]
              val y= event.values[1]
              val z= event.values[2]

              binding.tvGiro.text="X: ${(x.toInt())} y: ${(y.toInt())} z: ${(z.toInt())}"
          }

      }

    }

    private fun brightness(brightness: Float): String {

        return when (brightness.toInt()) {
            0 -> "Pitch black"
            in 1..10 -> "Dark"
            in 11..50 -> "Grey"
            in 51..5000 -> "Normal"
            in 5001..25000 -> "Incredibly bright"
            else -> "This light will blind you"
        }
    }
    override fun onResume() {
        super.onResume()
        // Register a listener for the sensor.
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

}


