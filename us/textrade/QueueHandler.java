/**
 * Created by dsantos on 1/12/16 for Textrade.
 * The MIT License (MIT)
 * Copyright (c) 2016 Daniel Santos
 * <p>
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package us.textrade;

import us.textrade.connection.DBConnection;
import us.textrade.models.MatchesQueue;
import us.textrade.models.Trade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

public class QueueHandler {

    private MatchesQueue queueOfTrades;

    public QueueHandler(MatchesQueue queueOfTrades) {
        this.queueOfTrades = queueOfTrades;
    }

    private void uploadTrade() throws SQLException{
        Connection conn = DBConnection.makeConnection();
        Trade newTrade = this.queueOfTrades.seeNextTrade();

        if(newTrade != null) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "INSERT INTO trade " +
                            "(internal_id, user_one_id, user_two_id, user_one_approved, " +
                            "user_two_approved, book_one, book_two, status_id, date) " +
                            "VALUES ('%s', '%s', '%s', 0, 0, '%s', '%s', 'processing', '%s');",
                            newTrade.getInternalID(), newTrade.getUserOne(),
                            newTrade.getUserTwo(), newTrade.getBookOne(),
                            newTrade.getBookTwo(), newTrade.getDateCreated())
            );
            statement.close();
        }
        DBConnection.closeConnection(conn);
    }

    public void activateHandler(){
        while(!this.queueOfTrades.isEmpty()) {

            try {
                this.uploadTrade();
            }catch (Exception e){
                e.printStackTrace();
                return;
            }

            // TODO: Send email notification
            System.out.println("Sending notification!...");

            this.queueOfTrades.deleteHead();
        }
    }

}
