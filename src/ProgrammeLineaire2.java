import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import Jama.*; 
import java.text.NumberFormat.*;

/**
 *
 * @author Gashteovski
 */
public class ProgrammeLineaire2 {
    
    private int n;
    private int m;
    
    private double A[][];
    private double b[];
    private double c[];
    
    private int xB[];
    private double xBetoile[];
    
    private double B[][];
    private int xH[];
    private double H[][];
    
    private double cH[];
    private double cB[];
    
    private double epsilon;
    
    public enum state {INITIAL, INFAISABLE, NONBORNE, OPTIMAL};
    private state etat;
    
    public ProgrammeLineaire2(int m2, int n2){
        m = m2;
        n = n2;
                
        A = new double [n+m][m];
        b = new double [m];
        c = new double [m+n];
        
        xB = new int[m];
        xBetoile = new double[m];
        
        B = new double[m][m];
        xH = new int[n];
        H = new double [m][n];
        
        cH = new double [n];
        cB = new double [m];
        
        etat = state.INITIAL;
    }
    
    public ProgrammeLineaire2(double H2[][], double b2[], double c2[]){

        for (int i=0; i<m; i++)
            for (int j=0; j<n; j++)
                H[i][j] = H2[i][j];
        
        for (int i=0; i<m; i++)
            b[i] = b2[i];
        
        for (int i=0; i<n; i++)
            c[i] = c2[i];
    }
    
    public void input(double H2[][], double b2[], double cH2[]){
        for (int i=0; i<m; i++)
            for (int j=0; j<n; j++)
                H[i][j] = H2[i][j];
        
        for (int i=0; i<m; i++)
            b[i] = b2[i];
        
        for (int i=0; i<n; i++)
            cH[i] = cH2[i];
    }
    
    public void initialization(){
        this.initA();
        this.initc();
        this.initxB();
        this.initxBetoile();
        this.initB();
        this.initxH();
        this.initcB();
    }
    
    public void initA(){

        A = new double [m][m+n];
        for (int i=0; i<m; i++){
            for (int j=0; j<n+m; j++){
                if (j<n){
                    A[i][j] = H[i][j];
                }
                else
                    A[i][j] = 0;
            }
        }
        
        int ind = 1;
        for (int i=0; i<m; i++){
            for (int j=0; j<n+m; j++){
                if (j == ((n-1)+ind) ){
                    A[i][j] = 1;
                    ind++;
                    j++;
                }
            }
        }
    }
    
    public void initc(){
        for (int i=0; i<n+m; i++){
            if (i<n)
                c[i] = cH[i];
            else
                c[i] = 0;
        }
            
    }
    
    public void initxH(){
        for (int i=0; i<n; i++)
            xH[i] = i;
    }
    
    public void initxB(){
        int ind = n;
        for (int i=0; i<m; i++){
            xB[i] = ind;
            ind++;
        }
    }
    
    public void initxBetoile(){
        for (int i=0; i<m; i++){
            xBetoile[i] = b[i];
        }
    }
    
    public void initcB(){
        for (int i=0; i<m; i++)
            cB[i] = 0;
    }
    
    public void initB(){
        for (int i=0; i<m; i++)
            for (int j=0; j<m; j++)
                if (i==j)
                    B[i][j] = 1;
                else
                    B[i][j] = 0;
    }
        
    public void resolution(){
        boolean flag;
        int counter = 1; 
        do{
            if (counter <= 100){
                System.out.println("ITTERATION No: " + counter + "\n");
                flag = iteration();
                System.out.println("===================================");
                counter++;
            }
            else {
                System.out.println("(Bland rule) ITTERATION No: " + counter + "\n");
                flag = Bland();
                System.out.println("===================================");
                counter++;
            }
        } while (flag == true);
    }
    
