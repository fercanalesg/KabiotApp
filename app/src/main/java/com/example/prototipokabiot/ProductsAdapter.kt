package com.example.prototipokabiot

/////// Clase especifica para configurar nuestro Recycler View, encargado de mostrar los datos guardados ////

/* El RecyclerView se divide en 4 partes :
    - El RecyclerView en sí, el papá de todos los elementos, es como la libreta que adquiriste, tu vas a decidir cuantas lineas va a tener, te ayudará a organizar
    - ViewHolder, cada casilla individual de la lista esta definido por un ViewHolder, es como reservarte el espacio en la lista, no tiene datos, simplemente "Holdea", es como "Simon, yo te aguanto tu espacio"
    - Adapter, las vistas dentro de las casillas (encapsuladas dentro del ViewHolder) van a ser vinculadas con este, cada casilla tendrá su info individual, por eso el Adapter se "adapta" para darle lo que le toca a cada casilla
    - Layout Manager, es el que pone orden, el portero que checa cuantas casillas hay y puede identificar la posicion de cada una, el mas comun es LinearLayoutManager
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
//import kotlinx.android.synthetic.main.lista_productos_layout.view.*
import kotlinx.android.synthetic.main.recycler_custom_row.view.*

class ProductsAdapter (var mContext : Context, var listaProductos : ArrayList<Producto>): RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {

        val enlazandoView = LayoutInflater.from(mContext) // Creamos el inflador que nos va permitir enlazar el formato de cada casilla con el ViewHolder que le reservará el espacio
        return ViewHolder(enlazandoView.inflate(R.layout.recycler_custom_row, parent,false )) // Nos retorna el ViewHolder ya reservando el espacio a esas view. enlazandoView usa su metodo inflate.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val elemento = listaProductos.get(position) // items es la lista de elementos que le pasamos como parametro a nuestra clase, guardamos en "elemento" el valor que este dentro del indice "position"

        val PortionSize = elemento.cantidadProducto.toInt() / elemento.porcionProducto.toInt()

        //holder.theId.text = elemento.identificador.toString()
        holder.theName.text = elemento.nombreProducto // Como holder es el ViewHolder y ViewHolder ya está ligado a nuestra vista de xml, necesitamos llamar a holder primero y de ahí entrar al elemento que deseamos.
        holder.theGrams.text = elemento.cantidadProducto
        holder.thePortions.text = PortionSize.toString()
        holder.theImage.setImageResource(elemento.imagenProducto)

        holder.theEditBut.setOnClickListener(){                  // Declaramos la funcionalidad que va tener el boton de "Modificar" en cada elemento de la lista
            if(mContext is laLista){                             // Utilizamos como base el contexto de la Activity "laLista"
                (mContext as laLista).updateProductFirebase(elemento)
            }
        }

        holder.theDeleteBut.setOnClickListener() {                // Declaramos la funcionalidad que va tener el boton de "Delete" en cada elemento de la lista
            if (mContext is laLista) {                            // Utilizamos como base el contexto de la Activity "laLista"
                (mContext as laLista).deleteProductFirebase(elemento)
            }
        }

        holder.theImage.setOnClickListener(){                      //Declaramos la funcionalidad que va tener la imagen en cada elemento de la lista
            if (mContext is laLista) {                            // Utilizamos como base el contexto de la Activity "laLista"
                (mContext as laLista).editIconDialog(elemento, (mContext as laLista).setIconList())
            }
        }
    }

    override fun getItemCount(): Int {                              // Esta funcion viene incluida dentro de RecyclerView.Adapter, nos retorna el numero de elementos que tiene la lista
        return listaProductos.size
    }

    class ViewHolder(vista:View) : RecyclerView.ViewHolder(vista){  // Es una clase anidada(dentro del Adapter), extiende de una clase padre, entonces hereda todas las propiedades, nos sirve para no tener que escribir RecyclerView.ViewHolder() siempre
        // Recibe como pareametro la view que ya estamos inflando en el onCreateViewHolder, inflamos nuestro xml
        //val theId = vista.Tvid      // Inicializamos nuestros elementos graficos que estan dentro de la view (lista_productos_layout)
        val theName = vista.TvProduct
        val theGrams = vista.TvGrams
        val thePortions = vista.tvPortions
        val theImage = vista.IvProduct
        val theEditBut = vista.butEdit
        val theDeleteBut = vista.butDelete

    }

}
