
/*
En mi programa he utilizado la técnica de vuelta atrás para resolver el problema. Me he definido una variable global que es el número de combinaciones que se han hecho.
Cada torreta tiene un valor que se calcula con una fórmula que he creado. El valor de una torreta es la suma de los pesos de cada característica de la torreta multiplicado por el valor de cada característica.
El método recursivo que he creado recorre el array de torretas y comprueba si se puede comprar la torreta. Si se puede comprar, se añade al array de torretas compradas y se llama al método recursivo de nuevo.
Una vez que se ha llamado al método recursivo, se comprueba si el dinero que queda es mayor que el dinero que se ha gastado en torretas. Si es así, se comprueba si el número de combinaciones es menor que 15000.
Se compara el valor de todas las torretas con el valor de las torretas que se han comprado hasta el momento. Si vemos que hay un valor total más óptimo con otra combinación que no sea la seleccionada, se cambia la combinación seleccionada.
Si las torretas del array auxiliares es el mejor posible, añado esas torretas a la lista de indices seleccionados que voy a devolver.
He definido dos métodos privados, uno para obtener el índice de una torreta en un array de torretas, y otro para calcular el coste total de las torretas de un array.
Finalmente, el método buyTowers llama al método recursivo para comprar torretas, y devuelve el array de torretas seleccionadas.
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

public class TowerBuyer {
    private static int combinaciones=0;//Contador de combinaciones
    private static float getTowerValue(Tower tower){ //Metodo para calcular el valor de una torreta
        return -13.875f*tower.getRange()+149.775f*tower.getDamage()+62.755f*tower.getDispersion()-0.925f*tower.getCooldown();//Fórmula con pesos para obtener el valor óptimo de una torreta
    }

    private static void comprarTorretas(ArrayList<Tower> towers, ArrayList<Tower> aux, float money, ArrayList<Integer> selected, boolean done){//Metodo recursivo para comprar torretas
        if(!done){
            for(int i=0; i<towers.size() && combinaciones < 15000; i++){//Recorro el array de torretas y compruebo que no se hayan hecho más de 15000 combinaciones
                float costeTotal = towers.get(i).getCost()+costeTotal(aux); //Calculo el coste total de todas las torretas de mi array auxiliar más la que quiero intentar meter.
                int pos=buscar(aux,towers.get(i)); //Busco la posicion de la torreta que quiero meter en el array auxiliar
                if(aux.size()<towers.size() && costeTotal<=money){ //Si el coste total es menor que el dinero que tengo y la torreta no está en el array auxiliar
                    if(pos==-1){//Si la torreta no está en el array auxiliar
                        aux.add(towers.get(i));//Añado la torreta al array auxiliar
                        comprarTorretas(towers,aux,money,selected,false);//Vuelvo a llamar a la funcion
                        aux.remove(aux.size()-1);//Elimino la última torreta del array auxiliar
                    }
                }else{//Si el coste total es mayor que el dinero que tengo o la torreta está en el array auxiliar
                    done=true;//Cambio la variable done a true
                    comprarTorretas(towers,aux,money,selected,done);//Vuelvo a llamar a la funcion, pero esta vez con done a true
                }
            }
        }else{//Si done es true
            float totalOptimo=0;//Inicializo el valor optimo
            float totalAux=0;//Inicializo el valor auxiliar
            for(int i=0;i<aux.size();i++){ //Recorro el array auxiliar
                totalAux+=getTowerValue(aux.get(i)); //Calculo el valor de la torreta que estoy mirando, y lo sumo al valor auxiliar total
            }
            for(int i=0;i<selected.size();i++){ //Recorro el array de torretas seleccionadas
                totalOptimo+=getTowerValue(towers.get(selected.get(i))); //Calculo el valor de la torreta que estoy mirando, y lo sumo al valor optimo total
            }
            if(totalAux>totalOptimo){ //Si el valor auxiliar es mayor que el optimo
                selected.clear(); //Limpio el array de torretas seleccionadas
                for(int i=0;i<aux.size();i++){ //Recorro el array auxiliar
                    selected.add(buscar(towers,aux.get(i))); //Añado al array de torretas seleccionadas la posicion de la torreta que estoy mirando
                }
            }
            combinaciones++; //Incremento el contador de combinaciones
        }
    }

    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

        ArrayList<Integer> selected = new ArrayList<>();//Array que contendrá los indices de las torretas que voy a comprar
        ArrayList<Tower> aux = new ArrayList<>();//Array auxiliar para ir probando las combinaciones de torretas
        boolean done = false;//Variable para saber si es una combinación válida
        comprarTorretas(towers,aux,money,selected,done);//Llamo al método recursivo para comprar torretas

        combinaciones=0;//Reinicio el contador de combinaciones

        return selected;
    }

    private static int buscar(ArrayList<Tower> towers, Tower tower){ //Metodo para buscar la posicion de una torreta en el array de torretas
        int i=0;//Inicializo el contador
        while(i<towers.size() && towers.get(i)!=tower){ //Mientras no haya llegado al final del array y la torreta no sea la que estoy buscando
            i++; //Incremento el contador
        }
        if(i==towers.size()) { //Si he llegado al final del array
            i = -1; //Devuelvo -1
        }
        return i;//Devuelvo el contador
    }

    private static float costeTotal(ArrayList<Tower> towers){//Metodo para calcular el coste total de las torretas de un array
        float coste = 0;//Inicializo el coste
        for(int i=0;i<towers.size();i++){//Recorro el array de torretas
            coste+=towers.get(i).getCost();//Sumo el coste de la torreta que estoy mirando al coste total
        }
        return coste;//Devuelvo el coste total
    }
}