package com.exercise.AndroidClient;

/**
 * Created by Snehal on 11/16/2016.
 */


import java.util.ArrayList;
import java.util.HashMap;


public class NearestNeighbour{

    public NearestNeighbour(int k){
        ArrayList<NearestNeighbour.DataEntry> data = new ArrayList<NearestNeighbour.DataEntry>();
        data.add(new DataEntry(new double[]{1,	2,	3,	2,	1,	3	}, "Not Fall"));
        data.add(new DataEntry(new double[]{2,	1,	3,	3,	1,	2   }, "Not Fall"));
        data.add(new DataEntry(new double[]{1,	1,	2,	3,	2,	2	}, "Not Fall"));
        data.add(new DataEntry(new double[]{2,	2,	3,	3,	2,	1	}, "Not Fall"));
        data.add(new DataEntry(new double[]{6,	5,	7,	5,	6,	7	}, "Fall"));
        data.add(new DataEntry(new double[]{5,	6,	6,	6,	5,	7	}, "Fall"));
        data.add(new DataEntry(new double[]{5,	6,	7,	5,	7,	6	}, "Fall"));
        data.add(new DataEntry(new double[]{7,	6,	7,	6,	5,	6	}, "Fall"));

        this.classes = new ArrayList<Object>();
        this.k = k;
        this.dataSet = data;

        //Load different classes
        for(DataEntry entry : dataSet){
            if(!classes.contains(entry.getY())) classes.add(entry.getY());
        }
        //Load different classes

    }
    public static void main(String[] args){
        NearestNeighbour nn2 = new NearestNeighbour(3);
        System.out.println("Classified as: "+nn2.classify(new DataEntry(new double[]{7,	6,	5,	5,	6,	7},"Ignore")));
    }
    private int k;
    private ArrayList<Object> classes;
    private ArrayList<DataEntry> dataSet;

    public DataEntry[] getNearestNeighbourType(DataEntry x){
        DataEntry[] retur = new DataEntry[this.k];
        double fjernest = Double.MIN_VALUE;
        int index = 0;
        for(DataEntry tse : this.dataSet){
            double distance = distance(x,tse);
            if(retur[retur.length-1] == null){ //Hvis ikke fyldt
                int j = 0;
                while(j < retur.length){
                    if(retur[j] == null){
                        retur[j] = tse; break;
                    }
                    j++;
                }
                if(distance > fjernest){
                    index = j;
                    fjernest = distance;
                }
            }
            else{
                if(distance < fjernest){
                    retur[index] = tse;
                    double f = 0.0;
                    int ind = 0;
                    for(int j = 0; j < retur.length; j++){
                        double dt = distance(retur[j],x);
                        if(dt > f){
                            f = dt;
                            ind = j;
                        }
                    }
                    fjernest = f;
                    index = ind;
                }
            }
        }
        return retur;
    }

    private static double convertDistance(double d){
        return 1.0/d;
    }

    /**
     * Computes Euclidean distance
     * @param a From
     * @param b To
     * @return Distance
     */
    public static double distance(DataEntry a, DataEntry b){
        double distance = 0.0;
        int length = a.getX().length;
        for(int i = 0; i < length; i++){
            double t = a.getX()[i]-b.getX()[i];
            distance = distance+t*t;
        }
        return Math.sqrt(distance);
    }
    /**
     *
     * @param e Entry to be classifies
     * @return The class of the most probable class
     */
    public Object classify(DataEntry e){
        HashMap<Object,Double> classcount = new HashMap<Object,Double>();
        DataEntry[] de = this.getNearestNeighbourType(e);
        for(int i = 0; i < de.length; i++){
            double distance = NearestNeighbour.convertDistance(NearestNeighbour.distance(de[i], e));
            if(!classcount.containsKey(de[i].getY())){
                classcount.put(de[i].getY(), distance);
            }
            else{
                classcount.put(de[i].getY(), classcount.get(de[i].getY())+distance);
            }
        }
        //Find right choice
        Object o = null;
        double max = 0;
        for(Object ob : classcount.keySet()){
            if(classcount.get(ob) > max){
                max = classcount.get(ob);
                o = ob;
            }
        }

        return o;
    }

    public static class DataEntry{
        private double[] x;
        private Object y;

        public DataEntry(double[] x, Object y){
            this.x = x;
            this.y = y;
        }

        public double[] getX(){
            return this.x;
        }

        public Object getY(){
            return this.y;
        }
    }
}