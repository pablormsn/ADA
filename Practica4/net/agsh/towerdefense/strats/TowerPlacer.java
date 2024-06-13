/*
El procedimiento que he usado para obtener un valor para cada nodo consiste en ver si yo, si colocara una torreta en ese nodo, si suponiendo que el enemigo más grande transitara por ese nodo transitable, el nodo de la torreta sería el más cercano posible.
Cojo el máximo rango posible que coge una torreta para medir y un rango mínimo inicializado a 0. Si entra en ese intervalo, le doy una puntuación. Si no, le doy una puntuación mayor y actualizo en ambos casos los extremos para ver si puedo encontrar otro nodo transitable aún más cercano.
Una vez hecho eso, empiezo mi programa. Siguiendo el esquema general de algoritmos voraces, obtengo todos los nodos posibles.
Ahora, como quiero colocar torretas en los nodos no transitables, calculo sus valores y los ordeno de mayor a menor. Estos serán mis candidatos.
Ahora, siguiendo el esquema general delos algoritmos voraces, busco mi lista de seleccionados. Para ello, ordeno la lista de torretas de menor a mayor radio.
Mientras la lista de candidatos no este vacía y el contador sea menor que el número de torretas, extraigo en cada iteración el primer nodo de la lista de candidatos, quedando eliminado de la lista. Como hemos ordenado la lista de candidatos de mayor a menor valor, el primer nodo de la lista de candidatos será el mejor nodo.
Ahora aplico la función de factibilidad. Si está dentro del mapa y no colisiona con ningún obstáculo, otra torreta, o con la ruta, lo añado a la lista de seleccionados, coloco la torreta en la posición de el nodo seleccionado y aumento el contador. Si no, lo descarto.
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.ArrayList;

public class TowerPlacer {

    //Método que calculará el valor de un nodo en función de la distancia que esté de un enemigo como máximo
    public static float getNodeValue(MapNode node, Map map)
    {
        // Características de un nodo: cercanía al punto de inicio de los goblins, cercanía a los obstáculos, cercanía a los nodos de la ruta
        float maxEnemyRadius = Game.getInstance().getParam(Config.Parameter.ENEMY_RADIUS_MAX); //Radio máximo de los enemigos
        float maxRange = Game.getInstance().getParam(Config.Parameter.TOWER_RANGE_MAX);//Rango máximo que puede tener una torreta
        float max = maxRange;//Extremo del intervalo
        float min = 0;//Otro extremo del intervalo. Lo empiezo en 0.
        float cnt = 0; //Contador de nodos transitables que estén dentro del rango máximo
        ArrayList<MapNode> walkableNodes = map.getWalkableNodes(); //Lista de nodos transitables
        for(MapNode w : walkableNodes){ //Recorro la lista de nodos transitables
            float dist = node.getPosition().distance(w.getPosition());//Distancia entre el nodo actual y el nodo transitable
            if(dist < maxRange && dist >= maxEnemyRadius){//Si la distancia entre el nodo actual y el nodo transitable sea al menos el radio máximo de los enemigos y está dentro del rango de la torreta
                float d = Math.abs(dist-maxEnemyRadius);//Distancia entre el nodo actual y el nodo transitable menos el radio máximo de los enemigos
                if((d < max && d > min) || d > max){//Si d está entre el mínimo y el máximo o es mayor que el máximo
                    cnt+=10;//Lo incremento en 10 si está dentro del rango actual
                    max = d;//Mi extremo del intervalo ahora será la distancia actual, para que si hay nodos más lejos, les de la mínima puntuación
                }else{
                    cnt+=15;//Lo incremento más porque está más cerca que los otros nodos. Le doy más importancia.
                    min = d;//Si la d es menor, reduzco el mínimo para buscar nodos aún más cercanos
                }
            }
        }
        return cnt;
    }

    public static boolean collide(Point2D entity1Position, float entity1Radius, Point2D entity2Position, float entity2Radius) {
        return entity1Radius + entity2Radius >= entity1Position.distance(entity2Position); //Compruebo si la distancia entre las dos entidades es menor que la suma de sus radios
    }

    private static boolean dentroMapa(Point2D posicion, float radio, float alto, float ancho) {
        return !(posicion.x < radio) && !(posicion.x > ancho - radio) && !(posicion.y < radio) && !(posicion.y > alto - radio); //Compruebo que el radio de la torreta no sobrepase los límites del mapa
    }

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {

        MapNode[][] grid = map.getNodes();//Matriz con todos las celdas
        ArrayList<MapNode> candidatos = new ArrayList<>(); //Nuestra lista de candidatos, para a posteriori aplicar el algoritmo voraz
        //Recorremos la cuadrícula
        for(int i = 0; i < grid.length; i++){ //Recorro las filas
            for(int j = 0; j < grid[i].length; j++){ //Recorro las columnas
                if(!grid[i][j].isWalkable()){//Si el nodo actual no es transitable. Nosotros buscamos nodos no transitables para colocar las torretas
                    grid[i][j].setValue(0,getNodeValue(grid[i][j], map));//Almaceno el valor de la celda en el primer valor del vector de valores. Podría ser cualquier otro valor de la lista (el profesor lo pone en el 2, pero yo prefiero ponerlo en el 0)
                    candidatos.add(grid[i][j]);//Añado el nodo a la lista de candidatos
                }
            }
        }
        candidatos.sort((n1, n2) -> Float.compare(n2.getValue(0), n1.getValue(0)));//Ordeno la lista de candidatos de mayor a menor valor, tal y como hacíamos en los ejercicios de algoritmos voraces

        //Momento de recorrer la solucion como en el algoritmo voraz
        ArrayList<Tower> placedTowers = new ArrayList<>();//Lista de torretas seleccionadas
        ArrayList<Obstacle> obstacles = map.getObstacles();//Lista de obstáculos del mapa
        ArrayList<MapNode> walkableNodes = map.getWalkableNodes();//Lista de nodos transitables
        float mapWidth = map.getSize().x; //Ancho del mapa
        float mapHeight = map.getSize().y; //Alto del mapa
        float maxEnemyRadius = Game.getInstance().getParam(Config.Parameter.ENEMY_RADIUS_MAX); //Radio máximo de los enemigos
        boolean colisiona;//Variable para comprobar si colisiona con algún obstáculo
        int i = 0; //Contador
        towers.sort((t1, t2) -> Float.compare(t1.getRadius(), t2.getRadius())); //Ordeno la lista de torretas de menor a mayor radio
        while(i<towers.size() && !candidatos.isEmpty()) {//Mientras no hayamos colocado todas las torretas y queden candidatos (ESQUEMA GENERAL DE LOS ALGORITMOS VORACES)
            Tower t = towers.get(i);//Selecciono la torreta correspondiente
            MapNode node = candidatos.remove(0);//Extraigo el nodo con mayor valor, que es el primero de la lista (porque la hemos ordenado)
            colisiona = false; //Reinicio la variable de colisión
            //FUNCIÓN DE FACTIBILIDAD
            boolean dentro = dentroMapa(node.getPosition(), t.getRadius(), mapHeight, mapWidth); //Compruebo si el nodo está dentro del mapa
            if (dentro) { //Si está dentro del mapa
                //Comprobamos si colisiona con algún obstáculo
                for (Obstacle o : obstacles) { //Recorro la lista de obstáculos
                    if (collide(o.getPosition(), o.getRadius(), node.getPosition(), t.getRadius())) {//Si colisiona con algún obstáculo
                        colisiona = true; //Colisiona
                    }
                }
                //Compribamos si colisiona con alguna torreta
                for (Tower tor : placedTowers) { //Recorro la lista de torretas que ya están colocadas
                    if (collide(node.getPosition(), t.getRadius(), tor.getPosition(), tor.getRadius())) {//Si colisiona con alguna torreta
                        colisiona = true; //Colisiona
                    }
                }
                //Comprobamos si colisiona con la ruta
                for (MapNode n : walkableNodes) { //Recorro la lista de nodos transitables
                    if (collide(node.getPosition(), t.getRadius(), n.getPosition(), maxEnemyRadius)) {//Si colisiona con algún nodo transitables
                        colisiona = true; //Colisiona
                    }
                }
                if (!colisiona) { //Si no colisiona con nada
                    t.setPosition(node.getPosition()); //Coloco la torreta en la posición del nodo
                    placedTowers.add(t); //Añado la torreta a la lista de torretas colocadas
                    i++; //Aumento el contador
                }
            }
        }
        return placedTowers;
    }
}