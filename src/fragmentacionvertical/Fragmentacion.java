/*
 * UNIVERSIDAD DE LA SIERRA SUR.
 * Licenciatura en Informática
 * Bases de Datos Distribuidas.
 * Daniel Humberto Ramírez Juárez.
 * Fecha 16-01-2018.
 */
package fragmentacionvertical;

/**
 *
 * @author Humberto
 */
public class Fragmentacion {
    public String keyPrimary;
    public int[][] matrizUso;
    public int[][] matrizAcceso;
    public int[][] matrizAA;
    private int[][] matrizCA;
    private int numSitios;
    private int numConsultas;
    private int numAtributos;
    int[] ordenColumnas;
    int puntoDivision;

    public String[] atributos;

    public Fragmentacion(int numSitios, int numConsultas, int numAtributos) {
        this.numSitios = numSitios;
        this.numConsultas = numConsultas;
        this.numAtributos = numAtributos;
    }

    //método ref
    public int ref(int qk, int Ai, int Aj) {
        if (this.matrizUso[qk][Ai] != 0 && this.matrizUso[qk][Aj] != 0) {
            return 1;
        } else {
            return 0;
        }
    }
    
    //método ref.
    public int ref(int qk, int Ai, int Aj,int s) {
        if (this.matrizUso[qk][Ai] != 0 && this.matrizUso[qk][Aj] != 0) {
            return this.matrizUso[qk][Ai]*this.matrizAcceso[qk][s]+this.matrizUso[qk][Aj]*this.matrizAcceso[qk][s];
        } else {
            return 0;
        }
    }
    

    //método acc
    public int Acc(int qK) {
        int sum = 0;
        for (int i = 0; i < numSitios; i++) {
            sum += this.matrizAcceso[qK][i];
        }
        return sum;
    }

    public int afinidad(int Ai, int Aj) {
        int aff = 0;
        for (int fila = 0; fila < numConsultas; fila++) {
            if (ref(fila, Ai, Aj) == 1) {
                for (int columna = 0; columna < numSitios; columna++) {
                    aff += matrizAcceso[fila][columna];
                }
            }
        }
        return aff;
    }

    public void matrizAA() {
        int[][] matA = new int[numAtributos][numAtributos];
        for (int i = 0; i < numAtributos; i++) {
            for (int j = i; j < numAtributos; j++) {
                matA[i][j] = afinidad(i, j);
                matA[j][i] = matA[i][j];
            }
        }
        System.out.println("MatrizAA");

        matrizAA = matA;
        imprimirMatriz(matrizAA, numAtributos);
    }

    private void imprimirMatriz(int[][] matriz, int col) {
        int columna = 0;
        for (int fila = 0; fila < numAtributos; fila++) {
            for (columna = 0; columna < col; columna++) {
                System.out.print("[" + matriz[fila][columna] + "]\t");
            }
            System.out.println("");
        }
    }

    public void matrizAC() {
        int index;
        matrizCA = new int[numAtributos][numAtributos];
        int[] cont = new int[numAtributos];
        ordenColumnas = new int[numAtributos];
        int[] ordenFilas = new int[numAtributos];
        int loc;
        int aux;
        for (int fila = 0; fila < numAtributos; fila++) {
            for (int columna = 0; columna < 2; columna++) {
                matrizCA[fila][columna] = matrizAA[fila][columna];

            }
        }
        System.out.println("\nMatriz AC -" + (char) 97);
        imprimirMatriz(matrizCA, 2);
        index = 2;
        ordenColumnas[0] = 0;
        ordenColumnas[1] = 1;
        for (int posicion = 0; posicion < numAtributos; posicion++) {
            ordenFilas[posicion] = posicion;
        }

        while (index < numAtributos) {
            for (int columna = 0; columna < index; columna++) {
                cont[columna] = cont(matrizCA, columna - 1, index, columna, index);
            }
            cont[index] = cont(matrizCA, index - 1, index, index + 1, index);
            loc = mayor(cont, index);
            for (int fila = 0; fila < numAtributos; fila++) {
                for (int columna = index; columna > loc; columna--) {
                    matrizCA[fila][columna] = matrizCA[fila][columna - 1];
                }
                matrizCA[fila][loc] = matrizAA[fila][index];
            }
            System.out.println("\nMatriz AC -" + (char) (95 + index + 1));

            imprimirMatriz(matrizCA, index + 1);
            for (int pos = index; pos > loc; pos--) {
                ordenColumnas[pos] = ordenColumnas[pos - 1];
            }
            ordenColumnas[loc] = index;
            index = index + 1;

        }
        for (int posicion = 0; posicion < numAtributos; posicion++) {
            if (ordenFilas[posicion] != ordenColumnas[posicion]) {
                for (int columna = 0; columna < numAtributos; columna++) {
                    aux = matrizCA[posicion][columna];
                    matrizCA[posicion][columna] = matrizCA[ordenColumnas[posicion]][columna];
                    matrizCA[ordenColumnas[posicion]][columna] = aux;
                }
                aux = ordenFilas[posicion];
                ordenFilas[posicion] = ordenColumnas[posicion];
                ordenFilas[ordenColumnas[posicion]] = aux;
            }
        }

        System.out.println("\nMatriz AC ");
        imprimirMatriz(matrizCA, numAtributos);
        System.out.println("------------------------------------------------");

    }

