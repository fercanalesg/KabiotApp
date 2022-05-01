package com.example.prototipokabiot

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
/*
class dataBaseHandler(mContexto : Context) : SQLiteOpenHelper(mContexto, DATABASE_NAME,null, DATABASE_VERSION) {  // Heredamos de la clase SQLiteOpenHelper para poder utilizar todos sus metodos override y crear nuestra base de datos

    companion object{                             // Variables globales que contienen los datos basicos de la tabla
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "KabiotDataBase.db"  // Nombre de la base de datos
        private const val TABLE_CONTACTS = "ProductTable"  // Nombre de la tabla

        // Aqui declaramos los nombres de las columnas que tendra la tabla
        private const val KEY_ID = "Id"
        private const val KEY_NAME = "Name"
        private const val KEY_GRAMS = "Quantity"
        private const val KEY_IMAGE = "Image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Aquí estamos creando una variable tipo String que va guardar en sintaxis tipo SQLite (query), el comando para crear la tabla.
                                    // CREATE TABLE ProductTable(Id INTEGER PRIMARY KEY, Name TEXT, Quantity TEXT)
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"+ KEY_GRAMS + " TEXT," + KEY_IMAGE + " INTEGER" + ")")

        db?.execSQL(CREATE_PRODUCTS_TABLE) // Aqui le estamos diciendo "Ejecutame el comando que esta dentro de esta variable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { // Sirve para actualizar la tabla en caso de agregar columnas o filas

        db!!.execSQL("DROP TABLE IF EXIST" + TABLE_CONTACTS)
        onCreate(db)
    }

    /////////////////////////////////////////////////// ANADIR PRODUCTO ////////////////////////////////////////////////////////////////////////////
    fun addProduct(prod: Producto) : Long{ //Funcion para agregar productos
        val db = this.writableDatabase  // Con esto decimos que nuestra base de datos estará en modo escritura, para modificar

        val datosAdd = ContentValues() // Crea una especie de diccionario, le agregaremos clave-valor
        datosAdd.put(KEY_NAME, prod.nombreProducto) //(clave, valor) No ponemos el id porque al ser Primary Key, la base de datos lo agrega automaticamente.
        datosAdd.put(KEY_GRAMS, prod.cantidadProducto)
        datosAdd.put(KEY_IMAGE, prod.imagenProducto)

        // Insertando Fila
        val row = db.insert(TABLE_CONTACTS, null, datosAdd) // row retorna un Long, sirve solo para insertar la fila completa a la tabla

        db.close() // Cerramos la conexion con la base de datos en modo escritura
        return row

    }

    //////////////////////////////////////////////////  VER LOS PRODUCTOS /////////////////////////////////////////////////////////////////////////////////
    fun viewProduct() : ArrayList<Producto> { //Nos va regresar un ArrayList

        val productList  = ArrayList<Producto>() // Lista temporal que utilizaremos para la consulta, es solo local pero es lo que vamos a retornar para que pueda visualizarse en el RecyclerView

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS" // Query para que muestre todos los datos de la tabla

        val db = this.readableDatabase // Habilitamos la base de datos para que funcione en modo lectura
        var cursor: Cursor?=null // El cursor es literalmente el que ayuda a recorrer todos los datos para hacer la consulta.

        try{
            cursor = db.rawQuery(selectQuery, null) //Checa si hay un cursorr ya, si no se va al catch a realizar la consulta
        }
        catch (e: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id : Int          // Inicializamos unas variables locales, nos seran de ayuda para poder sustraer cada tipo de dato de la tabla, son para facilitar el paso de parametros en la variable "prod" simplemente
        var name : String
        var grams : String
        var image : Int

        if(cursor.moveToFirst()) { // Con esto solo aseguramos que el cursor SI tiene informacion que consultar, moveToFirst retorna un Booleano
            do{
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))  // "Dame el entero que se encuentra en la casilla del indice donde está posicionado ahorita el cursor, de la columna KEY_ID"
                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))  // "Dame el texto que este dentro de la columna KEY_NAME correspondiente a la fila donde está apuntando ahorita el cursor"
                grams = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GRAMS))
                image = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IMAGE))

                val prod = Producto(id, name, grams, image)
                productList.add(prod) // Vamos agregando cada fila con los datos consultados, a la lista local que creamos al principio de la funcion

            } while(cursor.moveToNext()) // Recorremos la base de datos hasta que moveToNext retorne false, es decir cuando ya no hay datos.
        }
        return productList // Retornamos todo el contenido de nuestra lista local, esto lo va a recibir el RecyclerView de la activity laLista
    }

    //////////////////////////////////////////////////////////////// MODIFICAR UN PRODUCTO //////////////////////////////////////////////////////////////////
    fun updateProduct(prod: Producto) : Int{
        val db = this.writableDatabase // Habilitamos la base de datos en modo escritura

        val datosUpdate = ContentValues()  // Es una especie de Diccionario

        datosUpdate.put(KEY_NAME, prod.nombreProducto)   // Estamos trayendo de vuelta cada dato modificable de nuestra base de datos del elemento que se va actualizar
        datosUpdate.put(KEY_GRAMS, prod.cantidadProducto) // NO ponemos el id porque ese dato NO es modificable, por lo tanto ni siquiera nos interesa traerlo a la superficie
        datosUpdate.put(KEY_IMAGE, prod.imagenProducto)

        // Actualizando Filas
        val whereClause = "${KEY_ID}=${prod.identificador}" // Query para decir que vas a modificar la fila del elemento en el que hiciste click
        val row = db.update(TABLE_CONTACTS, datosUpdate, whereClause, null)  // row retorna un Int  (Nombre de la tabla, tuContentValues, Query, null)

        db.close()
        return row  //Nos retorna un entero
    }

    ////////////////////////////////////////////////////////////////   ELIMINAR UN PRODUCTO ///////////////////////////////////////////////////////////////////
    fun deleteProduct(prod: Producto) : Int {
        val db = this.writableDatabase  // Habilitamos la base de datos en modo escritura

        val datosDelete = ContentValues() // Creamos una variable que es como un tipo Diccionario / Mapa
        //datosDelete.put(KEY_ID, prod.identificador) // Como vamos a eliminarla, solo nos interesa sustraer el id del elemento en el que hicimos click, todos sus demas datos no nos interesan.

        // Eliminando Fila
        val whereClause = "$KEY_ID=${prod.identificador}"  // Query donde le indicamos que vamos a borrar la fila que tenga el ID especifico del elemento en el que hicimos click
        val row = db.delete(TABLE_CONTACTS, whereClause, null)

        db.close()
        return row  //Nos retorna un entero

    }


}*/