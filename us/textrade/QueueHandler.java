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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import us.textrade.connection.DBConnection;
import us.textrade.models.MatchesQueue;
import us.textrade.models.Trade;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


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
            System.out.println("Sending notification...");
            System.out.println();
            sendNotification(this.queueOfTrades.seeNextTrade());

            this.queueOfTrades.deleteHead();
        }
    }

    public void sendNotification(Trade trade) {
        List<Map<String, String>> users = new ArrayList<>();

        users.add(
                getUserInfo(
                        trade.getUserOne()
                )
        );

        users.add(
                getUserInfo(
                    trade.getUserTwo()
                )
        );

        sendEmail(users);
        System.out.printf("Notification sent successfully....%n%n");
    }

    private Map<String, String> getEmailConfig(){

        Map<String, String> emailConfig = new HashMap<>();
        String xmlPath = "/usr/local/mtrade/emails/config.xml";

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try{
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            dom = documentBuilder.parse(xmlPath);

            emailConfig.put("host",
                    dom.getElementsByTagName("host").item(0).getTextContent()
            );

            emailConfig.put("port",
                    dom.getElementsByTagName("port").item(0).getTextContent()
            );

            emailConfig.put("sender",
                    dom.getElementsByTagName("sender").item(0).getTextContent()
            );

            emailConfig.put("sender-mask",
                    dom.getElementsByTagName("sender-mask").item(0).getTextContent())
            ;

            emailConfig.put("password",
                    dom.getElementsByTagName("password").item(0).getTextContent()
            );

            return emailConfig;

        } catch (ParserConfigurationException | SAXException | IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private String getHTML(){
        String htmlTemplate = "/usr/local/mtrade/emails/found_match_template.html";

        try {
            InputStream ips = new FileInputStream(htmlTemplate);
            InputStreamReader isr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(isr);
            String line;
            String html = "";

            while ((line=br.readLine()) != null){
                html += line;
            }

            return html;
        } catch (FileNotFoundException fnf){
            fnf.printStackTrace();
            return  "<h2>Hey %s we found a match for you!</h2>" +
                    "<a href=\"http://www.textrade.us/dashboard/trade-requests/\">" +
                        "<p>Click here to approve this trade</p>" +
                    "</a><p>This is the default html, no html file found.</p>";

        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }

    private void sendEmail(List<Map<String, String>> users){

        Map<String, String> emailConfig = getEmailConfig();

        String senderWithMask = String.format(
            "%s <%s>", emailConfig.get("sender-mask"),
                emailConfig.get("sender")
        );
        String senderEmail = emailConfig.get("sender");
        String senderPassword = emailConfig.get("password");

        Properties props = new Properties();
        String htmlTemplate = getHTML();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailConfig.get("host"));
        props.put("mail.smtp.port", emailConfig.get("port"));

        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            }
        );

        for(Map<String, String> user : users){
            try {

                Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress(senderWithMask));

                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.get("email")));

                message.setSubject("We found a match!");

                message.setContent(
                        String.format(htmlTemplate, user.get("name")),
                        "text/html");

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, String> getUserInfo(String username){
        Connection conn = DBConnection.makeConnection();
        Map<String, String> userInfo = new HashMap<>();

        try{
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery(
                String.format(
                    "SELECT university_email AS email, first_name AS name FROM user WHERE username = '%s'",
                    username
                )
            );

            set.next();

            userInfo.put("name", set.getString("name"));
            userInfo.put("email", set.getString("email"));

            statement.close();
            set.close();
            return userInfo;
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        DBConnection.closeConnection(conn);
        return null;
    }

}