    public int cont(int[][] matCA, int aa, int ab, int ac, int columnasCA) {
        int cont;
        cont = 2 * bond(matCA, aa, ab, columnasCA) + 2 * bond(matCA, ab, ac, columnasCA) - 2 * bond(matCA, aa, ac, columnasCA);
        return cont;
    }

    public int bond(int[][] matCA, int aa, int ab, int columnasCA) {
        int bond = 0;
        if (aa < 0 || ab > columnasCA) {
            bond = 0;
        } else {
            if (ab == columnasCA) {
                for (int columna = 0; columna < numAtributos; columna++) {
                    bond = bond + matCA[columna][aa] * matrizAA[columna][ab];
                }
            } else if (aa == columnasCA) {
                for (int columna = 0; columna < numAtributos; columna++) {
                    bond = bond + matCA[columna][ab] * matrizAA[columna][aa];
                }
            } else {
                for (int columna = 0; columna < numAtributos; columna++) {
                    bond = bond + matCA[columna][aa] * matCA[columna][ab];
                }
            }
        }
        return bond;
    }

    private static int mayor(int[] cont, int index) {
        int posicion = 0;
        int numMayor = cont[0];
        for (int i = 0; i <= index; i++) {
            if (cont[i] > numMayor) {
                numMayor = cont[i];
                posicion = i;
            }
        }
        return posicion;
    }

    public void particionar() {
        int CTQ = 0;
        int CBQ = 0;
        int COQ = 0;
        int best = 0;
        int z;
        boolean band=false;
        puntoDivision = 0;
        int[] ordenCol = new int[10];
        int[] max = new int[10];
        int[] queryTQ = new int[10];
        int[] queryBQ = new int[10];
        int[] querysT = new int[1];
        int[] querysB = new int[1];
        int aux = 0;
        int attKey = 0;
        boolean key = false;
        
        String[] atributo = new String[numAtributos];
        ordenCol = ordenColumnas;

        CTQ = calCTQ(queryTQ, numAtributos - 1, ordenColumnas, querysT);
        CBQ = calCBQ(queryBQ, numAtributos - 1, ordenColumnas, querysB);
        COQ = calCOQ(numAtributos - 1, queryTQ, queryBQ, querysT, querysB);
        best = CTQ * CBQ - (COQ * COQ);
        do {
            for (int columna = numAtributos - 2; columna >= 1; columna--) {
                for (int query = 0; query < numConsultas; query++) {
                    queryTQ[query] = 0;
                    queryBQ[query] = 0;
                }

                CTQ = calCTQ(queryTQ, columna, ordenCol, querysT);
                CBQ = calCBQ(queryBQ, columna, ordenCol, querysB);
                COQ = calCOQ(columna, queryTQ, queryBQ, querysT, querysB);

                z = CTQ * CBQ - (COQ * COQ);
                if (z > best) {
                    puntoDivision = columna;
                    max = ordenCol;
                }
            }
            shift(ordenCol, numAtributos);

        } while (aux != ordenCol[numAtributos - 1]);
        this.puntoDivision = puntoDivision;
        System.out.println("\nFRAGMENTO 1");
        
        for (int columna = 0; columna < puntoDivision; columna++) {
            for (int clave = 0; clave < attKey; clave++) {
                if (atributo[clave].equals(atributos[(ordenColumnas[max[columna]] + 1) - 1])) {
                    key = true;
                    
                }
            }
            if (!key) {
                System.out.print("Atributo: " + atributos[(ordenColumnas[max[columna]] + 1) - 1]); 
                if(atributos[(ordenColumnas[max[columna]] + 1) - 1].equals(keyPrimary)){
                    System.out.println("---KeyPrimary");
                    band=true;
                }else{
                    System.out.print("--");
                }
                System.out.println("");
            }
            key = false;
        }
        for (int clave = 0; clave < attKey; clave++) {
            System.out.print("Atributo: " + atributo[clave]);
            
        }
        System.out.println("\nFRAGMENTO 2");
        for (int columna = puntoDivision; columna < numAtributos; columna++) {
            key = false;
            for (int clave = 0; clave < attKey; clave++) {
                if (atributo[clave].equals(atributos[(ordenColumnas[max[columna]] + 1) - 1])) {
                    key = true;
                }
            }
            if (!key) {
                System.out.print("Atributo: " + atributos[(ordenColumnas[max[columna]] + 1) - 1]);
                if(atributos[(ordenColumnas[max[columna]] + 1) - 1].equals(keyPrimary)){
                    System.out.print("---KeyPrimary");
                }
                System.out.println("");
            }
        }
        for (int clave = 0; clave < attKey; clave++) {
            System.out.println("Atributo:i " + atributo[clave]);
        }
    }

