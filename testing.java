package newest;

import java.util.ArrayList;
import java.util.Random;


public class testing {
    private static ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static ArrayList<Vehicle> north = new ArrayList<>(), south = new ArrayList<>(), west = new ArrayList<>(), east = new ArrayList<>();
    static Light nsLights;
    static Light weLights;
    static int normalCount;
    static int normalWT;



    private static void generateVehicles(){
        char[] possibleDirection = {'N','S','W','E'};
        int[] possibleSpeed = {3,5,7};
        int[] possibleSpeedUpdated = {5,7}; //speed can not be 3 if previous vehicle is 5 or 7 or else collision
        int onlyPossibleSpeed = 7; //speed can not be 3,5 if previous vehicle is 7 or else collision
        for(int i = 0; i < 32; i++){
            char direction = possibleDirection[new Random().nextInt(possibleDirection.length)];
            int spd = possibleSpeed[new Random().nextInt(possibleSpeed.length)];
            Vehicle vehicle = new Vehicle(direction,spd);
            if(vehicle.getDirection() == 'N'){
                if(!north.isEmpty()){
                    if(vehicle.getTime() == 3){
                        if(north.get(north.size()-1).getTime() == 5){
                            vehicle.updateTime(possibleSpeedUpdated[new Random().nextInt(possibleSpeedUpdated.length)]);
                        } else if (north.get(north.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    } else if (vehicle.getTime() == 5){
                        if (north.get(north.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    }
                }
                north.add(vehicle);
            } else if (vehicle.getDirection() == 'S'){
                if(!south.isEmpty()){
                    if(vehicle.getTime() == 3){
                        if(south.get(south.size()-1).getTime() == 5){
                            vehicle.updateTime(possibleSpeedUpdated[new Random().nextInt(possibleSpeedUpdated.length)]);
                        } else if (south.get(south.size()-1).getTime() == 7){
                            vehicle.updateTime(7);
                        }
                    } else if (vehicle.getTime() == 5){
                        if (south.get(south.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    }
                }
                south.add(vehicle);
            } else if (vehicle.getDirection() == 'W') {
                if(!west.isEmpty()){
                    if(vehicle.getTime() == 3){
                        if(west.get(west.size()-1).getTime() == 5){
                            vehicle.updateTime(possibleSpeedUpdated[new Random().nextInt(possibleSpeedUpdated.length)]);
                        } else if (west.get(west.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    } else if (vehicle.getTime() == 5){
                        if (west.get(west.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    }
                }
                west.add(vehicle);
            } else {
                if(!east.isEmpty()){
                    if(vehicle.getTime() == 3){
                        if(east.get(east.size()-1).getTime() == 5){
                            vehicle.updateTime(possibleSpeedUpdated[new Random().nextInt(possibleSpeedUpdated.length)]);
                        } else if (east.get(east.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    } else if (vehicle.getTime() == 5){
                        if (east.get(east.size()-1).getTime() == 7){
                            vehicle.updateTime(onlyPossibleSpeed);
                        }
                    }
                }
                east.add(vehicle);
            }
            vehicles.add(i,vehicle);
        }
    }
    private static void generateLights(char directionOfFirstVehicle){
        if(directionOfFirstVehicle == 'N' || directionOfFirstVehicle =='S'){
            nsLights = new Light("ns","green",22);
            weLights = new Light("we","red");
        } else {
            weLights = new Light("we","green",22);
            nsLights = new Light("ns","red");
        }
    }
    private static void calculateNormal(char directionOfFirstVehicle,ArrayList<Vehicle> north, ArrayList<Vehicle> south, ArrayList<Vehicle> west, ArrayList<Vehicle> east) {
        //calculate the vehicle on north side allowed to go within the cycle
        ArrayList<Vehicle> n = new ArrayList<>(north), s = new ArrayList<>(south), w = new ArrayList<>(west), e = new ArrayList<>(east);
        int northGo = 0; // 22 second green
        int northCount = 0, northWT = 0, nwt = 0;

        while(!n.isEmpty() && northGo < 22 && (northGo + n.get(0).getTime()) <= 30) {
                northCount+=1;
                northWT += nwt;
                nwt += n.get(0).getTime();
                northGo += n.get(0).getTime();
                n.remove(0); //passed intersection
        }
        int southGo = 0; // 22 second green
        int southCount = 0, southWT = 0, swt = 0;
        while(!s.isEmpty() && southGo < 22 && (southGo + s.get(0).getTime()) <= 30) {
                southCount+=1;
                southWT += swt;
                swt += s.get(0).getTime();
                southGo += s.get(0).getTime();
                s.remove(0); //passed intersection
        }
        int westGo = 0; // 22 second green
        int westCount = 0, westWT = 0, wwt = 0;
        while(!w.isEmpty() && westGo < 22 && (westGo + w.get(0).getTime()) <= 30) {
                westCount+=1;
                westWT += wwt;
                wwt += w.get(0).getTime();
                westGo += w.get(0).getTime();
                w.remove(0); //passed intersection
        }
        int eastGo = 0; // 22 second green
        int eastCount = 0, eastWT = 0, ewt = 0;
        while(!e.isEmpty() && eastGo < 22 && (eastGo + e.get(0).getTime()) <= 30) {
                eastCount+=1;
                eastWT += ewt;
                ewt += e.get(0).getTime();
                eastGo += e.get(0).getTime();
                e.remove(0); //passed intersection
        }
        normalWT = northWT + southWT + westWT + eastWT;
        if (directionOfFirstVehicle == 'N' || directionOfFirstVehicle == 'S'){
            // account for the wait time on opposing side, all vehicle waiting 30 seconds
            normalWT += ((westCount + eastCount) * 30);
        } else {
            normalWT += ((northCount + southCount) * 30);
        }
        normalCount = northCount + southCount + westCount + eastCount;
        System.out.println(normalCount);
        System.out.println(normalWT);
    }
    private static String longerLine(ArrayList<Vehicle> north, ArrayList<Vehicle> south, ArrayList<Vehicle> west, ArrayList<Vehicle> east){
        int nsVehicles = north.size() + south.size();
        int weVehicles = west.size() + east.size();
        if (nsVehicles >= weVehicles){
            return "NS";
        } else {
            return "WE";
        }
    }

    private static void calculateSmart(ArrayList<Vehicle> north, ArrayList<Vehicle> south, ArrayList<Vehicle> west, ArrayList<Vehicle> east) {
        ArrayList<Vehicle> n = new ArrayList<>(north), s = new ArrayList<>(south), w = new ArrayList<>(west), e = new ArrayList<>(east);
        int cycle = 0, cpsTP = 0, cpsWT = 0, nWT = 0, sWT = 0, wWT = 0, eWT = 0;
        if (n.size() + s.size() == w.size() + e.size()) {
            calculateNormal(vehicles.get(0).getDirection(),n,s,w,e);
        } else {
            int prevLonger;
            if (longerLine(n, s, w, e).equalsIgnoreCase("ns")) {
                prevLonger = 0;
            } else {
                prevLonger = 1;
            }
            while (cycle <= 53) {

                if (longerLine(n,s,w,e).equalsIgnoreCase("ns")) {
                    if (prevLonger == 1) {
                        cycle += 3;
                    }
                    if (!n.isEmpty() && !s.isEmpty()) {
                        Vehicle nVehicle = n.get(0), sVehicle = s.get(0);
                        cpsTP += 2;
                        if (prevLonger == 1) {
                            cpsWT += (2 * (Math.max(wWT, eWT) + 3));
                            nWT = Math.max(wWT, eWT) + 3;
                            sWT = Math.max(wWT, eWT) + 3;
                        } else {
                            cpsWT += nWT + sWT;
                        }
                        nWT += nVehicle.getTime();
                        sWT += sVehicle.getTime();
                        cycle += Math.max(nVehicle.getTime(), sVehicle.getTime());
                        n.remove(0);
                        s.remove(0);
                    } else if (!n.isEmpty()) {
                        Vehicle nVehicle = n.get(0);
                        cpsTP += 1;
                        if (prevLonger == 1) {
                            cpsWT += (Math.max(wWT, eWT) + 3);
                            nWT = Math.max(wWT, eWT) + 3;
                        } else {
                            cpsWT += nWT;
                        }
                        nWT += nVehicle.getTime();
                        cycle += nVehicle.getTime();
                        n.remove(0);
                    } else {
                        Vehicle sVehicle = s.get(0);
                        cpsTP += 1;
                        if (prevLonger == 1) {
                            cpsWT += (Math.max(wWT, eWT) + 3);
                            sWT = Math.max(wWT, eWT) + 3;
                        } else {
                            cpsWT += sWT;
                        }
                        sWT += sVehicle.getTime();
                        cycle += sVehicle.getTime();
                        s.remove(0);
                    }
                    prevLonger = 0;

                } else {
                    if (prevLonger == 0) {
                        cycle += 3;
                    }
                    if (!w.isEmpty() && !e.isEmpty()) {
                        Vehicle wVehicle = w.get(0), eVehicle = e.get(0);
                        cpsTP += 2;
                        if (prevLonger == 0) {
                            cpsWT += (2 * (Math.max(nWT, sWT) + 3));
                            wWT = Math.max(nWT, sWT) + 3;
                            eWT = Math.max(nWT, sWT) + 3;
                        } else {
                            cpsWT += wWT + eWT;
                        }
                        wWT += wVehicle.getTime();
                        eWT += eVehicle.getTime();
                        cycle += Math.max(wVehicle.getTime(), eVehicle.getTime());
                        w.remove(0);
                        e.remove(0);
                    } else if (!w.isEmpty()) {
                        Vehicle wVehicle = w.get(0);
                        cpsTP += 1;
                        if (prevLonger == 0) {
                            cpsWT += ((Math.max(nWT, sWT) + 3));
                            wWT = Math.max(nWT, sWT) + 3;
                        } else {
                            cpsWT += wWT;
                        }
                        wWT += wVehicle.getTime();
                        cycle += wVehicle.getTime();
                        w.remove(0);
                    } else {
                        Vehicle eVehicle = e.get(0);
                        cpsTP += 1;
                        if (prevLonger == 0) {
                            cpsWT += ((Math.max(nWT, sWT) + 3));
                            eWT = Math.max(nWT, sWT) + 3;
                        } else {
                            cpsWT += eWT;
                        }
                        eWT += eVehicle.getTime();
                        cycle += eVehicle.getTime();
                        e.remove(0);
                    }
                    prevLonger = 1;
                }


            }

            System.out.println(cpsTP);
            System.out.println(cpsWT);


        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            generateVehicles();

            for (Vehicle v : vehicles) {
                System.out.println(v.toString());
            }

            System.out.println();
            calculateNormal(vehicles.get(0).getDirection(), north, south, west, east);
            calculateSmart(north, south, west, east);
            System.out.println();
            vehicles.clear();north.clear();south.clear();west.clear();east.clear();
        }
    }
    }
