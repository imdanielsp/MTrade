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

import us.textrade.connection.DBConnection;
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
import java.io.File;

public class MatchEngine {

    private static final MatchesQueue queue = new MatchesQueue();

    private static Trade findMatchByTitle(){
        return findMatch(
                "SELECT want.id AS WANT_ID, want.user_id AS WANT_BY, want.name AS WANT_TITLE,\n" +
                "  want.isbn AS WANT_ISBN, want.date_posted AS DATE_POSTED,\n" +
                "  have.id AS HAVE_ID, have.user_id AS HAVE_BY, have.name AS HAVE_TITLE,\n" +
                "  have.isbn AS HAVE_ISBN, have.date_posted AS DATE_POSTED\n" +
                "  FROM booktradewant AS want\n" +
                "  JOIN booktradehave AS have ON want.name = have.name\n" +
                "  ORDER BY want.date_posted;"
        );
    }

    private static Trade findMatchByISBN(){
        return findMatch(
                "SELECT want.id AS WANT_ID, want.user_id AS WANT_BY, want.name AS WANT_TITLE,\n" +
                "  want.isbn AS WANT_ISBN, want.date_posted AS DATE_POSTED,\n" +
                "  have.id AS HAVE_ID, have.user_id AS HAVE_BY, have.name AS HAVE_TITLE,\n" +
                "  have.isbn AS HAVE_ISBN, have.date_posted AS DATE_POSTED\n" +
                "  FROM booktradewant AS want\n" +
                "  JOIN booktradehave AS have ON want.isbn = have.isbn\n" +
                "  ORDER BY want.date_posted;"
        );
    }

    private static void deleteFromDatabase(String id, String tableName)
            throws SQLException{
        Connection conn = DBConnection.makeConnection();

        Statement statement = conn.createStatement();

        statement.executeUpdate(
            String.format(
                "DELETE FROM %s WHERE id = '%s'",
                    tableName, id
            )
        );

        DBConnection.closeConnection(conn);
    }

    private static void dropWantBook(String bookID) throws SQLException {
        deleteFromDatabase(bookID, "booktradewant");
    }

    private static void dropHaveBook(String bookID) throws SQLException {
        deleteFromDatabase(bookID, "booktradehave");
    }

    private static Trade findMatch(String query){

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

            statement.close();
            set.close();

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

        DBConnection.closeConnection(conn);

        for(int i = 0; i < listOfPossibleTrade.size(); i++){
            Map<String, String> currentMap = listOfPossibleTrade.get(i);
            for(int j = 1; j < listOfPossibleTrade.size(); j++){
                try {
                    Map<String, String> nextMap = listOfPossibleTrade.get(j);
                    if(currentMap.get("WANT_BY").equals(nextMap.get("HAVE_BY"))){

                        dropHaveBook(currentMap.get("HAVE_ID"));
                        dropHaveBook(nextMap.get("HAVE_ID"));
                        dropWantBook(currentMap.get("WANT_ID"));
                        dropWantBook(nextMap.get("WANT_ID"));

                        return new Trade(
                            currentMap.get("WANT_BY"),
                            currentMap.get("WANT_ISBN"),
                            false,
                            currentMap.get("HAVE_BY"),
                            nextMap.get("HAVE_ISBN"),
                            false,
                            "processing",
                            new Date(new java.util.Date().getTime())
                        );
                    }
                }catch (IndexOutOfBoundsException iob){
                    break;
                }catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }

        DBConnection.closeConnection(conn);
        return null;
    }

    public static void run(){
        Trade newTrade;
        QueueHandler qHandler = new QueueHandler(queue);

        // Check if email config exits
        File config = new File("/usr/local/mtrade/emails/config.xml");
        if(!config.exists() || config.isDirectory()) {
            System.out.println("Email configuration cannot be open or doesn't exists");
            System.exit(0);
        }

        while (true){
            System.out.println("Looking for matches....");

            newTrade = findMatchByISBN();

            if(queue.addTradeToQueue(newTrade)) {
                System.out.println("Match found!");
                System.out.println(newTrade);
            }

            if(newTrade == null) {

                System.out.printf("Trade(s) found: %s%n%n",
                        queue.queueSize());

                qHandler.activateHandler();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

}
