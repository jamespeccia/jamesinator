package trades.threads;

import api.BybitClient;
import logger.Logger;
import org.json.JSONObject;
import state.State;

import java.util.TimerTask;

public abstract class TradeThread implements Runnable  {

    protected final State CURRENT_STATE;
    protected final String SYMBOL;
    protected final boolean IS_BUY_ORDER;
    protected TradeThread counterThread;
    protected Class threadClass;

    public TradeThread(final State CURRENT_STATE, final Class threadClass, final String SYMBOL, final boolean IS_BUY_ORDER) {
        this.CURRENT_STATE = CURRENT_STATE;
        this.SYMBOL = SYMBOL;
        this.IS_BUY_ORDER = IS_BUY_ORDER;
        this.threadClass = threadClass;
    }

    public void placeTrade() {
        final double ORDER_PRICE = CURRENT_STATE.getPrice(SYMBOL, State.BYBIT);
        boolean filled = false;
        if(IS_BUY_ORDER)
            filled = threadClass.placeLimitOrder(SYMBOL, "Buy", ORDER_PRICE, ORDER_QUANTITY, false);
        else
            filled = BybitClient.placeLimitOrder(SYMBOL, "Sell", ORDER_PRICE, ORDER_QUANTITY, false);


        if (!filled) {
            Logger.log("Trade order on Bybit failed. Cancelling Phemex trade.");
            counterThread.exitTrade();
            CURRENT_STATE.getJSONObject("in_trade").getJSONObject(State.BTCUSD).put(State.BYBIT, false);
        } else {
            Logger.log("Bybit order successfully placed.");
        }
    };
    public abstract void exitTrade();


    public void setCounterThread(TradeThread counterThread) {
        this.counterThread = counterThread;
    }


    @Override
    public void run() {
        this.placeTrade();
    }
}
