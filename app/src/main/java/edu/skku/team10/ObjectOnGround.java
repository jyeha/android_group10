package edu.skku.team10;

public class ObjectOnGround {
    public float radius;
    public Position position;
    public boolean onlyFood;
    public int furnitureID; // -1 is none
    public int catID; // -1 is none

    public class Position{
        public float x;
        public float y;
    }

    public ObjectOnGround() {
        position = new Position();
        furnitureID = -1;
        catID = -1;
        onlyFood = false;
    }

    public ObjectOnGround(float x, float y) {
        position = new Position();
        position.x = x;
        position.y = y;
        radius = (float)0.05;
        furnitureID = -1;
        catID = -1;
        onlyFood = false;
    }

    public ObjectOnGround(int furnitureID, boolean onlyFood, float x, float y, float radius) {
        this.furnitureID = furnitureID;
        this.onlyFood = onlyFood;
        position = new Position();
        position.x = x;
        position.y = y;
        this.radius = radius;
        catID = -1;
        onlyFood = false;
    }

    public boolean isOnlyFood() {
        return onlyFood;
    }
}
