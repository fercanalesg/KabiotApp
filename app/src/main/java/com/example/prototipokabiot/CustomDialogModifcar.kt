package com.example.prototipokabiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_modificar_producto.* // Sirve para poder convertir todo los recursos xml de mi layout, a codigo.
import kotlinx.android.synthetic.main.activity_modificar_producto.view.*

class CustomDialogModificar : DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Funcion obligatoria dentro de la clase DialogGragment

        var rootView:View = inflater.inflate(R.layout.activity_modificar_producto, container, false) // Instanciamos nuestro layout de modificar producto para poder utilizar sus views
        var instanciaLista = laLista()

        rootView.cancelButton.setOnClickListener(){
            dismiss()
        }
        return rootView
    }
}