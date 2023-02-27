package com.TelegramBots.telegrambots.Search;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WhoIs {

    public static StringBuilder getInfo(String domainName) throws UnsupportedEncodingException {
        String API_URL = "https://www.whoisxmlapi.com/whoisserver/WhoisService";
        String apiKey = "at_BbhmuqTMGrn2KHG8sKILSjDvvXu0t";
        String url = API_URL
                     + "?domainName=" + URLEncoder.encode(domainName, StandardCharsets.UTF_8)
                     + "&apiKey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        CloseableHttpClient httpclient = null;

        String responseBody = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);

            // Create a response handler
            ResponseHandler<String> responseHandler =
                    new BasicResponseHandler();

            responseBody = httpclient.execute(httpget, responseHandler);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseBody));

            Document doc = db.parse(is);


            String rawText = "Основные сведения: ✔️ " + doc.getElementsByTagName("strippedText").item(0).getTextContent() + "\n ";

            stringBuilder.append(rawText);

            var hostNames = doc.getElementsByTagName("hostNames");

            for (int i = 0; i < hostNames.getLength(); i++) {
                String address = doc.getElementsByTagName("Address").item(0).getTextContent();
                String hostingName = "Хостинг: ✔️" + address + "\n ";
                stringBuilder.append(hostingName);
            }


            var registryData = doc.getElementsByTagName("registryData");
            for (int i = 0; i < registryData.getLength(); i++) {
                String crateDate = "Дата создания: ✔️" + doc.getElementsByTagName("createdDate").item(1).getTextContent();
                stringBuilder.append(crateDate);

            }


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }


}