    public boolean iteration(){
        //this.initialization();
        boolean flag = false;
        //Etape 1
        
        /* 
         * matrixB as object of the class Matrix; 
         * Bt = matrixB transposee
         */
        Matrix matrixB = new Matrix(B);
        Matrix Bt = matrixB.transpose();
        
        double AA[][] = new double[m][m];
        AA = matrixB.getArray();
        System.out.println("\nPrint Matrix B");
        for (int i=0; i<m; i++){
            for (int j=0; j<m; j++){
                System.out.print(AA[i][j] + " ");
                }
                System.out.println();
        }
        
        /*
         * cT = c transposed
         * yT = y transposed
         * matrixcT - cT as object of the class Matrix
         * matrixyT - yT as object of the class Matrix
         */
        double cT [][] = new double[m][1];
        double yT [][] = new double [m][1];
        
        for (int i=0; i<m; i++){
            cT[i][0] = c[xB[i]];
        }
        for (int i=0; i<m; i++)
            yT[i][0] = 0;
        
        
        Matrix matrixcT = new Matrix(cT);
        Matrix matrixyT = new Matrix(yT);
        
        double barray [][] = new double[1][m];
        barray = matrixcT.getArray();
        System.out.print("cT=");
        for (int i=0; i<m; i++)
            System.out.print(barray[i][0] + " ");
        
        barray = matrixyT.getArray();
        
        matrixyT = Bt.solve(matrixcT);
        yT = matrixyT.getArray();
        for (int i=0; i<m; i++)
            yT[i][0] = yT[i][0];
        
        System.out.println();
        System.out.print("y = ");
        for (int i=0; i<m; i++)
            System.out.print(yT[i][0] + " ");
        System.out.println();
        
        /* Etape 2
         * aj    - the aj-th colone of the matrix A
         * ind   - index for aj (from 0 to n-1)
         * r     - resolution variable
         * AJ    - aj in Matrix class object
         * Y     - vector yT transposed in matrix class object
         * R     - r in Matrix class object
         * indxH - current index of xH
         */
        double [][] aj = new double [m][1];
        double [][] aj2 = new double [m][1];

        int indxH = 0;
        double max = -Double.MAX_VALUE;
        
        for (int ind = 0; ind  < n; ind++ ){  
            
            System.out.println("j= " + (xH[ind]+1));
            for (int i=0; i<m; i++){
                for (int j=0; j<n+m; j++){
                    if (j==xH[ind]){
                        aj[i][0] = A[i][j];
                    }
                }
            }
                
            //printing aj
            System.out.print("a" + (xH[ind]+1) + "=");
            for (int i=0; i<m; i++)
                System.out.print(aj[i][0] + " ");
            
            //printing y
            System.out.print("\ny = ");
            for (int i=0; i<m; i++)
                System.out.print(yT[i][0] + " ");
                
            //vector y
            double [][] r = new double[1][1];
                
            Matrix AJ = new Matrix(aj);
            Matrix Y = new Matrix(yT);
            Y = Y.transpose();
            Matrix R = new Matrix(r);
                
            R = Y.times(AJ);
            r = R.getArray();
            
            System.out.println();
            
            if ((c[xH[ind]]-r[0][0]) > 0){ //ako ne biva, smeni go u >=
                System.out.println("Solution: c" + (xH[ind]+1) + " - y*a" +
                        (xH[ind]+1) + " = " + c[xH[ind]] + " - " + r[0][0] + 
                                       " = " + (c[xH[ind]]-r[0][0]) + " > 0");
                flag = true;
                if (c[xH[ind]] > max){
                    max = c[xH[ind]];
                    indxH = ind;
                    for (int i=0; i<m; i++)
                        for (int j=0; j<n+m; j++)
                            if (j==xH[ind])
                                aj2[i][0] = A[i][j];
                }
            }
            else{
                    System.out.println("Solution: c" + (xH[ind]+1) + " - y*a" + 
                           (xH[ind]+1) + " = " + c[xH[ind]] + " - " + r[0][0] + 
                                       " = " + (c[xH[ind]]-r[0][0]) + " < 0");
                    System.out.println("x"+(xH[ind]+1) + 
                                            " does not enter the base");
            }
            System.out.println();
            
            
        }
        
        if (flag == false)
        {
            System.out.println("The current solution is the optimal solution");
            etat = state.OPTIMAL;
            return false;
        }
        
        System.out.println("The biggest value is c[" + (indxH+1) + "]=" + max);
        System.out.println("x" + (xH[indxH]+1) + " enters the base");        
        /*
         * Etape 3
         * d - values of d1, d2, ... , dm
         */
        Matrix matrixaj = new Matrix(aj2);
        double [][] d = new double [m][1];      
        Matrix matrixd = new Matrix(d);
        matrixd = matrixB.solve(matrixaj);
        d = matrixd.getArray();
        
        System.out.print("d=");
        for (int i=0; i<m; i++)
            System.out.print(d[i][0] + " ");
        
        /*
         * Etape 4
         * t   - array of xBetoile / d
         * min - the smallest member of t
         * ft  - flag for t
         */
        double t [] = new double [m];
        boolean ft = false;
        for (int i=0; i<m; i++){
            if (xBetoile[i]>=0 && d[i][0]>0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t <= " + t[i] + " ");
                if (t[i]>=0) 
                    ft = true;
            }
            else if (xBetoile[i]>=0 && d[i][0]<0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t >= " + t[i] + " ");
            }
            else if (xBetoile[i]<=0 && d[i][0]>0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t <= " + t[i] + " ");
                if (t[i]>=0)
                    ft = true;
            }
            else if (xBetoile[i]<=0 && d[i][0]<0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t >= " + t[i] + " ");
            }
            
            System.out.println();
        }
        
        if (ft == false){
            System.out.println("This solution is unbounded!");
            etat = state.NONBORNE;
            return false;
        }
        
        boolean flag2 = false;
        double min = java.lang.Double.MAX_VALUE;
        int ind = 0;
        for (int i=0; i<m; i++){
            if ((xBetoile[i] >= 0 && d[i][0] > 0) || 
                    (xBetoile[i] <= 0 && d[i][0] > 0)){
                if (t[i] >= 0){
                    if (min > t[i]){
                        min = t[i];
                        ind = i;
                        flag2=true;
                    }
                }
            }
        }
        if (flag2==true)
            System.out.println("The biggest value of t is " + min  + " and x" + 
                (xB[ind]+1) + " exit the base");
        else{
            System.out.println("This solution is unbounded! (2)");
            etat = state.NONBORNE;
            return false;
        }
        /*
         * Etape 5: actualization
         * temp - temporary variable 
         */
        
        //actualization of xH and xB
        int temp = xH[indxH];
        xH[indxH] = xB[ind];
        xB[ind] = temp;
                
        //printing the actualized xH
        System.out.print("\nxH (actualized)= ");
        for (int i=0; i<n; i++)
            System.out.print("x" + (xH[i]+1) + " ");
        
        //printing the actualized xB
        System.out.print("\nxB (actualized) = ");
        for (int i=0; i<m; i++)
            System.out.print("x" + (xB[i]+1)+ " ");
        
        //actualization of xBetoile
        for (int i=0; i<m; i++){
            if (i==ind)
                xBetoile[i] = min;
            else
                xBetoile[i] = xBetoile[i] - d[i][0]*min;
        }
        
        //printing the actualized xBetoile
        System.out.print("\nxBetoile (actualized) = ");
        for (int i=0; i<m; i++){
            System.out.print(xBetoile[i] + " ");
        }
        
        //actualization of B
        for (int i=0; i<m; i++)
            for (int j=0; j<m; j++){
                if (j==ind)
                    B[i][j] = aj2[i][0];
            }
        //printing the actualized B
        System.out.println("\nB (actualized)");
        for (int i=0; i<m; i++){
            for (int j=0; j<m; j++)
                System.out.print(B[i][j] + " ");
            System.out.println();
        }
        
        return flag;
    }
    
