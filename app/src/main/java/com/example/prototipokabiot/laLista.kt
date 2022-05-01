package com.example.prototipokabiot

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prototipokabiot.recyclerImagenes.Icon
import com.example.prototipokabiot.recyclerImagenes.iconAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_la_lista.* //Sirve para importar todos los views del layout aqui al codigo
import kotlinx.android.synthetic.main.activity_modificar_producto.*
import kotlinx.android.synthetic.main.activity_modificar_producto.cancelButton
import kotlinx.android.synthetic.main.agregar_producto_layout.*
import kotlinx.android.synthetic.main.lista_productos_layout.*
import kotlinx.android.synthetic.main.lista_productos_layout.view.*
import kotlinx.android.synthetic.main.recycler_icon_edit.*
import kotlinx.android.synthetic.main.select_image_layout.*

class laLista : AppCompatActivity() {
    var chooseImage = false
    var image : Int = 2
    var deadlineOfProduct = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_la_lista)
        readDataFirebase()
    }

    fun readDataFirebase(){
        val list = ArrayList<Producto>()
        val database = FirebaseDatabase.getInstance().getReference("Productos")    // Creamos una referencia a la base de datos, en este caso va estar apuntando a lo que esté dentro del Path "Productos"

        var nombre : String
        var cantidad : String
        var porcion : String
        var imagen : Int

        val messageListener : ValueEventListener   // Necesitamos crear una variable de tipo ValueEventListener, es como nuestro agente encubierto que estará recorriendo la Real Time Base

        messageListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()   // Limpiamos la lista por un segundo para que sobreescriba la nueva lista que acaba de leer y no la concatene
                for (doc in snapshot.children) {  // Snapshot apunta al path que indicamos en la variable "database", y su children entonces son todas las sublistas que hay dentro de el
                    nombre = doc.child("nombreProducto").getValue().toString()
                    cantidad = doc.child("cantidadProducto").getValue().toString()
                    porcion = doc.child("porcionProducto").getValue().toString()
                    imagen = doc.child("imagenProducto").getValue().toString().toInt()

                    val Producto: Producto = Producto(nombre, cantidad, porcion, imagen)  // Creamos un objeto de tipo productos con los datos recabados
                    list.add(Producto)
                }

                if(list.size > 0){   // Sirve para validar que si hay algo para mostrar
                    myRecylerProductView.visibility = View.VISIBLE   // Si hay algo para mostrar de la base de datos, hace visible la RecyclerView, y esconde el mensaje de "No Records"
                    TvNoRecordsAvailable.visibility = View.GONE      //
                    myRecylerProductView.layoutManager = LinearLayoutManager(this@laLista) // Le estas diciendo que la forma en la que quieres que ordene tu vista myRecyclerView(de tipo RW), es justamente con RecyclerView, por eso ponermos this

                    val itemAdapter = ProductsAdapter(this@laLista, list) // Estamos instanciando la clase del Adaptador, le pasamos como parametro la lista que generamos con el for de arriba, con los elementos obtenidos de la DB

                    myRecylerProductView.adapter = itemAdapter // Le dices a tu vista de RV que se tiene que adaptar a todo lo que hayamos definido en el Adapter
                }
                else{
                    myRecylerProductView.visibility = View.GONE     //  Escondemos el recyclerView porque no hay nada para mostrar
                    TvNoRecordsAvailable.visibility = View.VISIBLE  // Si no hay nada para mostrar de la base de datos, hace visible el mensaje de "No Records", y esconde la RecyclerView
                }

                 if(list.size >= 6){                // Estas lineas sirven para limitar al usuario a solo agregar 6 Productos
                    deadlineOfProduct = false
                }
                else{
                    deadlineOfProduct = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "message:onCancelled: ${error.message}")
            }
        }
        database.addValueEventListener(messageListener) // Como database es una referencia a DB, usamos su metodo de addValueEventListener y le pasamos como parametro nuestro agente encubierto que nos va ayudar.
    }

    fun addProductFirebase(){
        val db = FirebaseDatabase.getInstance().reference   // Creamos una referencia a la base de datos, en este caso apunta directamente a la dirección madre (la de hasta arriba)

        val addProductDialog = Dialog(this)  // Le estamos avisando que su trabajo va a ser mandar un dialogo, todavia no sabe cual, pero para que esté trucha
        addProductDialog.setCancelable(false)

        addProductDialog.setContentView(R.layout.agregar_producto_layout)  // Le estamos diciendo "wey, quiero que me traigas para acá la view de Agregar producto, voy a trabajar sobre esa"

        addProductDialog.addButton.setOnClickListener() {
            val name = addProductDialog.etAddName.text.toString()     // Guardamos en una variable lo que el usuario introduzca en el editText de Nombre
            val quantity = "0"
            val portion = addProductDialog.etAddPortion.text.toString()  // Guardamos en una variable lo que el usuario introduzca en el editText de Porcion
            val image = R.drawable.arroz

            if(!name.isEmpty() && portion.toString().isNotEmpty()){    // Debemos asegurarnos que ninguno de los campos este vacio

                val id = db.push().key.toString()       // Esta linea es para decirle que queremos que la DB nos genere la id automaticamente
                var producto = Producto(name,quantity,portion,image)
                db.child("Productos").child(id).setValue(producto)     // Queremos que nos ponga los datos apuntando a Productos, y la sublista se va crear con el titulo del id

                addProductDialog.etAddName.text.clear()   // Vaciamos los dos EditText
                addProductDialog.etAddPortion.text.clear()
                readDataFirebase()
                addProductDialog.dismiss()
            }
            else{
                Toast.makeText(applicationContext,"Nombre de producto y cantidad no pueden estar en blanco", Toast.LENGTH_LONG).show()
            }

        }
        addProductDialog.cancelButton.setOnClickListener(){
            addProductDialog.dismiss()
        }
        addProductDialog.show() // El show se pone al final
    }

    fun updateProductFirebase(prodModelClass: Producto){

        val rootRef = FirebaseDatabase.getInstance().reference
        val nombre = prodModelClass.nombreProducto
        val grams = prodModelClass.cantidadProducto

        val updateDialog = Dialog(this, R.style.Theme_Dialog)  // Instanciamos un objeto de la clase Dialog, un API que ayuda a crear pop up windows
        //val updateDialog = Dialog(this)  // Instanciamos un objeto de la clase Dialog, un API que ayuda a crear pop up windows
        updateDialog.setCancelable(false)

        updateDialog.setContentView(R.layout.activity_modificar_producto)  // Estamos "inflando" nuestra xml de modificar el producto

        updateDialog.etUpdateName.setText(prodModelClass.nombreProducto)  // Al campo de rellenar nombre lo llenamos con el nombre actual del producto, es el que se va modificar
        updateDialog.etUpdatePortion.setText(prodModelClass.porcionProducto)
        updateDialog.editImage.setImageResource(prodModelClass.imagenProducto)

        updateDialog.modifyButton.setOnClickListener(){
            val name = updateDialog.etUpdateName.text.toString()
            val portion : String = updateDialog.etUpdatePortion.text.toString()
            val producto = Producto(name, grams, portion,R.drawable.arroz)

            if(!name.isEmpty() && portion.toString().isNotEmpty()) {

                val Query = rootRef.child("Productos").orderByChild("nombreProducto").equalTo(nombre)  // Hacemos el Query apuntando exactamente a la fila que tenga el nombre del producto al que hicimos click
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val id = ds.key.toString()
                            rootRef.child("Productos").child(id).setValue(producto)

                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("TAG", databaseError.getMessage()) //Don't ignore errors!
                    }
                }
                Query.addListenerForSingleValueEvent(valueEventListener)

                readDataFirebase()
                updateDialog.dismiss()

            }
            else{
                Toast.makeText(applicationContext, "Nombre y gramos no pueden estar vacios", Toast.LENGTH_SHORT).show()
            }
        }


        updateDialog.cancelButton.setOnClickListener(){
            updateDialog.dismiss()
        }
        updateDialog.show() // El show se pone al final

    }


    fun editIconDialog(prodModelClass : Producto, lista : ArrayList<Icon>){

        val rootRef = FirebaseDatabase.getInstance().reference
        val nombre = prodModelClass.nombreProducto

        val updateIconDialog = Dialog(this, R.style.Theme_Dialog)  // Instanciamos un objeto de la clase Dialog, un API que ayuda a crear pop up windows
        updateIconDialog.setCancelable(false)
        updateIconDialog.setContentView(R.layout.recycler_icon_edit)  // Estamos "inflando" nuestra xml de modificar el producto

        var instanceGridManager = GridLayoutManager(this, 2)

        updateIconDialog.myRecylerIconEdit.layoutManager = instanceGridManager
        var myIconAdapter = iconAdapter(this,lista)

        updateIconDialog.myRecylerIconEdit.adapter = myIconAdapter
        updateIconDialog.myRecylerIconEdit.setHasFixedSize(true)

        updateIconDialog.Aceptar.setOnClickListener{
            if(chooseImage == true){
                val producto = Producto(nombre, prodModelClass.cantidadProducto,prodModelClass.porcionProducto, image)
                val Query = rootRef.child("Productos").orderByChild("nombreProducto").equalTo(nombre)  // Hacemos el Query apuntando exactamente a la fila que tenga el nombre del producto al que hicimos click
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val id = ds.key.toString()
                            rootRef.child("Productos").child(id).setValue(producto)

                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("TAG", databaseError.getMessage()) //Don't ignore errors!
                    }
                }
                Query.addListenerForSingleValueEvent(valueEventListener)
                readDataFirebase()
                updateIconDialog.dismiss()
            }
            else{
                Toast.makeText(this,"Debes elegir un Icono para continuar", Toast.LENGTH_SHORT).show()
            }
        }
        updateIconDialog.Cancelar.setOnClickListener { updateIconDialog.dismiss() }
        chooseImage = false
        updateIconDialog.show()
    }

    fun setRecyclerOnBlank(){

    }
    fun deleteProductFirebase(prodModelClass: Producto){

        val rootRef = FirebaseDatabase.getInstance().reference
        val db = FirebaseDatabase.getInstance().reference
        val builder = AlertDialog.Builder(this)

        val nombre = prodModelClass.nombreProducto   // en una variable guardamos uno de los datos de la fila que queremos borrar (a la que le dimos click), en este caso el nombre
        var elId : String


        builder.setTitle("Eliminar producto")
        builder.setMessage("Seguro que deseas eliminar ${prodModelClass.nombreProducto}?")
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Si"){ dialogInterface, which ->
            val Query = rootRef.child("Productos").orderByChild("nombreProducto").equalTo(nombre)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val id = ds.key.toString()
                        rootRef.child("Productos").child(id).removeValue()

                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", databaseError.getMessage()) //Don't ignore errors!
                }
            }
            Query.addListenerForSingleValueEvent(valueEventListener)
            readDataFirebase()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val dialogoDeAlerta : AlertDialog = builder.create()  // Sirve para crear el dialogo de alerta, se pone al final
        dialogoDeAlerta.setCancelable(false)
        dialogoDeAlerta.show()
    }

    fun setIconList() : ArrayList<Icon>{
        var lista : ArrayList<Icon> = ArrayList()
        lista.add(Icon(R.drawable.almendra))
        lista.add(Icon(R.drawable.arroz))
        lista.add(Icon(R.drawable.avena))
        lista.add(Icon(R.drawable.cacahuate))
        lista.add(Icon(R.drawable.cereal))
        lista.add(Icon(R.drawable.frijol))
        lista.add(Icon(R.drawable.galleta))
        lista.add(Icon(R.drawable.spaghetti1))
        lista.add(Icon(R.drawable.spaghetti2))
        lista.add(Icon(R.drawable.spaghetti3))
        return lista
    }







    ////////////////////////////////////////////////////////////////////////////////////////  FIRESTORE  ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////  FIRESTORE  ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*fun retrieveDataFirestore(it: QuerySnapshot, list : ArrayList<Producto>){ // Esta funcion ira dentro de la de read, simplemente es para que quede mas ordenado
        var nombre : String
        var cantidad : String
        var porcion : String
        var imagen : Int
        var producto : Producto

        if(it.isEmpty){
            Toast.makeText(this, "No Task Found", Toast.LENGTH_SHORT).show()
        }
        for(doc in it){
            nombre = doc["Producto"].toString()  // doc se comporta como un mapa entonces le decimos que queremos recuperar el valor de  su clave "Producto"
            cantidad = doc["Cantidad"].toString()
            porcion = doc ["Porcion"].toString()
            imagen = doc["Imagen"].toString().toInt()
            producto = Producto(nombre,cantidad,porcion,imagen) // rellenamos el objeto de tipo Producto

            list.add(producto) // Agregamos ese objeto a la lista
        }
        myRecylerProductView.layoutManager = LinearLayoutManager(this) // Le estas diciendo que la forma en la que quieres que ordene tu vista myRecyclerView(de tipo RW), es justamente con RecyclerView, por eso ponermos this

        val itemAdapter = ProductsAdapter(this, list) // Estamos instanciando la clase del Adaptador, le pasamos como parametro la lista que generamos con el for de arriba, con los elementos obtenidos de la DB

        myRecylerProductView.adapter = itemAdapter // Le dices a tu vista de RV que se tiene que adaptar a todo lo que hayamos definido en el Adapter

        if (list.size == 6){            // Si la lectura detecta que la lista ya alcanzó 6 elementos, setea nuestra variable deadline a false
            deadlineOfProduct = false
        }
        else{
            deadlineOfProduct = true
        }
    }

    fun readFirestoreData(){
        val db = FirebaseFirestore.getInstance()

        val list = ArrayList<Producto>()
        val connection = db.collection("Kabiot")

        connection.get()
            .addOnSuccessListener {
                retrieveDataFirestore(it, list)
            }
    }*/


    /*fun addNewProductFirestore(){
        val db = FirebaseFirestore.getInstance()    // Creamos una variable que guardará la conexión con la base de datos
        val productsMap : MutableMap<String, Any> = hashMapOf()  // Creamos un diccionario, que tiene como claves los titulos de las columnas, y como valores los datos de la base de datos
        val addProductDialog = Dialog(this)  // Le estamos avisando que su trabajo va a ser mandar un dialogo, todavia no sabe cual, pero para que esté trucha
        addProductDialog.setCancelable(false)

        addProductDialog.setContentView(R.layout.agregar_producto_layout)  // Le estamos diciendo "wey, quiero que me traigas para acá la view de Agregar producto, voy a trabajar sobre esa"

        addProductDialog.addButton.setOnClickListener() {
            val name = addProductDialog.etAddName.text.toString()     // Guardamos en una variable lo que el usuario introduzca en el editText de Nombre
            val portion = addProductDialog.etAddPortion.text.toString()  // Guardamos en una variable lo que el usuario introduzca en el editText de Grams

            if(!name.isEmpty() && portion.toString().isNotEmpty()){    // Debemos asegurarnos que ninguno de los campos este vacio

                productsMap["Producto"] = name        // Rellenamos el diccionario, lo que esta dentro del corchete es la clave (debe tener el mismo nombre que la columna de la DB) y lo del lado derecho del igual es el valor
                productsMap["Cantidad"] = "0"
                productsMap["Porcion"] = portion
                productsMap["Imagen"] = R.drawable.arroz

                db.collection("Kabiot")
                    .add(productsMap)                       // Le estamos pasando los datos que estan dentro de nuestro Diccionario
                    .addOnSuccessListener {
                        Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "El producto no pudo ser añadido", Toast.LENGTH_SHORT).show()
                    }

                addProductDialog.etAddName.text.clear()   // Vaciamos los dos EditText
                addProductDialog.etAddPortion.text.clear()

                readFirestoreData()
                addProductDialog.dismiss()
            }
            else{
                Toast.makeText(applicationContext,"Nombre de producto y cantidad no pueden estar en blanco", Toast.LENGTH_LONG).show()
            }

        }
        addProductDialog.cancelButton.setOnClickListener(){
            addProductDialog.dismiss()
        }
        addProductDialog.show() // El show se pone al final
    }*/



    /*fun deleteProductAlertDialogFirestore(prodModelClass: Producto){
        val db = FirebaseFirestore.getInstance()
        val builder = AlertDialog.Builder(this)

        val nombre = prodModelClass.nombreProducto   // en una variable guardamos uno de los datos de la fila que queremos borrar (a la que le dimos click), en este caso el nombre
        var elId : String


        builder.setTitle("Eliminar producto")
        builder.setMessage("Seguro que deseas eliminar ${prodModelClass.nombreProducto}?")
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Si"){ dialogInterface, which ->

            db.collection("Kabiot").whereEqualTo("Producto", nombre).get()  //Hacemos un query, queremos obtener de la coleccion "Kabiot" la fila en donde Producto = nombre
                .addOnSuccessListener {
                    for(doc in it){
                        elId = doc.id    // Obtenemos el id unico de esa fila
                        db.collection("Kabiot").document(elId).delete() // Le decimos que queremos eliminar la fila con el ID unico que obtuvimos en la linea anterior
                    }
                    readFirestoreData()
                }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val dialogoDeAlerta : AlertDialog = builder.create()  // Sirve para crear el dialogo de alerta, se pone al final
        dialogoDeAlerta.setCancelable(false)
        dialogoDeAlerta.show()

    }*/



    /*fun UpdateProductDialogFirestore(prodModelClass: Producto){
        val db = FirebaseFirestore.getInstance()
        var elId : String
        val nombre = prodModelClass.nombreProducto
        val grams = prodModelClass.cantidadProducto


        val updateDialog = Dialog(this, R.style.Theme_Dialog)  // Instanciamos un objeto de la clase Dialog, un API que ayuda a crear pop up windows
        updateDialog.setCancelable(false)

        updateDialog.setContentView(R.layout.activity_modificar_producto)  // Estamos "inflando" nuestra xml de modificar el producto

        updateDialog.etUpdateName.setText(prodModelClass.nombreProducto)  // Al campo de rellenar nombre lo llenamos con el nombre actual del producto, es el que se va modificar
        updateDialog.etUpdatePortion.setText(prodModelClass.porcionProducto)


        updateDialog.modifyButton.setOnClickListener(){
            val name = updateDialog.etUpdateName.text.toString()
            val portion : String = updateDialog.etUpdatePortion.text.toString()

            if(!name.isEmpty() && portion.toString().isNotEmpty()){
                val newMap : MutableMap<String,Any> = hashMapOf("Producto" to name, "Cantidad" to grams, "Porcion" to portion, "Imagen" to R.drawable.arroz)  // Creamos un diccionario que contiene los datos que el usuario introdujo en los EditText

                db.collection("Kabiot").whereEqualTo("Producto", nombre).get()
                    .addOnSuccessListener {
                        for(doc in it){
                            elId = doc.id
                            db.collection("Kabiot").document(elId).update(newMap)  // Le decimos que queremos que nos actualice esa fila de la DB con los datos que estan dentro de newMap
                        }
                        readDataFirebase()
                    }
                    updateDialog.dismiss()
            }
            else{
                Toast.makeText(applicationContext, "Nombre y gramos no pueden estar vacios", Toast.LENGTH_SHORT).show()
            }
        }

        updateDialog.cancelButton.setOnClickListener(){
            updateDialog.dismiss()
        }
        updateDialog.show() // El show se pone al final
    }*/







    ///////////////////////////////////////////////////////////////////////////////////////////////  SQLITE   //////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////  SQLITE  //////////////////////////////////////////////////////////////////////////////////////
    /*private fun showMyListIntoRecyclerView(){
        if(getItemList().size > 0){                          //
            myRecylerProductView.visibility = View.VISIBLE   // Si hay algo para mostrar de la base de datos, hace visible la RecyclerView, y esconde el mensaje de "No Records"
            TvNoRecordsAvailable.visibility = View.GONE      //

            myRecylerProductView.layoutManager = LinearLayoutManager(this)

            var myListAdapter : ProductsAdapter = ProductsAdapter(this,getItemList()) // Instanciamos un objeto de la clase ProductsAdapter donde inflamos la vista de RecylerView
            myRecylerProductView.adapter = myListAdapter // Le damos permisos a la ListView de mostrar lo que inflamos en el Products adapter
        }
        else{
            myRecylerProductView.visibility = View.GONE     //
            TvNoRecordsAvailable.visibility = View.VISIBLE  // Si no hay nada para mostrar de la base de datos, hace visible el mensaje de "No Records", y esconde la RecyclerView
        }
    }*/

    /*private fun getItemList() : ArrayList<Producto>{

        val DBHandler :dataBaseHandler = dataBaseHandler(this)             // Esta funcion se encarga de recibir los datos recibidos de la dB y convertirlos en lista
        val empList : ArrayList<Producto> = DBHandler.viewProduct()

        return empList
    }*/

    /*fun addNewProduct(){
       val addProductDialog = Dialog(this)  // Le estamos avisando que su trabajo va a ser mandar un dialogo, todavia no sabe cual, pero para que esté trucha
       addProductDialog.setCancelable(false)

       addProductDialog.setContentView(R.layout.agregar_producto_layout)  // Le estamos diciendo "wey, quiero que me traigas para acá la view de Agregar producto, voy a trabajar sobre esa"

       addProductDialog.addButton.setOnClickListener(){
           val name = addProductDialog.etAddName.text.toString()
           val gram = addProductDialog.etAddGrams.text.toString()

           val handlerDeDB : dataBaseHandler = dataBaseHandler(this)     // Creamos una instancia de nuestro dataBaseHandler para poder usar su metodo de addProduct

           if(!name.isEmpty() && gram.toString().isNotEmpty()){

               /*val status = handlerDeDB.addProduct(Producto(1, name,gram,R.drawable.arroz))
               if(status > -1){
                   Toast.makeText(applicationContext, "Producto añadido", Toast.LENGTH_SHORT).show()
                   showMyListIntoRecyclerView()
                   addProductDialog.etAddName.text.clear()
                   addProductDialog.etAddGrams.text.clear()
                   addProductDialog.dismiss()
               }*/
               addNewProductFirestore(name, gram, R.drawable.arroz)
               addProductDialog.etAddName.text.clear()
               addProductDialog.etAddGrams.text.clear()
               addProductDialog.dismiss()
           }
           else{
               Toast.makeText(applicationContext,"Nombre de producto y cantidad no pueden estar en blanco", Toast.LENGTH_LONG).show()
           }
       }
       addProductDialog.cancelButton.setOnClickListener(){
           addProductDialog.dismiss()
       }
       addProductDialog.show() // El show se pone al final
   }*/


    /*fun UpdateRecordDialog (prodModelClass : Producto){

        val updateDialog = Dialog(this, R.style.Theme_Dialog)  // Instanciamos un objeto de la clase Dialog, un API que ayuda a crear pop up windows
        updateDialog.setCancelable(false)

        updateDialog.setContentView(R.layout.activity_modificar_producto)  // Estamos "inflando" nuestra xml de modificar el producto

        updateDialog.etUpdateName.setText(prodModelClass.nombreProducto)  // Al campo de rellenar nombre lo llenamos con el nombre actual del producto, es el que se va modificar
        updateDialog.etUpdateGrams.setText(prodModelClass.cantidadProducto)


        updateDialog.modifyButton.setOnClickListener(){
            val name = updateDialog.etUpdateName.text.toString()
            val grams : String
            grams = updateDialog.etUpdateGrams.text.toString()


            val handlerDeDB : dataBaseHandler = dataBaseHandler(this) // Instanciamos un objeto de tipo dataBaseHandler para poder usar las funciones de CRUD

            if(!name.isEmpty() && grams.toString().isNotEmpty()){
                val status = handlerDeDB.updateProduct(Producto(prodModelClass.identificador, name,grams, R.drawable.arroz))
                if(status > -1){
                    Toast.makeText(applicationContext, "Producto editado", Toast.LENGTH_SHORT).show()
                    showMyListIntoRecyclerView()
                    updateDialog.dismiss()
                }
            }
            else{
                Toast.makeText(applicationContext, "Nombre y gramos no pueden estar vacios", Toast.LENGTH_SHORT).show()
            }
        }

        updateDialog.cancelButton.setOnClickListener(){
            updateDialog.dismiss()
        }

        updateDialog.show() // El show se pone al final
    }*/



    /*fun deleteRecordAlertDialog(prodModelClass: Producto){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Eliminar producto")

        builder.setMessage("Seguro que deseas eliminar ${prodModelClass.nombreProducto}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Confirmo"){ dialogInterface, which ->
            val handlerDeDB : dataBaseHandler = dataBaseHandler(this)

            val status = handlerDeDB.deleteProduct(Producto(prodModelClass.identificador, prodModelClass.nombreProducto,prodModelClass.cantidadProducto,prodModelClass.imagenProducto))
            if(status > -1){
                Toast.makeText(applicationContext, "Producto eliminado", Toast.LENGTH_SHORT).show()
                showMyListIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val dialogoDeAlerta : AlertDialog = builder.create()
        dialogoDeAlerta.setCancelable(false)
        dialogoDeAlerta.show()

    }*/


}