package bgu.spl.mics.application.passiveObjects;

public class MoneyStatus{
    private final int bookPrice;
    private final boolean isEnoughMoney;

    public MoneyStatus(int price,boolean enough){
        bookPrice=price;
        isEnoughMoney=enough;
    }

    public boolean isEnoughMoney() {
        return isEnoughMoney;
    }

    public int getBookPrice() {
        return bookPrice;
    }

}
