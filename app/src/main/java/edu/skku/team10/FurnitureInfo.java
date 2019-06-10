package edu.skku.team10;

import java.io.Serializable;

public class FurnitureInfo implements Serializable {
    public int furnitureID;
    public int furnitureImgID;
    public String furnitureName;
    public String ImgFileName;
    public int furniturePrice;
    public float scaleX;
    public float scaleY;
    public boolean isOnGround;
    public String Description;
    public int z_index;

    public FurnitureInfo(){}

    public FurnitureInfo(int furnitureID, int furnitureImgID){
        this.furnitureID = furnitureID;
        this.furnitureImgID = furnitureImgID;
    }
}
