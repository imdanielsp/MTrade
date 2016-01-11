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
package us.textrade;

import us.textrade.BookTradeController;
import us.textrade.connection.DBConnection;
import us.textrade.models.Book;
import us.textrade.models.BookTrade;
import us.textrade.models.MatchesQueue;
import us.textrade.models.Trade;

import java.sql.Connection;
import java.sql.Date;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MatchEngine {
    private final MatchesQueue queue;
    private List<BookTrade> listBookTrade;

    public MatchEngine(){
        this.queue = new MatchesQueue();
        this.listBookTrade = BookTradeController.loadBookTrade();
    }

    private static List<Trade> findMatchByTitle(){
        return findMatch(
                "SELECT want.id AS WANT_ID, want.user_id AS WANT_BY, want.name AS WANT_TITLE,\n" +
                "  want.isbn AS WANT_ISBN, want.date_posted AS DATE_POSTED,\n" +
                "  have.id AS HAVE_ID, have.user_id AS HAVE_BY, have.name AS HAVE_TITLE,\n" +
                "  have.isbn AS HAVE_ISBN, have.date_posted AS DATE_POSTED\n" +
                "  FROM booktradewant AS want\n" +
                "  JOIN booktradehave AS have ON want.name = have.name;"
        );
    }

    private static List<Trade> findMatchByISBN(){
        return findMatch(
                "SELECT want.id AS WANT_ID, want.user_id AS WANT_BY, want.name AS WANT_TITLE,\n" +
                "  want.isbn AS WANT_ISBN, want.date_posted AS DATE_POSTED,\n" +
                "  have.id AS HAVE_ID, have.user_id AS HAVE_BY, have.name AS HAVE_TITLE,\n" +
                "  have.isbn AS HAVE_ISBN, have.date_posted AS DATE_POSTED\n" +
                "  FROM booktradewant AS want\n" +
                "  JOIN booktradehave AS have ON want.isbn = have.isbn;"
        );
    }

    private static List<Trade> findMatch(String query){

        List<Trade> listOfTrades = new ArrayList<>();
        List<Map<String, String>> listOfPossibleTrade = new ArrayList<>();
        Connection conn = DBConnection.makeConnection();

        try{
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery(query);

            while (set.next()){
                Map<String, String> newMap = new TreeMap<>();
                newMap.put("WANT_ID", set.getString("WANT_ID"));
                newMap.put("WANT_BY", set.getString("WANT_BY"));
                newMap.put("WANT_TITLE", set.getString("WANT_TITLE"));
                newMap.put("WANT_ISBN", set.getString("WANT_ISBN"));
                newMap.put("HAVE_ID", set.getString("HAVE_ID"));
                newMap.put("HAVE_BY", set.getString("HAVE_BY"));
                newMap.put("HAVE_TITLE", set.getString("HAVE_TITLE"));
                newMap.put("HAVE_ISBN", set.getString("HAVE_ISBN"));
                listOfPossibleTrade.add(newMap);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

        for(int i = 0; i < listOfPossibleTrade.size(); i++){
            Map<String, String> currentMap = listOfPossibleTrade.get(i);
            for(int j = 1; j < listOfPossibleTrade.size(); j++){
                try {
                    Map<String, String> nextMap = listOfPossibleTrade.get(j);
                    if(currentMap.get("WANT_BY").equals(nextMap.get("HAVE_BY"))){
                        listOfTrades.add(
                            new Trade(
                                currentMap.get("WANT_BY"),
                                currentMap.get("WANT_ISBN"),
                                false,
                                currentMap.get("HAVE_BY"),
                                nextMap.get("HAVE_ISBN"),
                                false,
                                "processing",
                                new Date(new java.util.Date().getTime())
                            )
                        );
                        listOfPossibleTrade.remove(j);
                        listOfPossibleTrade.remove(i);
                        i = 0;
                        break;
                    }
                }catch (IndexOutOfBoundsException iob){
                    break;
                }
            }
            i--;
        }
        DBConnection.closeConnection(conn);

        return listOfTrades;
    }

    public static void run(){
        MatchesQueue queue = new MatchesQueue();
        List<Trade> listOfTrade;

        while (true){
            listOfTrade = findMatchByTitle();

            for(Trade trade : listOfTrade){
                if(queue.addTradeToQueue(trade));
                    System.out.println("New Trade added");
            }

            try {
                Thread.sleep(10000);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        MatchEngine.run();
    }

}