    //returns primary solution of the LP problem
    public double [] solutionPrimale(){
        double []solution = new double [n];
        for (int i=0; i<n; i++)
            solution[i] = 0.0;
        
        if (etat == state.OPTIMAL){
            for (int i=0; i<n; i++){
                for (int j=0; j<m; j++){
                    if (xB[j]==i)
                        solution[i] = xBetoile[j];
                }
            }
        }
        
        return solution;
    }
    
    public double[] solutionDuale(){
        
        //defining new variable D, which is the constraints of the dual problem
        double D [][] = new double[n][m];
        Matrix Hmat = new Matrix(H);
        D = Hmat.transpose().getArray();
        
        System.out.println("The dual problem is: ");
        
        //printing the objective function (for D)
        System.out.print("Minimise: ");
        for (int i=0; i<m; i++){
            if (i==0){
                if (b[i] != 0)
                    System.out.print(b[i] + "y" + (i+1) + " ");
                else
                    System.out.print("    ");
            }
            
            else{
                if (b[i] > 0)
                    System.out.print(" + " + b[i] + "y" + (i+1) + " ");
                else if (b[i] < 0)
                    System.out.print(b[i] + "y" + (i+1) + " ");
                else if (b[i] == 0)
                    System.out.print("   ");
            }
        }
        
        //printing the constraints
        System.out.println();
        for (int i=0; i<n; i++){
            for (int j=0; j<m; j++){
                if (j==0){
                    if (D[i][j] != 0)
                        System.out.print(D[i][j] + "y" + (j+1) + " ");
                    else
                        System.out.print("   ");
                }
                
                else if (j!=0){
                    if (D[i][j] < 0)
                        System.out.print(D[i][j] + "y" + (j+1) + " ");
                    else if (D[i][j] > 0)
                        System.out.print(" + " + D[i][j] + "y" + (j+1) + " ");
                    else if (D[i][j] == 0)
                        System.out.print("   ");
                }
                
                if (j == (m-1)){
                    System.out.print(" >= " + c[i]);
                }
            }
            System.out.println();
        }
        
        //getting the primary solution
        double []solution = new double [n];
        solution = this.solutionPrimale();
        
        /* new dictionarry
         * nonZeroCounter - counts the non-zeros in the solution 
         * D2 - the new dual dictionary
         */
        int nonZeroCounter = 0;
        for (int i=0; i<m; i++)
            if (xBetoile[i] != 0)
                nonZeroCounter++;
        
        
        
        //initialization for D2 for the nonzero values of solution
        //d2counter
        int d2counter = 0;
        
        //initialization of D2 with all zeros
        double D2[][] = new double [nonZeroCounter][m];
        for (int i=0; i<nonZeroCounter; i++)
            for (int j=0; j<m; j++)
                D2[i][j] = 0;
        
        //c2 - the vector from the right side
        double [] c2 = new double[m];
        for (int j=0; j<m; j++){
            if (xBetoile[j] > 0){
                for (int i=0; i<m; i++){
                    D2[d2counter][i] = A[i][xB[j]];
                }
                
                c2[d2counter] = c[xB[j]];
                d2counter++;
            }
        }
        
        //printing the new dictionary (D2)
        System.out.println("The new D2");
        for (int i=0; i<nonZeroCounter; i++){
            for (int j=0; j<m; j++){
                System.out.print(D2[i][j] + " ");
                if (j==(m-1))
                    System.out.print(" | " + c2[i]);
            }
            System.out.println();
        }
        
        //creating Matrices D2,c2m,y
        Matrix d2 = new Matrix(D2);
        Matrix c2m = new Matrix(c2, m);
        
        Matrix y = d2.solve(c2m);
        y.print(5,3);
        
        double Y [] = y.getRowPackedCopy();
        return Y;
    }
    
