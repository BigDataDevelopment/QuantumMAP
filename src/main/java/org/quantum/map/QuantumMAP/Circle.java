package org.quantum.map.QuantumMAP;

class Circle {

    private double x;
    private double y;
    private int radius;

    public Circle(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }
    
    public double calculateDistance(
    		  double longitude1, double latitude1, 
    		  double longitude2, double latitude2) {
    		    double c = 
    		        Math.sin(Math.toRadians(latitude1)) *
    		        Math.sin(Math.toRadians(latitude2)) +
    		            Math.cos(Math.toRadians(latitude1)) *
    		            Math.cos(Math.toRadians(latitude2)) *
    		            Math.cos(Math.toRadians(longitude2) - 
    		                Math.toRadians(longitude1));
    		    c = c > 0 ? Math.min(1, c) : Math.max(-1, c);
    		    return 3959 * 1.609 * 1000 * Math.acos(c);
    		}
    
    public boolean checkInside(Circle circle, double x, double y) {
        return calculateDistance(
            circle.getX(), circle.getY(), x, y
        ) < circle.getRadius();
    } 
        
}
