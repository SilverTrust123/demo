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

import com.example.demo.DTO.requestDTO.RequestTimesDTO;
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

    @GetMapping("/pdf/log")
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

    @GetMapping("/pdf/log/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getLogReportBetweenTimes(RequestTimesDTO request)
            throws Exception {
        log.info("Received and transfer request for logs between times report ");
        return queueService
                .addRequestToQueue(URGENT, request, "getLogReportBetweenTimes")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "logReportBetweenTimes.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/pdf/temperatureAndHumidity")
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

    @GetMapping("/pdf/temperatureAndHumidity/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getTemperatureAndHumidityReportBetweenTimes(
            RequestTimesDTO request) throws Exception {
        log.info("Received and transfer request for TemperatureAndHumidity bewteen timesreport ");
        return queueService
                .addRequestToQueue(URGENT, request, "getTemperatureAndHumidityReportBetweenTimes")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "temperatureAndHumidityReportBetweenTimes.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/excel/temperatureAndHumidity")
    public CompletableFuture<ResponseEntity<byte[]>> getTemperatureAndHumidityExcelReport() throws Exception {
        log.info("Received request for TemperatureAndHumidity Excel report");

        return queueService
                .addRequestToQueue(URGENT, null, "generateTemperatureAndHumidityExcelReport")
                .thenApply(excelContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    headers.setContentDispositionFormData("attachment", "temperatureAndHumidityReport.xlsx");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(
                            (byte[]) excelContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/excel/temperatureAndHumidity/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getTemperatureAndHumidityExcelReportBetweenTimes(
            RequestTimesDTO request)
            throws Exception {
        log.info("Received request for TemperatureAndHumidity between times Excel report");

        return queueService
                .addRequestToQueue(URGENT, request, "generateTemperatureAndHumidityExcelReportBetweenTimes")
                .thenApply(excelContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    headers.setContentDispositionFormData("attachment",
                            "temperatureAndHumidityReportBetweenTimes.xlsx");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(
                            (byte[]) excelContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/pdf/circuit")
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

    @GetMapping("/pdf/circuit/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getCircuitReportBetweenTimes(RequestTimesDTO request)
            throws Exception {
        log.info("Received and transfer request for Circuit between times report ");
        return queueService
                .addRequestToQueue(URGENT, request, "getCircuitReportBetweenTimes")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "circuitReportBetweenTimes.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/excel/circuit")
    public CompletableFuture<ResponseEntity<byte[]>> getCircuitExcelReport() throws Exception {
        log.info("Received request for circuit Excel report");

        return queueService
                .addRequestToQueue(URGENT, null, "generateCircuitExcelReport")
                .thenApply(excelContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    headers.setContentDispositionFormData("attachment", "CircuitReport.xlsx");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(
                            (byte[]) excelContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/excel/circuit/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getCircuitExcelReportBetweenTimes(RequestTimesDTO request)
            throws Exception {
        log.info("Received request for circuit Excel report between times");

        return queueService
                .addRequestToQueue(URGENT, request, "generateCircuitExcelReportBetweenTimes")
                .thenApply(excelContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    headers.setContentDispositionFormData("attachment", "CircuitReportBetweenTimes.xlsx");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(
                            (byte[]) excelContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/pdf/airQuality")
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

    @GetMapping("/pdf/airQuality/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getAirQualityReportBetweenTimes(RequestTimesDTO request)
            throws Exception {
        log.info("Received and transfer request for air quality between times report ");
        return queueService
                .addRequestToQueue(URGENT, request, "getAirQualityReportBetweenTimes")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "AirQualityReportBetweenTimes.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/excel/airQuality")
    public CompletableFuture<ResponseEntity<byte[]>> getAirQualityExcelReport() throws Exception {
        log.info("Received request for air quality Excel report");

        return queueService
                .addRequestToQueue(URGENT, null, "generateAirQualityExcelReport")
                .thenApply(excelContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    headers.setContentDispositionFormData("attachment", "airQualityReport.xlsx");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(
                            (byte[]) excelContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/excel/airQuality/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getAirQualityExcelReportBetweenTimes() throws Exception {
        log.info("Received request for air quality Excel report between times");

        return queueService
                .addRequestToQueue(URGENT, null, "generateAirQualityExcelReportBetweenTimes")
                .thenApply(excelContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    headers.setContentDispositionFormData("attachment", "airQualityReportBetweenTimes.xlsx");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(
                            (byte[]) excelContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/pdf/airParticulates")
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

    @GetMapping("/pdf/airParticulates/betweenTimes")
    public CompletableFuture<ResponseEntity<byte[]>> getAirParticulatresReportBetweenTimes(RequestTimesDTO request)
            throws Exception {
        log.info("Received and transfer request for air particulates between times report ");
        return queueService
                .addRequestToQueue(URGENT, request, "getAirParticulatresReportBetweenTimes")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "AirParticulatesReportBetweenTimes.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }

    @GetMapping("/pdf/addressTable")
    public CompletableFuture<ResponseEntity<byte[]>> getAddressTable() throws Exception {
        log.info("Received and transfer request for address table");
        return queueService
                .addRequestToQueue(URGENT, null, "getAddressTable")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "AddressTable.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });
    }
}