    public boolean Bland(){
        //this.initialization();
        boolean flag = false;
        //Etape 1
        
        /* 
         * matrixB as object of the class Matrix; 
         * Bt = matrixB transposee
         */
        Matrix matrixB = new Matrix(B);
        Matrix Bt = matrixB.transpose();
        
        double AA[][] = new double[m][m];
        AA = matrixB.getArray();
        System.out.println("\nPrint Matrix B");
        for (int i=0; i<m; i++){
            for (int j=0; j<m; j++){
                System.out.print(AA[i][j] + " ");
                }
                System.out.println();
        }
          
        /*
         * cT = b transposed
         * yT = y transposed
         * matrixcT - cT as object of the class Matrix
         * matrixyT - yT as object of the class Matrix
         */
        double cT [][] = new double[m][1];
        double yT [][] = new double [m][1];
        
        for (int i=0; i<m; i++){
            cT[i][0] = c[xB[i]];
        }
        for (int i=0; i<m; i++)
            yT[i][0] = 0;
        
        
        Matrix matrixcT = new Matrix(cT);
        Matrix matrixyT = new Matrix(yT);
        
        double barray [][] = new double[1][m];
        barray = matrixcT.getArray();
        System.out.print("cT=");
        for (int i=0; i<m; i++)
            System.out.print(barray[i][0] + " ");
        
        barray = matrixyT.getArray();
        System.out.print("\nyT=");
        for (int i=0; i<m; i++)
            System.out.print(barray[i][0] + " ");
        
        
        matrixyT = Bt.solve(matrixcT);
        yT = matrixyT.getArray();
        for (int i=0; i<m; i++)
            yT[i][0] = yT[i][0];
        
        System.out.println();
        System.out.print("y = ");
        for (int i=0; i<m; i++)
            System.out.print(yT[i][0] + " ");
        System.out.println();
        
        /* Etape 2
         * aj    - the aj-th colone of the matrix A
         * ind   - index for aj (from 0 to n-1)
         * r     - resolution variable
         * AJ    - aj in Matrix class object
         * Y     - vector yT transposed in matrix class object
         * R     - r in Matrix class object
         * indxH - current index of xH
         */
        double [][] aj = new double [m][1];
        int indxH = 0;
        
        for (int ind = 0; ind  < n; ind++ ){  
            
            System.out.println("j= " + (xH[ind]+1));
            for (int i=0; i<m; i++){
                for (int j=0; j<n+m; j++){
                    if (j==xH[ind]){
                        aj[i][0] = A[i][j];
                    }
                }
            }
                
            //printing aj
            System.out.print("a" + (xH[ind]+1) + "=");
            for (int i=0; i<m; i++)
                System.out.print(aj[i][0] + " ");
                
            //vector y
            double [][] r = new double[1][1];
                
            Matrix AJ = new Matrix(aj);
            Matrix Y = new Matrix(yT);
            Y = Y.transpose();
            Matrix R = new Matrix(r);
                
            R = Y.times(AJ);
            r = R.getArray();
            
            System.out.println();
            if ((c[xH[ind]]-r[0][0]) > 0){ //zaebaniot uslov
                System.out.println("Solution: c" + (xH[ind]+1) + " - y*a" + 
                        (xH[ind]+1) + " = " + (c[xH[ind]]-r[0][0]) + " > 0");
                System.out.println("x" + (xH[ind]+1) + " enters the base");
                flag = true;
                indxH = ind;
                break;
            }
            else{
                    System.out.println("Solution: c" + (xH[ind]+1) + " - y*a" + 
                           (xH[ind]+1) + " = " + (c[xH[ind]]-r[0][0]) + " < 0");
                    System.out.println("x"+(xH[ind]+1) + 
                                                    " does not enter the base");
                    flag = false;
            }
            System.out.println();
        }
        
        if (flag == false)
        {
            System.out.println("The current solution is the optimal solution");
            etat = state.OPTIMAL;
            return false;
        }
        
        
        /*
         * Etape 3
         * d - values of d1, d2, ... , dm
         */
        Matrix matrixaj = new Matrix(aj);
        double [][] d = new double [m][1];      
        Matrix matrixd = new Matrix(d);
        matrixd = matrixB.solve(matrixaj);
        d = matrixd.getArray();
        
        System.out.print("d=");
        for (int i=0; i<m; i++)
            System.out.print(d[i][0] + " ");
        System.out.println();
        
        /*
         * Etape 4
         * t   - array of xBetoile / d
         * min - the smallest member of t
         * ft  - flag for t
         */
        double t [] = new double [m];
        boolean ft = false;
        for (int i=0; i<m; i++){
            if (xBetoile[i]>=0 && d[i][0]>0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t <= " + t[i] + " ");
                if (t[i] >= 0)
                    ft = true;
            }
            else if (xBetoile[i]>=0 && d[i][0]<0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t >= " + t[i] + " ");
            }
            else if (xBetoile[i]<=0 && d[i][0]>0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t <= " + t[i] + " ");
                if (t[i] >= 0) 
                    ft = true;
            }
            else if (xBetoile[i]<=0 && d[i][0]<0){
                t[i] = xBetoile[i]/d[i][0];
                System.out.print("t >= " + t[i] + " ");
            }
            
            System.out.println();
        }
        
        if (ft == false){
            System.out.println("This solution is unbounded!");
            etat = state.NONBORNE;
            return false;
        }
        
        boolean flag2 = false;
        double min = java.lang.Double.MAX_VALUE;
        int ind = 0;
        for (int i=0; i<m; i++){
            if ((xBetoile[i] >= 0 && d[i][0] > 0) || 
                    (xBetoile[i] <= 0 && d[i][0] > 0)){                   
                min = t[i];
                ind = i;
                flag2=true;
                break;
            }
        }
        if (flag2==true)
            System.out.println("The first accecible value of t is " + min  + 
                    " and x" + (xB[ind]+1) + " exit the base");
        else{
            System.out.println("This solution is unbounded! (2)");
            etat = state.NONBORNE;
            return false;
        }
        /*
         * Etape 5: actualization
         * temp - temporary variable 
         */
        
        //actualization of xH and xB
        int temp = xH[indxH];
        xH[indxH] = xB[ind];
        xB[ind] = temp;
                
        //printing the actualized xH
        System.out.print("\nxH (actualized)= ");
        for (int i=0; i<n; i++)
            System.out.print("x" + (xH[i]+1) + " ");
        
        //printing the actualized xB
        System.out.println("\nxB (actualized)= ");
        for (int i=0; i<m; i++)
            System.out.print("x" + (xB[i]+1) + " ");
        
        //actualization of xBetoile
        for (int i=0; i<m; i++){
            if (i==ind)
                xBetoile[i] = min;
            else
                xBetoile[i] = xBetoile[i] - d[i][0]*min;
        }
        
        //printing the actualized xBetoile
        System.out.print("\nxBetoile (actualized)= ");
        for (int i=0; i<m; i++){
            System.out.print(xBetoile[i] + " ");
        }
        
        //actualization of B
        for (int i=0; i<m; i++)
            for (int j=0; j<m; j++){
                if (j==ind)
                    B[i][j] = aj[i][0];
            }
        //printing the actualized B
        System.out.println("\nB (actualized)");
        for (int i=0; i<m; i++){
            for (int j=0; j<m; j++)
                System.out.print(B[i][j] + " ");
            System.out.println();
        }
        
        return flag;
    }
    
