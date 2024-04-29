/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fisheyematlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.lang.Math;
import java.math.BigDecimal;

/**
 *
 * @author super
 */
public class FishEyeMatlock {
    
    static double[][] matMult(double[][] m1, double[][] m2, int fil1, int col2, int col1){
        double[][] mat = new double[fil1][col2];
        double temp=0;
        
        for (int i = 0; i < fil1; i++) {
            for (int j = 0; j < col2; j++) {
                temp=0;
                for (int k = 0; k < col1; k++) {
                    temp += m1[i][k]*m2[k][j];
                }
                mat[i][j] = temp;
            }
        }
        
        return mat;
    }
    
    public static Punto[] matlock(Punto[] p, double[][] mat){
        Punto[] p2 = new Punto[p.length];
        for (int i=0; i<p.length; i++) {
             p2[i] = matlock(p[i],mat);
        }
        
        return p2;
    }
    
    public static Punto matlock(Punto p, double[][] mat){
        double[][] m;
        
        m = matMult(p.toMat(), mat, 1, 3, 3);
        Punto p2 = new Punto();
        p2.fromMat(m);
        
        return p2;
    }
    
    
    public static Punto[] loadPuntos(){
        ArrayList<Punto> a = new ArrayList();
        
        File f = new File("puntos.csv");
        
        String[] data;
        String dataLine;
        
        double x,y,t;
        
        try{  
            Scanner myReader = new Scanner(f);
            dataLine = myReader.nextLine();

            while (myReader.hasNextLine()) {
              dataLine = myReader.nextLine();

              data = dataLine.split(",");

              x = Double.parseDouble(data[0]);
              y = Double.parseDouble(data[1]);
              t = Double.parseDouble(data[2]);

              a.add(new Punto(x, y, t));
            }

            myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        
        Punto[] p = new Punto[a.size()];
        int i=0;
        for (Punto punto : a) {
            p[i++] = punto;
        }
        
        return p;
    }
            
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        double[][] m= {{1.0,2.0,3.0},{-2.0,8.0,1.0},{9.0,3.0,5.0}};
        
        double[][] mt = new double[3][3];
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                mt[i][j] = m[i][j];
            }
        }
        
        double[][] mi = MatInverse.invert(mt);

        Punto[] puntos = loadPuntos();
        Punto[] puntosc = matlock(puntos,m);
        Punto[] puntosc2;
        Punto puntoctemp;
        //double[] rs = {0, 1, 10, 50, 100, 500, 100};
        //double[] rt = {0, 0.5, 1, 5, 10};
        double[] rs = {1, 5, 10, 50, 100, 500, 1000};
        double[] rt = {0};
        /*double[] rs = {100}; //Fixed space noise to test time
        double[] rt = {1, 5, 10, 50, 100};
        */
        int[] rk = {1, 10, 100, 1000};
        
        double[][] x = new double[3][3];
        double[][] xi = new double[3][3];
        double[][] xc = new double[3][3];
        double[][] mti = new double[3][3];
        double[] dist = new double[puntos.length];
        double[] distt = new double[puntos.length];
        ArrayList<Result> results = new ArrayList();
        ArrayList<Result> results_all = new ArrayList();
        double max;
        double min;
        double maxt;
        double mint;
        double maxav;
        double minav;
        double maxav25;
        double minav25;
        double maxavt;
        double minavt;
        double maxav10;
        double minav10;
        double maxavt10;
        double minavt10;
        double diffAtI;
        Result tr;
        double tempDist=0;
        int trying =0;
        int i = 350;
        for (double s : rs) {
            for(double t : rt){
                for(int k : rk){
                //System.out.println(""+s+", "+t);
                
                    //System.out.println(""+s+", "+t+", "+i);
                    do{
                        trying++;
                        //System.out.println(trying++);
                        //Obtain original points from path, and obfuscate the location
                        x[0] = puntos[i-k].toVect(s,t);
                        x[1] = puntos[i].toVect(s,t);
                        x[2] = puntos[i+k].toVect(s,t);

                        //Obtain coded points from path
                        xc[0] = puntosc[i-k].toVect();
                        xc[1] = puntosc[i].toVect();
                        xc[2] = puntosc[i+k].toVect();

                        //Calculate the invert of the obfuscated path
                        xi = MatInverse.invert(x);

                        //Calculate the estimates matrix based on obfuscated data and coded data
                        mt = matMult(xi, xc, 3, 3, 3);

                        //Calculate the inverse of the estimated matrix
                        mti = MatInverse.invert(mt);

                        //Calculate matlock for point[i]
                        puntoctemp = matlock(puntosc[i],mti);
                        
                        //Calculate distance between estimated point and original point
                        tempDist = puntoctemp.calcDist(puntos[i]);
                        
                        //Repeat until distance between estimated and original points is less than the maximum expected
                    }while(Double.isNaN(tempDist) || tempDist > (s+1)*Punto.GRAD_TO_MTS);
                    System.out.println(""+s+", "+t+", "+trying);
                    puntosc2 = matlock(puntosc,mti);

                    for (int j = 0; j < puntos.length; j++) {
                        dist[j] = puntosc2[j].calcDist(puntos[j]);
                        distt[j] = puntosc2[j].calcTDist(puntos[j]);
                    }

                    /*if(s == 100 && k==100){
                        for (Double d : dist) {
                            System.out.println(d/Punto.GRAD_TO_MTS);
                        }
                    }*/
                    
                    if(s == 100 && k==1){
                        for (Punto d : puntosc2) {
                            System.out.print(d);
                        }
                    }
                    
                    
                    tr = new Result(s,t,i,k,dist, distt);
                    //System.out.println(tr.getDiffAtI());
                    //}while(Double.isNaN(tr.max) || tr.getDiffAtI()/(360.0/40400000) > s*1000);
                    //}while(Double.isNaN(tr.max) || tr.getDiffAtI() > s/Punto.GRAD_TO_MTS);
                    //if the maximum is not a nan, then add the result
                    //System.out.println(tr.getDiffAt(i));
                    results.add(tr);
                    trying=0;
                        
                
                
                max = results.get(0).max;
                min = results.get(0).min;
                maxt = results.get(0).maxt;
                mint = results.get(0).mint;
                maxav = results.get(0).avgs();
                minav = maxav;
                maxav25 = results.get(0).avgs25();
                minav25 = maxav25;
                maxavt = results.get(0).avgt25();
                minavt = maxavt;
                maxav10 = results.get(0).avgs10();
                minav10 = maxav10;
                maxavt10 = results.get(0).avgt10();
                minavt10 = maxavt10;
                diffAtI = results.get(0).getDiffAtI();
                
                
                for (Result result : results) {
                    
                    if(result.max > max){
                        max = result.max;
                    }
                    if(result.min < min){
                        min = result.min;
                    }
                    if(result.maxt > maxt){
                        maxt = result.maxt;
                    }
                    if(result.mint < mint){
                        mint = result.mint;
                    }
                    if(result.avgs() > maxav){
                        maxav = result.avgs();
                    }
                    if(result.avgs() < minav){
                        minav = result.avgs();
                    }
                    if(result.avgs25() > maxav25){
                        maxav25 = result.avgs25();
                    }
                    if(result.avgs25() < minav25){
                        minav25 = result.avgs25();
                    }
                    if(result.avgt25() < minavt){
                        minavt = result.avgt25();
                    }
                    if(result.avgs10() > maxav10){
                        maxav10 = result.avgs();
                        diffAtI = result.getDiffAtI();
                    }
                    if(result.avgs10() < minav10){
                        minav10 = result.avgs10();
                    }
                    if(result.avgt10() > maxavt10){
                        maxavt10 = result.avgt10();
                    }
                    if(result.avgt10() < minavt10){
                        minavt10 = result.avgt10();
                    }
                    
                }
                tr = new Result(s,t,k,i,min,max,mint,maxt,minav,maxav,minav25,maxav25,minavt,maxavt,minav10,maxav10,minavt10,maxavt10,diffAtI);
                results_all.add(tr);
                results.clear();
                }
            }
        }
        
        
        System.out.println("s,t,k,i,max,min,maxav,minav,maxav25,minav25,maxav10,minav10,diffAtI,maxt,mint,maxavt,minavt,maxavt10,minavt10");
        //System.out.println("Resultados: "+results_all.size());
        
        for (int q = 0; q < results_all.size(); q++) {
            System.out.println(results_all.get(q));
        }
        
    }
    
}



