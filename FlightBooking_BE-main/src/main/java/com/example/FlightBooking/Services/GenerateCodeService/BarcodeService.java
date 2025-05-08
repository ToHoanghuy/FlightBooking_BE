package com.example.FlightBooking.Services.GenerateCodeService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

@Service
public class BarcodeService {
    public byte[] generateBarcodeBytes(String username, String email) throws IOException {
        String barcodeText = username + ":" + email;
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 200, 100);  // width x height

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}