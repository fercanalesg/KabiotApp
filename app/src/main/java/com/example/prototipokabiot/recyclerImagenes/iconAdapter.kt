package com.example.prototipokabiot.recyclerImagenes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prototipokabiot.Producto
import com.example.prototipokabiot.ProductsAdapter
import com.example.prototipokabiot.R
import com.example.prototipokabiot.laLista
import kotlinx.android.synthetic.main.select_image_layout.view.*

class iconAdapter(var mContext : Context, var listaProductos : ArrayList<Icon>): RecyclerView.Adapter<iconAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): iconAdapter.ViewHolder {
        val enlazandoView = LayoutInflater.from(mContext)
        return ViewHolder(enlazandoView.inflate(R.layout.select_image_layout, parent, false))
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        val elemento = listaProductos.get(position)

        holder.theIcon.setImageResource(elemento.imagen)


        holder.theIcon.setOnClickListener{

            holder.theCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGray))
            Toast.makeText(mContext, "Presionaste: " + elemento.imagen, Toast.LENGTH_SHORT).show()
            (mContext as laLista).chooseImage = true  // Seteamos nuestra variable global a true, para dar a entender que el usuario SI seleccionó una imagen de las opciones
            (mContext as laLista).image = elemento.imagen
        }
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista){
        val theIcon = vista.iconImage
        val theCardView = vista.myCardView
    }

}