class Result{
    ArrayList<Double> diffTotal;
    ArrayList<Double> diff;
    ArrayList<Double> difft;
    ArrayList<Double> diff10;
    ArrayList<Double> difft10;
    double max;
    double min;
    double maxt;
    double mint;
    double maxav;
    double minav;
    double maxav25;
    double minav25;
    double maxavt;
    double minavt;
    double maxav10;
    double minav10;
    double maxavt10;
    double minavt10;
    double diffAtI;
    int i;
    int k;
    double s;
    double t;
    
    public Result(double s, double t, int k, int i, double min, double max, double mint, double maxt,
            double minav, double maxav, double maxav25, double minav25, double minavt, double maxavt,
            double minav10, double maxav10, double minavt10, double maxavt10, double diffAtI){
        this.s = s;
        this.t = t;
        this.i = i;
        this.k = k;
        this.max = max;
        this.min = min;
        this.maxt = maxt;
        this.mint = mint;
        this.maxav = maxav;
        this.minav = minav;
        this.maxav25 = maxav25;
        this.minav25 = minav25;
        this.maxavt = maxavt;
        this.minavt = minavt;
        this.maxav10 = maxav10;
        this.minav10 = minav10;
        this.maxavt10 = maxavt10;
        this.minavt10 = minavt10;
        this.diffAtI = diffAtI; 
    }
    
