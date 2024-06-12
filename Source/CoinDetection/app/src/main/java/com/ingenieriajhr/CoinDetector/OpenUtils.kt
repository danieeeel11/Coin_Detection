package com.ingenieriajhr.CoinDetector

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class OpenUtils(private val context: Context) {

    private var totalPesos = 0.0

    fun setUtil(bitmap: Bitmap): Bitmap {
        // Convertir Bitmap a Mat
        val imgMat = Mat()
        Utils.bitmapToMat(bitmap, imgMat)

        // Aumentar el brillo de la imagen
        val imgProcesada = Mat()
        imgMat.convertTo(imgProcesada, -1, 0.9, 70.0)

        // Aplicar desenfoque para reducir el ruido
        Imgproc.medianBlur(imgProcesada, imgProcesada, 3)

        // Convertir a escala de grises
        Imgproc.cvtColor(imgProcesada, imgProcesada, Imgproc.COLOR_RGB2GRAY)

        // Aplicar la transformación de Hough para detectar círculos
        val circulos = Mat()
        Imgproc.HoughCircles(
            imgProcesada,
            circulos,
            Imgproc.HOUGH_GRADIENT,
            1.0,
            imgProcesada.rows() / 8.0,
            100.0,
            30.0,
            2,
            50
        )

        // Crear un mapa para los valores de las monedas según su radio
        val valoresMonedas = mapOf(
            35 to 50.0,
            41 to 100.0,
            43 to 200.0,
            45 to 500.0,
            49 to 1000.0
        )

        // Dibujar los círculos detectados y contar el valor total
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(outputBitmap, imgMat)

        // Dibujar circulos
        for (i in 0 until circulos.cols()) {
            val datos = circulos.get(0, i)
            val centro = Point(datos[0], datos[1])
            val radio = datos[2].toInt()
            println(radio)
            // Dibujar el borde del círculo
            Imgproc.circle(imgMat, centro, radio, Scalar(0.0, 255.0, 0.0), 3)
            // Dibujar el centro del círculo
            Imgproc.circle(imgMat, centro, 3, Scalar(0.0, 0.0, 255.0), 3)
        }

        var totalDinero = 0.0
        // Contabilizar total dinero
        for (i in 0 until circulos.cols()) {
            val datos = circulos.get(0, i)
            val radio = datos[2].toInt()
            // Encontrar el valor de la moneda basado en su radio
            val valor = valoresMonedas[radio] ?: valoresMonedas[radio - 1]
            if (valor != null) {
                totalDinero += valor
            }
        }

        // Convertir Mat a Bitmap
        Utils.matToBitmap(imgMat, outputBitmap)
        totalPesos = totalDinero

        return outputBitmap
    }

    fun returnTotal(): Double {
        return totalPesos
    }

}