    private int calCTQ(int[] queryTQ, int puntoDivision, int[] orden, int[] querysT) {
        int CTQ = 0;
        int consultas = buscarQueryTQ(orden, queryTQ, puntoDivision);
        CTQ = sum(queryTQ, consultas);
        querysT[0] = consultas;
        return CTQ;
    }

    private int calCBQ(int[] queryBQ, int puntoDivision, int[] nuevoOrdenCol, int[] totalquerysB) {
        int CBQ = 0;
        int consultas = buscarQueryBQ(nuevoOrdenCol, queryBQ, puntoDivision);
        CBQ = sum(queryBQ, consultas);
        totalquerysB[0] = consultas;
        return CBQ;
    }

    private int buscarQueryTQ(int[] nuevoOrdenCol, int[] queryTQ, int puntoDivision) {
        int querys = 0;
        for (int i = 0; i < numConsultas; i++) {
            for (int atributo = 0; atributo < puntoDivision; atributo++) {
                if (matrizUso[i][nuevoOrdenCol[atributo]] == 1) {
                    queryTQ[querys] = i;
                    querys++;
                }
            }
        }
        return querys;
    }

    private int buscarQueryBQ(int[] nuevoOrdenCol, int[] queryBQ, int puntoDivision) {
        int querys = 0;
        for (int i = 0; i < numConsultas; i++) {
            for (int atributo = puntoDivision; atributo < numAtributos; atributo++) {
                if (matrizUso[i][nuevoOrdenCol[atributo]] == 1) {
                    queryBQ[querys] = i;
                    querys++;
                }
            }
        }
        return querys;
    }

    private int sum(int[] queryTQ, int querys) {
        int frecuencias = 0;
        for (int query = 0; query < querys; query++) {
            for (int querysFrec = 0; querysFrec < numSitios; querysFrec++) {
                frecuencias = frecuencias + matrizAcceso[queryTQ[query]][querysFrec];
            }
        }
        return frecuencias;
    }

    private int calCOQ(int columna, int[] queryTQ, int[] queryBQ, int[] querysT, int[] querysB) {
        int querysCOQ = 0;
        int[] querysOQ = new int[20];
        int querysOQ_TQ = 0;
        int[] querysOQ_TQ_BQ = new int[20];
        boolean encontrado = false;
        for (int query = 0; query < numAtributos; query++) {
            for (int tq = 0; tq < querysT[0]; tq++) {
                if (ordenColumnas[query] == queryTQ[tq]) {
                    encontrado = true;
                }
            }
            if (encontrado == false) {
                querysOQ[querysOQ_TQ] = ordenColumnas[query];
                querysOQ_TQ++;
            }
            encontrado = false;
        }
        for (int query = 0; query < querysOQ_TQ; query++) {
            for (int bq = 0; bq < querysB[0]; bq++) {
                if (querysOQ[query] == queryBQ[bq]) {
                    encontrado = true;
                }
            }
            if (encontrado == false) {
                querysOQ_TQ_BQ[querysCOQ] = querysOQ[query];
                querysCOQ++;
            }
            encontrado = false;
        }
        int COQ = sum(querysOQ_TQ_BQ, querysCOQ);
        return COQ;
    }

    private static void shift(int[] orden, int numAt) {
        int aux;
        aux = orden[0];
        for (int columna = 0; columna < numAt - 1; columna++) {
            orden[columna] = orden[columna + 1];
        }
        orden[numAt - 1] = aux;
    }
}
