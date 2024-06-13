/*
En mi programa he utilizado la técnica de programación dinámica para resolver el problema. He hecho una variación del problema de la mochila.
He creado una matriz de tamaño (número de torretas + 1) * (dinero + 1) donde en cada celda he almacenado el valor óptimo de las torretas que puedo comprar con el dinero que tengo.
Ese valor óptimo lo he calculado con la fórmula que he creado para calcular el valor de una torreta. El valor de una torreta es la suma de los pesos de cada característica de la torreta multiplicado por el valor de cada característica.
He aplicado a cada atributo un peso arbitrario para que el valor de la torreta sea lo más óptimo posible.
He aplicado bottom-up para rellenar la matriz y he recorrido la matriz de abajo a arriba y de derecha a izquierda para obtener las torretas seleccionadas.
He recuperado la solución óptima de la matriz TSR y he obtenido las torretas seleccionadas, que las he añadido a una lista de torretas seleccionadas.
Finalmente, he invertido la lista de torretas seleccionadas para que me queden los índices en el orden que vienen en el array de torretas.
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;
import java.util.Collections;

public class TowerBuyer {

    private static float getTowerValue(Tower t){

        return 36.8F*t.getDamage()-4.8F*t.getRange()+0.22F*t.getCooldown()-11.6F*t.getDispersion(); //Fórmula con pesos para obtener el valor óptimo de una torreta
    }

    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {
        ArrayList<Integer> selected = new ArrayList<>();

        //Creo la TSR
        float[][] TSR = new float[towers.size()+1][(int) money+1]; //Inicializo la matriz TSR

        for(int i=0; i<=towers.size(); i++){ //Recorro las filas
            for(int j=0; j<=money; j++){ //Recorro las columnas
                if(i==0 || j==0){ //Primer caso base. Si estoy en la primera fila o primera columna
                    TSR[i][j] = 0; //Inicializo a 0
                }else if(j<towers.get(i-1).getCost()){ //Segundo caso base. Si el dinero que tengo es menor que el coste de la torreta
                    TSR[i][j] = TSR[i-1][j]; //Copio el valor de la celda de arriba
                }else if(j>=towers.get(i-1).getCost()){ //Si el dinero que tengo es mayor o igual que el coste de la torreta
                    TSR[i][j] = Math.max(TSR[i-1][j], TSR[i-1][(j - (int) Math.ceil(towers.get(i-1).getCost()))] + getTowerValue(towers.get(i-1))); //Cojo el máximo entre el valor de la celda de arriba y el valor de la celda de arriba a la izquierda + el valor de la torreta
                }
            }
        }

        int pos = towers.size(); //Posición de la torreta, inicializada a la última
        int din=0; //Dinero inicializado a 0
        for(din=(int)money; pos>0 && din>0; pos--){ //Recorro la matriz TSR de abajo a arriba y de derecha a izquierda
            if(TSR[pos][din] != TSR[pos-1][din]){ //Si el valor de la celda de arriba es distinto al valor de la celda de arriba a la izquierda
                selected.add(pos-1); //Añado la torreta a la lista de torretas seleccionadas
                din -= (int) Math.ceil(towers.get(pos-1).getCost()); //Resto el coste de la torreta al dinero
            }
        }
        if (pos == 0 && TSR[pos][din] != 0.0F) { //Si he llegado a la primera fila y el valor de la celda es distinto de 0
            selected.add(pos-1); //Añado la torreta a la lista de torretas seleccionadas
        }
        Collections.reverse(selected); //Invierto la lista de torretas seleccionadas para que me queden los índices en el orden que vienen en el array de torretas
        return selected;
    }
}
