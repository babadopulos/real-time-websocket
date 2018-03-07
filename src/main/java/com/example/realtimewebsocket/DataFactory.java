package com.example.realtimewebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;


/**
 * @author babadopulos
 */
@Component
public class DataFactory {

    private static final long BITCOIN_REFRESH = 60_000L;

    private long lastBitcoinUpdate = 0l;
    private String lastBitcoinPrice = null;

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 1_000)
    public void randomDataGenerator() {

        TransportMessage transportMessage = new TransportMessage(DataType.RANDOM);
        transportMessage.setDate(new Date());
        String value = String.valueOf(Math.random());
        transportMessage.setData(value);
        transportMessage.setNextUpdate(1l);

        this.template.convertAndSend("/channel/random", transportMessage);

    }


    @Scheduled(fixedRate = 1_000)
    public void bitcoinPriceFromAPI() {
        TransportMessage transportMessage = new TransportMessage(DataType.BITCOIN);
        transportMessage.setDate(new Date());

        if (lastBitcoinPrice != null && System.currentTimeMillis() - lastBitcoinUpdate < BITCOIN_REFRESH) {
            long nextUpdate = (BITCOIN_REFRESH - (System.currentTimeMillis() - lastBitcoinUpdate)) / 1_000L;

            transportMessage.setData(lastBitcoinPrice);
            transportMessage.setNextUpdate(nextUpdate);

        } else {

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");

            HttpEntity<String> httpEntity = new HttpEntity<String>("", requestHeaders);


            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange("https://api.coindesk.com/v1/bpi/currentprice.json", HttpMethod.GET, httpEntity, String.class);

            if (HttpStatus.OK == response.getStatusCode()) {
                lastBitcoinPrice = response.getBody();
                transportMessage.setData(lastBitcoinPrice);
                transportMessage.setNextUpdate(BITCOIN_REFRESH / 1_000L);
            }

            lastBitcoinUpdate = System.currentTimeMillis();
        }
        this.template.convertAndSend("/channel/bitcoin", transportMessage);
    }


}
