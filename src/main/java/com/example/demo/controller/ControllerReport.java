package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.priorityQueueTask.QueueService;

import org.slf4j.Logger;

@RestController
@RequestMapping("/report")
public class ControllerReport {
    private static final Logger log = LoggerFactory.getLogger(ControllerLog.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @GetMapping("/log")
    public CompletableFuture<ResponseEntity<byte[]>> getLogReport() throws Exception {
        log.info("Received and transfer request for logs report ");
        return queueService
                .addRequestToQueue(URGENT, null, "getLogReport")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "logReport.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });

    }

    @GetMapping("/temperatureAndHumidity")
    public CompletableFuture<ResponseEntity<byte[]>> getTemperatureAndHumidityReport() throws Exception {
        log.info("Received and transfer request for TemperatureAndHumidity report ");
        return queueService
                .addRequestToQueue(URGENT, null, "getTemperatureAndHumidityReport")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "temperatureAndHumidityReport.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });

    }

    @GetMapping("/circuit")
    public CompletableFuture<ResponseEntity<byte[]>> getCircuitReport() throws Exception {
        log.info("Received and transfer request for Circuit report ");
        return queueService
                .addRequestToQueue(URGENT, null, "getCircuitReport")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "circuitReport.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });

    }

    @GetMapping("/airQuality")
    public CompletableFuture<ResponseEntity<byte[]>> getAirQualityReport() throws Exception {
        log.info("Received and transfer request for air quality report ");
        return queueService
                .addRequestToQueue(URGENT, null, "getAirQualityReport")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "AirQualityReport.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/airParticulates")
    public CompletableFuture<ResponseEntity<byte[]>> getAirParticulatresReport() throws Exception {
        log.info("Received and transfer request for air particulates report ");
        return queueService
                .addRequestToQueue(URGENT, null, "getAirParticulatresReport")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "AirParticulatesReport.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/addressTable")
    public CompletableFuture<ResponseEntity<byte[]>> getAddressTable() throws Exception {
        log.info("Received and transfer request for address table");
        return queueService
                .addRequestToQueue(URGENT, null, "getAddressTable")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("inline", "AddressTable.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }
}
