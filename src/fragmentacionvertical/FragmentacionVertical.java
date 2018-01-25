/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fragmentacionvertical;

/**
 *
 * @author Humberto
 */
public class FragmentacionVertical {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Fragmentacion fr=new Fragmentacion(3,4,4);
        int[][] matAA = new int[4][4];
        int [][] m={
            {1,0,1,0},
            {0,1,1,0},
            {0,1,0,1},
            {0,0,1,1}
        };
        fr.matrizUso=m;
        int[][] a={
            {15,20,10},
            {5,0,0},
            {25,25,25},
            {3,0,0}
        };
        fr.atributos=new String[4];
        fr.atributos[0]="A1";
        fr.atributos[1]="A2";
        fr.atributos[2]="A3";
        fr.atributos[3]="A4";
        fr.keyPrimary=new String();
        fr.keyPrimary="A1";
        fr.matrizAcceso=a;
        fr.matrizAA();
        fr.matrizAC();
        fr.particionar();
    }
    
}
