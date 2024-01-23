package com.example.parejas_alejandrogutierrezisabelroldan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import android.content.Context;
import android.content.SharedPreferences;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private EditText editText1, editText2, editText3, mostrarTodosEditText;
    private TextView textView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = findViewById(R.id.editTextText2);
        editText2 = findViewById(R.id.editTextText3);
        editText3 = findViewById(R.id.editTextText4);
        textView = findViewById(R.id.textVal1);
        mostrarTodosEditText = findViewById(R.id.MostrarTodos);

        sharedPreferences = getSharedPreferences("MiArchivo", Context.MODE_PRIVATE);

        Button guardarButton = findViewById(R.id.buttonSave);
        Button buscarButton = findViewById(R.id.buttonLookFor);
        Button mostrarTodosButton = findViewById(R.id.buttonDelete);
        Button borrarButton = findViewById(R.id.btnShowAll);

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valor1 = editText1.getText().toString().trim();  // Eliminar espacios
                String valor2 = editText2.getText().toString();
                String valor3 = editText3.getText().toString();

                // Crear una nueva cadena que representa un trío de datos
                String trio = valor1 + "," + valor2 + "," + valor3;

                // Obtener la lista actual de tríos almacenados
                Set<String> listaTrios = sharedPreferences.getStringSet("clave_trios", new HashSet<String>());

                // Verificar si el primer valor ya existe
                if (contienePrimerValor(listaTrios, valor1)) {
                    // Mostrar AlertDialog para confirmar la actualización
                    mostrarDialogoActualizar(trio, valor1);
                } else {
                    // Agregar el nuevo trío a la lista
                    listaTrios.add(trio);

                    // Guardar la lista actualizada en SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet("clave_trios", listaTrios);
                    editor.apply();

                    // Limpiar campos de texto
                    editText1.setText("");
                    editText2.setText("");
                    editText3.setText("");

                    // Mostrar mensaje de éxito
                    Toast.makeText(MainActivity.this, "Se ha guardado con éxito", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener valores de búsqueda
                String valorBusqueda = editText1.getText().toString();

                // Recuperar la lista de tríos almacenados
                Set<String> listaTrios = sharedPreferences.getStringSet("clave_trios", new HashSet<String>());

                // Filtrar tríos que contienen el valor de búsqueda
                Set<String> resultados = new HashSet<>();
                for (String trio : listaTrios) {
                    if (trio.contains(valorBusqueda)) {
                        resultados.add(trio);
                    }
                }

                // Mostrar resultados en el TextView
                mostrarTriosEnEditText(resultados);
            }
        });

        mostrarTodosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Recuperar la lista de tríos almacenados
                Set<String> listaTrios = sharedPreferences.getStringSet("clave_trios", new HashSet<String>());

                // Mostrar todos los tríos en el EditText destinado
                mostrarTodosTriosEnEditText(listaTrios);
            }
        });

        borrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar si el primer campo de texto no está vacío
                String valorABorrar = editText1.getText().toString();
                if (!valorABorrar.isEmpty()) {
                    mostrarDialogoConfirmacionBorrar(valorABorrar);
                } else {
                    // Mostrar mensaje si el campo está vacío
                    Toast.makeText(MainActivity.this, "El campo está vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para verificar si el primer valor está contenido en el conjunto de tríos
    private boolean contienePrimerValor(Set<String> listaTrios, String primerValor) {
        for (String existente : listaTrios) {
            String[] valores = existente.split(",");
            if (valores.length > 0 && valores[0].trim().equals(primerValor)) {
                return true;
            }
        }
        return false;
    }

    private void mostrarDialogoActualizar(final String trioExistente, String valor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmar actualización");
        builder.setMessage("El trío ya existe. ¿Desea actualizarlo?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Obtener valores actualizados del nuevo trío dentro del onClick
                String valor1 = editText1.getText().toString().trim();
                String valor2 = editText2.getText().toString();
                String valor3 = editText3.getText().toString();
                String nuevoTrio = valor1 + "," + valor2 + "," + valor3;

                // Llamar a la función para realizar la actualización
                actualizarTrio(trioExistente, nuevoTrio, valor);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // No hacer nada si se selecciona "No"
            }
        });
        builder.show();
    }

    // Nueva función para realizar la actualización
    private void actualizarTrio(String valorAActualizar, String nuevoTrio, String valor) {
        // Obtener la lista actual de tríos almacenados
        Set<String> listaTrios = sharedPreferences.getStringSet("clave_trios", new HashSet<String>());

        // Crear una nueva lista para almacenar los tríos actualizados
        Set<String> nuevaListaTrios = new HashSet<>();

        // Iterar sobre la lista existente y actualizar el trío si es necesario
        for (String trio : listaTrios) {
            if (trio.contains(valorAActualizar)) {
                // Reemplazar el trío antiguo con el nuevo
                nuevaListaTrios.add(nuevoTrio);
            } else {
                // Conservar los tríos que no necesitan actualización
                nuevaListaTrios.add(trio);
            }
        }

        // Actualizar directamente la lista existente con los tríos actualizados
        listaTrios.clear();
        listaTrios.addAll(nuevaListaTrios);

        // Guardar la lista actualizada en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("clave_trios", listaTrios);
        editor.commit();

        // Limpiar campos de texto
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");

        // Mostrar mensaje de éxito
        Toast.makeText(MainActivity.this, "Se ha actualizado con éxito", Toast.LENGTH_SHORT).show();
    }

    private void mostrarTriosEnEditText(Set<String> listaTrios) {
        // Mostrar los primeros resultados en los EditText
        List<String> triosList = new ArrayList<>(listaTrios);

        if (!triosList.isEmpty()) {
            String[] datos = triosList.get(0).split(",");
            editText1.setText(datos[0]);
            editText2.setText(datos[1]);
            editText3.setText(datos[2]);
        } else {
            // Limpiar los EditText si no hay resultados
            editText1.setText("");
            editText2.setText("");
            editText3.setText("");
        }
    }

    private void mostrarTodosTriosEnEditText(Set<String> listaTrios) {
        // Limpiar el EditText antes de mostrar los resultados
        mostrarTodosEditText.setText("");

        // Mostrar todos los tríos en el EditText
        for (String trio : listaTrios) {
            mostrarTodosEditText.append(trio + "\n");
        }
    }

    private void mostrarDialogoConfirmacionBorrar(final String valorABorrar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Está seguro que quiere eliminar '" + valorABorrar + "'?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Recuperar la lista de tríos almacenados
                Set<String> listaTrios = sharedPreferences.getStringSet("clave_trios", new HashSet<String>());

                // Eliminar el trío que contiene el valor a borrar
                Set<String> nuevaListaTrios = new HashSet<>();
                for (String trio : listaTrios) {
                    if (!trio.contains(valorABorrar)) {
                        nuevaListaTrios.add(trio);
                    }
                }

                // Guardar la lista actualizada en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("clave_trios", nuevaListaTrios);
                editor.apply();

                // Limpiar el campo de texto
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");

                // Mostrar Toast indicando que se ha eliminado correctamente
                Toast.makeText(MainActivity.this, "Se ha eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // No hacer nada si se selecciona "No"
            }
        });
        builder.show();
    }
}