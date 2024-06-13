package p2;

import net.agsh.towerdefense.*;

import java.util.ArrayList;

/*
n	    NoSort	InsertionSort	MergeSort	QuickSort
496	    0,0482	0,3871	        0,0792	    0,0355
946	    0,0904	1,3263	        0,1726	    0,0927
1540	0,1582	3,8577	        0,2993	    0,1741
2278	0,2519	9,4906	        0,4780	    0,2950
3160	0,3939	20,3400     	0,7289	    0,5277

Como podemos observar en la tabla, nos sale que el mejor método para seleccionar las ubicaciones
sería el NoSort. Sin embargo, esto es porque el número de ubicaciones que buscamos es pequeño.
Si el número fuera más grande, los resultados serían distintos, y el método QuickSort sería el más óptimo.
Además, hasta el método MergeSort sería más óptimo que No Sort. Por tanto, aunque los resultados me digan que
NoSort parece el mejor, yo me decantaría finalmente por QuickSort, ya que es el que mejor se comporta en general (véase que la diferencia
en esta tabla entre NoSort y QuickSort es muy pequeña, por lo que seguirá siendo rentable coger QuickSort sabiendo que con valores más grandes se volverá el más óptimo).

*/



public class Main {
    public static void main(String[] args) {
        // initialize game and map
        Game g = Game.getInstance();
        g.init(0);
        Config config = new Config();

        System.out.print("n\tNoSort\tInsertionSort\tMergeSort\tQuickSort\n");

        for(float scale = 0.5f ; scale < 1.5 ; scale += 0.2f) {
            Map map = new Map(new Point2D(config.get(Config.Parameter.MAP_SIZE_X) * scale,
                    config.get(Config.Parameter.MAP_SIZE_Y) * scale),
                    config.get(Config.Parameter.MAP_GRID_SPACE));
            map.init();

            // assign values to nodes and print map
            boolean printMap = false;
            MapNode center = map.getNodes()[map.getNodes().length / 2][map.getNodes()[0].length / 2];
            for (int i = 0; i < map.getNodes().length; i++) {
                for (int j = 0; j < map.getNodes()[i].length; j++) {
                    if (map.getNodes()[i][j].isWalkable()) {
                        if(printMap) {
                            System.out.print("   ");
                        }
                    } else {
                        float distanceToCenter = center.getPosition().distance(map.getNodes()[i][j].getPosition());
                        map.getNodes()[i][j].setValue(0, distanceToCenter);
                        if(printMap) {
                            System.out.printf("%2.0f ", distanceToCenter / 10f);
                        }
                    }
                }
                if(printMap) {
                    System.out.println();
                }
            }

            // select best nodes for towers
            Point2D size = g.getMap().getSize();
            float separation = map.getSeparation();
            int numberOfTowers = (int) (g.getParam(Config.Parameter.TOWER_DENSITY) * size.x * size.y / (separation * separation));
            ArrayList<MapNode> best = selectBestNodes(map.getNodesList(), numberOfTowers);
        }
    }

    private static ArrayList<MapNode> deepCopy(ArrayList<MapNode> nodes) {
        ArrayList<MapNode> copy = new ArrayList<>();
        for(MapNode node : nodes) {
            copy.add(new MapNode(node.getPosition(), node.isWalkable(), node.getValues()));
        }
        return copy;
    }

    private static boolean NodeListEquals(ArrayList<MapNode> a, ArrayList<MapNode> b, int valueIndex) {
        if(a.size() != b.size()) {
            return false;
        }
        for(int i=0; i<a.size(); i++) {
            if(a.get(i).getValue(valueIndex) != b.get(i).getValue(valueIndex)) {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<MapNode>  selectBestNodes(ArrayList<MapNode> nodesList, int count) {
        int n = nodesList.size();
        long maxTime = 1000;

        System.out.print(n+"\t");

        // ========================== No sort ==========================
        int iterations = 0;
        Chronometer c = new Chronometer();
        ArrayList<MapNode> bestNoSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestNoSort = selectBestNodesNoSort(nodesListCopy, count);
            iterations++;
        } while(c.getElapsedTime() < maxTime);
        float timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        // ========================== Insertion sort ==========================
        iterations = 0;
        c = new Chronometer();
        ArrayList<MapNode> bestInsertionSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestInsertionSort = selectBestNodesInsertionSort(nodesListCopy, count);
            iterations++;
        } while(c.getElapsedTime() < maxTime);
        timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        if(!NodeListEquals(bestNoSort, bestInsertionSort, 0)) {
            System.out.println("ERROR");
        }

        // ========================== Merge sort ==========================
        iterations = 0;
        c = new Chronometer();
        ArrayList<MapNode> bestMergeSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestMergeSort = selectBestNodesMergeSort(nodesListCopy, count);
            iterations++;
        } while(c.getElapsedTime() < maxTime);
        timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        if(!NodeListEquals(bestNoSort, bestMergeSort, 0)) {
            System.out.println("ERROR");
        }

        // ========================== Quick sort ==========================
        iterations = 0;
        c = new Chronometer();
        ArrayList<MapNode> bestQuickSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestQuickSort = selectBestNodesQuickSort(nodesListCopy, count);
            iterations++;
        } while(c.getElapsedTime() < maxTime);
        timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        if(!NodeListEquals(bestNoSort, bestQuickSort, 0)) {
            System.out.println("ERROR");
        }

        System.out.println();