    public Result(double s, double t, int i, int k, double[] d, double[] dt){
        this.s = s;
        this.t = t;
        this.i = i;
        this.k = k;
        diff = new ArrayList();
        difft = new ArrayList();
        diff10 = new ArrayList();
        difft10 = new ArrayList();
        diffTotal = new ArrayList();
        max = d[0];
        min = d[0];
        maxt = dt[0];
        mint = dt[0];
        diffAtI = d[i];
        for (int j=0; j<d.length; j++) {
            diffTotal.add(d[j]);
            if(j<i+25 && j>i-25){
                diff.add(d[j]);
                difft.add(dt[j]);
                if(j<i+2 && j>i-2){
                    diff10.add(d[j]);
                    difft10.add(dt[j]);
                }
            }
            if(d[j] > max){
                max = d[j];
            }
            if(d[j] < min){
                min = d[j];
            }
            if(dt[j] > maxt){
                maxt = dt[j];
            }
            if(dt[j] < mint){
                mint = dt[j];
            }
        }
    }
    
    double getDiffAtI(){
        return diffAtI;
    }
    
    double avgs(){
        double x = 0; 
        for (double d : diffTotal) {
            x+=d;
        }
        return x/diffTotal.size();
    }
    
    double avgs25(){
        double x = 0; 
        for (double d : diff) {
            x+=d;
        }
        return x/diff.size();
    }
    
    double avgt25(){
        double x = 0;
        for (double d : difft) {
            x+=d;
        }
        return x/difft.size();
    }
    
    double avgs10(){
        double x = 0; 
        for (double d : diff10) {
            x+=d;
        }
        return x/diff10.size();
    }
    
    double avgt10(){
        double x = 0;
        for (double d : difft10) {
            x+=d;
        }
        return x/difft10.size();
    }
    
    public String toString(){
        if(this.diff != null)
            return ""+s+", "+t+", "+k+", "+i+", "+max+", "+min+", "+avgs()+", "+avgs25()+", "+avgt25()+", "+avgs10()+", "+avgt10();
        else
            return ""+s+", "+t+", "+k+", "+i+", "+max+", "+min+", "+maxav+", "+minav+", "+maxav25+", "+minav25+", "+maxav10+", "+minav10+", "+diffAtI+", "+maxt+", "+mint+", "+maxavt+", "+minavt+", "+maxavt10+", "+minavt10;
    }
    
}


class Punto{
    public static double GRAD_TO_MTS = 360.0/40400000;
    public static double PHI = 123;
    double x,y;
    double t;
    static Random rand = new Random();
    static boolean init=false;
    double[] vect;
    double[] vect_ob;
    public Punto(){
        this(0,0,0);
    }

    public Punto(double x, double y, double t){
        this.x = x;
        this.y = y;
        this.t = t;
        vect = new double[3];
        vect_ob = new double[3];
        vect[0] = x;
        vect[1] = y;
        vect[2] = t;
        if(!init){
            rand.setSeed(7235697);
            init=true;
        }
    }

    double getX(){
        return x;
    }

    double getY(){
        return y;
    }

    double getT(){
        return t;
    }

    void setX(double x){
        this.x = x;
    }

    void setY(double y){
        this.y = y;
    }

    void setT(double t){
        this.t = t;
    }

    public double calcDist(double x, double y){
        double dist=0;
        dist = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        return dist;    
    }


    public double calcDist(Punto p){
        return calcDist(p.getX(), p.getY());
    }

    public double calcTDist(Punto p){
        return t - p.getT();
    }

    public double[][] toMat(){
        double[][] m = {{getX(), getY(), getT()}};
        return m;
    }


    public double[] toVect(){
        vect[0] = getX();
        vect[1] = getY();
        vect[2] = getT();
        return vect;
    }

    public Punto generatePinwheel(double rs, double rt){
        double alpha = rand.nextDouble()*360;
        double distance = (alpha%PHI)/PHI*rs;
        
        return new Punto(distance*Math.cos(alpha/360*2*Math.PI)*GRAD_TO_MTS, distance*Math.sin(alpha/360*2*Math.PI)*GRAD_TO_MTS,(2*rand.nextDouble()*rt-rt));
    } 
    
    public double[] toVect(double rs, double rt){

        Punto tp;
        Punto max = new Punto();
        for (int i = 0; i < 4; i++) {
            //tp = new Punto((2*rand.nextDouble()*rs-rs)*GRAD_TO_MTS,(2*rand.nextDouble()*rs-rs)*GRAD_TO_MTS,(2*rand.nextDouble()*rt-rt));
            tp = generatePinwheel(rs, rt);
            if(tp.calcDist(0,0) > max.calcDist(0,0)){
                max = tp;
            }
        }
        
        /*vect_ob[0] = getX()+(2*rand.nextDouble()*rs-rs)*GRAD_TO_MTS;
        vect_ob[1] = getY()+(2*rand.nextDouble()*rs-rs)*GRAD_TO_MTS;
        vect_ob[2] = getT()+(2*rand.nextDouble()*rt-rt);
        */
        vect_ob[0] = getX()+max.getX();
        vect_ob[1] = getY()+max.getY();
        vect_ob[2] = getT()+max.getT();
        return vect_ob;
    }

    public void fromMat(double[][] m){
        setX(m[0][0]);
        setY(m[0][1]);
        setT(m[0][2]);
    }

    public String toString(){
        return ""+x+", "+y+", "+t+"\n";
    }
        
}
