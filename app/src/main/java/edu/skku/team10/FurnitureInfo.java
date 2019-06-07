package edu.skku.team10;

public class FurnitureInfo {
    public int furnitureID;
    public int furnitureImgID;
    public String furnitureName;
    public String ImgFileName;
    public int furniturePrice;

    public FurnitureInfo(){}

    public FurnitureInfo(int furnitureID, int furnitureImgID){
        this.furnitureID = furnitureID;
        this.furnitureImgID = furnitureImgID;
    }
}
