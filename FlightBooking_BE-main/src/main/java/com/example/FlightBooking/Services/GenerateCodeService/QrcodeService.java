package com.example.FlightBooking.Services.GenerateCodeService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

@Service
public class QrcodeService {
    public byte[] generateQrcodeBytes(String username, String email) throws WriterException, IOException {
        String qrcodeText = username + ":" + email;
        QRCodeWriter qrcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                qrcodeWriter.encode(qrcodeText, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}

