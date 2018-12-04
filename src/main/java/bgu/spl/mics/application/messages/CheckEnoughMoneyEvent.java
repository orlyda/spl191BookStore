package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MoneyCheckResult;
import bgu.spl.mics.application.passiveObjects.MoneyStatus;

public class CheckEnoughMoneyEvent implements Event<MoneyStatus> {
    private final int price;

    public CheckEnoughMoneyEvent(int bookPrice){
        price=bookPrice;
    }

    public int getPrice() {
        return price;
    }
}
