package com.example.prototipokabiot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.prototipokabiot.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_modificar_producto.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boton : Button = binding.startButton  // Convertimos en objeto de codigo el boton de XML
        var elTexto : TextView = binding.slogan   // Convertimos en objeto de codigo el texto de XML

        boton.setOnClickListener{ //Funcion para indicar lo que va a suceder cuando des click en el boton
            cambioActivity(elTexto, boton)
        }
    }

    fun cambioActivity(texto : TextView, but: Button){  // Funcion para cambiar a la activity de Lista

        Toast.makeText(this,"¿Que vamos a comer hoy?", Toast.LENGTH_LONG).show()   //Mandar un Toast en cuanto hagas click en el boton de iniciar
        var laLista = Intent(this, laLista::class.java)
        startActivity(laLista)
    }


}