        return bestNoSort;
    }

    private static ArrayList<MapNode> selectBestNodesNoSort(ArrayList<MapNode> nodesList, int count) {
        // TODO: Return "count" nodes with the lowest value in the index 0 WITHOUT ORDERING the list. Given a node
        // in the list, the value of the node is accessed with node.getValue(0). For example, the following snippet
        // prints all the values of the nodes:
        //
        // for(MapNode node : nodesList) {
        //   System.out.println(node.getValue(0));
        // }
        ArrayList<MapNode> bestNodes = new ArrayList<>(); //Lista con los count mejores nodos
        MapNode min = nodesList.get(0); //Nodo con el valor minimo
        ArrayList<MapNode> aux = new ArrayList<>(nodesList); //Copia de la lista de nodos para poder trabajar sobre ella
        while(bestNodes.size()<count) { //Mientras no se hayan seleccionado los count mejores nodos
            for (MapNode node : aux) { //Se recorre la lista de nodos
                if (node.getValue(0) <= min.getValue(0)){ //Si el primer valor del nodo es menor o igual que el valor minimo
                    min = node; //Se actualiza el valor minimo
                }
            }
            bestNodes.add(min); //Se añade el nodo con el valor minimo a la lista de mejores nodos
            aux.remove(min); //Se elimina el nodo con el valor minimo de la lista auxiliar para que no se vuelva a seleccionar
        }
        return bestNodes; //Se devuelve la lista de mejores nodos
    }

    private static void ordenacionInsercion(ArrayList<MapNode> a){
        for(int i = 0; i < a.size(); i++){
            MapNode actual = a.get(i);
            int pos = -1;
            for(int j = i; j>=0; j--){
                if(actual.getValue(0) < a.get(j).getValue(0)){
                    pos = j;
                }
            }
            if(pos != -1){
                a.remove(actual);
                a.add(pos, actual);
            }
        }
    }


    private static ArrayList<MapNode> selectBestNodesInsertionSort(ArrayList<MapNode> nodesList, int count) {
        // TODO: Implement insertion sort according to the values of nodes in the index 0. Given a node in the list, the value
        // of the node is accessed with node.getValue(0). The list is sorted in ascending order.
        // Return the "count" first nodes in the list.
        //
        // Replace all the code in this method with your own code.
        ArrayList<MapNode> bestNodes = new ArrayList<>();
        ordenacionInsercion(nodesList);
        for(int i = 0; i < count; i++){
            bestNodes.add(nodesList.get(i));
        }
        return bestNodes;
    }


    private static void mezclar(ArrayList<MapNode> a, int min, int medio, int max){

        int i = min;
        int j = medio +1;
        ArrayList<MapNode> aux = new ArrayList<>();
        int cnt = 0;

        while(i <= medio && j <= max) {
            if (a.get(i).getValue(0) <= a.get(j).getValue(0)) {
                aux.add(cnt, a.get(i));
                i++;
            } else {
                aux.add(cnt, a.get(j));
                j++;
            }
            cnt++;
        }
        while(i <= medio){
            aux.add(cnt, a.get(i));
            i++;
            cnt++;
        }
        while(j <= max){
            aux.add(cnt, a.get(j));
            j++;
            cnt++;
        }
        cnt = 0;
        for(int k = min; k <= max; k++){
            a.set(k, aux.get(cnt));
            cnt++;
        }
    }

    private static void ordenacionMezcla(ArrayList<MapNode> a, int min, int max){
        if(min < max){
            ordenacionMezcla(a, min,(min + max)/2);
            ordenacionMezcla(a,(min + max)/2+1, max);
            mezclar(a, min, (min + max)/2, max);
        }
    }
    private static ArrayList<MapNode> selectBestNodesMergeSort(ArrayList<MapNode> nodesList, int count) {
        // TODO: Implement merge sort according to the values of nodes in the index 0. Given a node in the list, the value
        // of the node is accessed with node.getValue(0). The list is sorted in ascending order.
        // Return the "count" first nodes in the list.
        //
        // Replace all the code in this method with your own code.
        int tam = nodesList.size()-1;
        ordenacionMezcla(nodesList,0, tam);
        ArrayList<MapNode> bestNodes = new ArrayList<>();

        for(int i = 0; i < count; i++){
            bestNodes.add(nodesList.get(i));
        }
        return bestNodes;
    }

    private static void intercambia(ArrayList<MapNode> a, int i , int j){
        MapNode aux = a.get(i);
        a.set(i,a.get(j));
        a.set(j, aux);
    }
    private static int partir(ArrayList<MapNode> a, int min, int max){
        float pivot = a.get(min).getValue(0);
        int i = min+1;
        int j = max;

        do{
            while (i <= j && a.get(i).getValue(0) <= pivot){
                i++;
            }
            while (i <= j && a.get(j).getValue(0) > pivot){
                j--;
            }
            if(i<j){
                intercambia(a,i,j);
            }
        }while (i <= j);
        intercambia(a,min,j);
        return j;
    }
    public static void ordenacionRapida(ArrayList<MapNode> a, int min, int max){
        if(min < max){
            int pivot = partir(a, min, max);
            ordenacionRapida(a, min, pivot-1);
            ordenacionRapida(a, pivot+1, max);
        }
    }
    private static ArrayList<MapNode> selectBestNodesQuickSort(ArrayList<MapNode> nodesList, int count) {
        // TODO: Implement quick sort according to the values of nodes in the index 0. Given a node in the list, the value
        // of the node is accessed with node.getValue(0). The list is sorted in ascending order.
        // Return the "count" first nodes in the list.
        //
        // Replace all the code in this method with your own code.
        ArrayList<MapNode> bestNodes = new ArrayList<>();
        int tam = nodesList.size()-1;
        ordenacionRapida(nodesList,0, tam);

        for(int i = 0; i < count; i++){
            bestNodes.add(nodesList.get(i));
        }
        return bestNodes;
    }
}