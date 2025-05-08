package com.example.FlightBooking.Controller.GenerateCode;

import com.example.FlightBooking.Services.GenerateCodeService.QrcodeService;
import com.google.zxing.WriterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@Tag(name = "Generate Code", description = "Apis for generating QR Code & Barcode for user and get points, ...")
public class QrcodeController {
    @Autowired
    private QrcodeService qrcodeService;
    @GetMapping("/generate/generate-qrcode")
    public ResponseEntity<byte[]> generateBarcode(@RequestParam String username, @RequestParam String email) {
        try {
            byte[] imageBytes = qrcodeService.generateQrcodeBytes(username, email);
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.setContentLength(imageBytes.length);
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