    public int n(){
        return n;
    }
    
    public int m(){
        return m;
    }
    
    public double epsilon(){
        return epsilon;
    }
    
    public int etat(){
        int e = 0;
        switch (etat){
            case INITIAL: e = 1; break;
            case INFAISABLE: e = 2; break;
            case NONBORNE: e = 3; break;
            case OPTIMAL: e = 4; break;
        }
        
        return e;
    }
    //NE E CELO!!!!
    public void defA(int i, int j, double aij){
        A[i][j] = aij;
    }
    
    //NE E CELO!!!!
    public void defb(int i, double bi){
        b[i] = bi;
    }
    
    //NE E CELO!!!!
    public void defc(int i, double ci){
        b[i] = ci;
    }
    
    public void defEpsilon(double eps){
        epsilon = eps;
    }
    
    public void printH(){
        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++)
                System.out.print(H[i][j] + " ");
            System.out.println();
        }
    }
    
    public void printA(){
        for (int i=0; i<m; i++){
            for (int j=0; j<n+m; j++)
                System.out.print(A[i][j] + " ");
            System.out.println();
        }
    }
    
    public void printb(){
        for (int i=0; i<m; i++)
            System.out.print(b[i] + " ");
    }
    
    public void printcH(){
        for (int i=0; i<n; i++)
            System.out.print(cH[i] + " ");
    }
    
    public void printc(){
        for (int i=0; i<n+m; i++)
            System.out.print(c[i] + " ");
    }
    
    public void printxB(){
        for (int i=0; i<m; i++){
            System.out.print(xB[i] + " ");
        }
    }
    
    public void printxBetoile(){
        for (int i=0; i<m; i++)
            System.out.print(xBetoile[i] + " ");
    }
    
    public void printB(){
        for (int i=0; i<m; i++){
            for (int j=0; j<m; j++)
                System.out.print(B[i][j] + " ");
            System.out.println();
        }
    }
    
    public void printxH(){
        for (int i=0; i<n; i++){
            System.out.print(xH[i] + " ");
        }
    }
    
    public void printcB(){
        for (int i=0; i<m; i++){
            System.out.print(cB[i] + " ");
        }
    }   
}