import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.*;

/**
 *
 * @author Gashteovski
 */
public class Main2 {
    public static void main(String args[]){
        GUI g = new GUI();
        g.setVisible(true);
        
        InputStreamReader convert = new InputStreamReader(System.in);
        BufferedReader stdin = new BufferedReader(convert);
        String instr;
        int m = 0; 
        int n = 0;

        //inputing m,n
        try
        {
            System.out.print("m= ");
            instr = stdin.readLine();
            m = new Integer(instr).intValue();
            
            System.out.print("n= ");
            instr = stdin.readLine();
            n = new Integer(instr).intValue();
        }
        catch(IOException e) {}
        
        catch(java.lang.NumberFormatException e){
            System.out.println ("Wrong format! Input integer!");
        }
        
        //creating object PL
        ProgrammeLineaire2 Model = new ProgrammeLineaire2(m, n);
        int etat = Model.etat();
        
        //input of matrix H
        double H[][] = new double [m][n];
        try
        {
            for (int i=0; i<m; i++)
                for (int j=0; j<n; j++)
                {
                    System.out.print("H[" + i + "][" + j + "]=");
                    instr = stdin.readLine();
                    H[i][j] = new Double(instr).doubleValue();
                }
        }
        catch(IOException e) {}
        
        catch(java.lang.NumberFormatException e){
            System.out.println ("Wrong format! Input integer!");
        }
        
        //input b
        double b[] = new double[m];
        try
        {
            for (int i=0; i<m; i++)
                {
                    System.out.print("b[" + i + "]=");
                    instr = stdin.readLine();
                    b[i] = new Double(instr).doubleValue();
                }
        }
        catch(IOException e) {}
        
        catch(java.lang.NumberFormatException e){
            System.out.println ("Wrong format! Input integer!");
        }
        
        double cH[] = new double[n];
        try
        {
            for (int i=0; i<n; i++)
                {
                    System.out.print("cH[" + i + "]=");
                    instr = stdin.readLine();
                    cH[i] = new Double(instr).doubleValue();
                }
        }
        catch(IOException e) {}
        
        catch(java.lang.NumberFormatException e){
            System.out.println ("Wrong format! Input integer!");
        }
        
        Model.defEpsilon(0.001);
        
        System.out.println("\nPrinting the matrixes");
        
        Model.input(H, b, cH);
        System.out.println("\nH:");
        Model.printH();
        
        System.out.print("\nb = ( ");
        Model.printb();
        System.out.print(")");
        
        System.out.print("\ncH:");
        Model.printcH();
        
        System.out.println("\nA: ");
        Model.initA();
        Model.printA();
        
        System.out.print("\nc = ");
        Model.initc();
        Model.printc();
        
        System.out.println("\nxB ");
        Model.initxB();
        Model.printxB();
        
        System.out.println("\nxBetoile");
        Model.initxBetoile();
        Model.printxBetoile();
        
        System.out.println("\nB");
        Model.initB();
        Model.printB();
        
        System.out.println("\nXh ");
        Model.initxH();
        Model.printxH();
        
        System.out.println("\ncB");
        Model.initcB();
        Model.printcB();
        
        Model.resolution();
        etat = Model.etat();
        System.out.println(etat);
        
        double solution [] = new double [n];
        solution = Model.solutionPrimale();
        
         DecimalFormat df = new DecimalFormat("#.###");
        
        System.out.println("---------------------");
        if (etat == 4){
            System.out.print("The optimal solution is: (");
            for (int i=0; i<n; i++){
                System.out.print(df.format(solution[i]) + " ");
                if (i<n-1)
                    System.out.print(", ");
                else
                    System.out.print(")");
            }
        }
        
        else if (etat == 1)
            System.out.println ("The solution is initial!");
        else if (etat == 2)
            System.out.println("The solution is infeasable!");
        else if (etat == 3)
            System.out.println("The solution is unbounded!");
        
        
        System.out.println("\n=============================");
        
        if (etat==4){
            double y[] = Model.solutionDuale();
               
            System.out.print("The dual solution is: y=(");
            for (int i=0; i<y.length; i++){
                System.out.print(df.format(y[i]) + " ");
                if (i<y.length-1)
                    System.out.print(", ");
                else
                    System.out.print(")");
            }
        
            System.out.println();
        }
    }
}
