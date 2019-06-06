package edu.skku.team10;

public class objectOnGround {
    private float radius;
    private Position position;
    private boolean onlyFood;
    private int furnitureID; // 0 is none
    private int catID; // 0 is none

    public class Position{
        float x;
        float y;
    }

    public objectOnGround(int furnitureID, boolean onlyFood, float x, float y, float radius) {
        this.furnitureID = furnitureID;
        this.onlyFood = onlyFood;
        position.x = x;
        position.y = y;
        this.radius = radius;
    }

    public void setFurnitureID(int furnitureID) {
        this.furnitureID = furnitureID;
    }

    public void setCatID(int catID) {
        this.catID = catID;
    }

    public int getFurnitureID() {
        return furnitureID;
    }

    public int getCatID() {
        return catID;
    }

    public boolean isOnlyFood() {
        return onlyFood;
    }
}
