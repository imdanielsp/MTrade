/**
 * Created by dsantos on 1/10/16 for Textrade.
 * The MIT License (MIT)
 * Copyright (c) 2016 Daniel Santos

 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package us.textrade.models;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *  This class is a queue for the trade that match.
 *  NotificationSender will read the trade in this
 *  queue to send notification to users.
 * */
public class MatchesQueue {
    private Queue<Trade> tradesInQueue;

    public MatchesQueue() {
        this.tradesInQueue = new ArrayDeque<Trade>();
    }

    public Queue<Trade> getTradesInQueue() {
        return tradesInQueue;
    }

    public boolean addTradeToQueue(Trade trade) {
        if(trade != null)
            return this.getTradesInQueue().add(trade);
        return false;
    }

    public Trade deleteHead(){
        return !this.tradesInQueue.isEmpty() ?
                this.tradesInQueue.remove() : null;
    }

    public Trade seeNextTrade(){
        return this.getTradesInQueue().peek();
    }

    public int queueSize(){
        return this.getTradesInQueue().size();
    }

    public boolean isEmpty(){
        return this.getTradesInQueue().isEmpty();
    }

    @Override
    public String toString() {
        return "MatchesQueue{" +
                "tradesInQueue=" + tradesInQueue +
                '}';
    }
}
