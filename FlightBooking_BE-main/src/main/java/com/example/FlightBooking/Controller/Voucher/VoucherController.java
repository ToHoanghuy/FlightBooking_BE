package com.example.FlightBooking.Controller.Voucher;

import com.example.FlightBooking.Models.Decorator.Vouchers;
import com.example.FlightBooking.Services.CloudinaryService.CloudinaryService;
import com.example.FlightBooking.Services.DecoratorService.VoucherService;
import com.example.FlightBooking.Services.UserService.ObserverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Tag(name = "Voucher Controller", description = ".....")
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private ObserverService observerService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> addVoucher(@RequestParam("Voucher Code") String code,
                                             @RequestParam("Voucher Name") String voucherName,
                                             @RequestParam("Minimum Order") Long minOrder,
                                             @RequestParam("Voucher Discount") Long discountAmount,
                                             @RequestPart("File") MultipartFile file) throws IOException {
        String imgUrl = cloudinaryService.uploadVoucherImage(file);
        Vouchers voucher = new Vouchers();
        voucher.setCode(code);
        voucher.setMinOrder(minOrder);
        voucher.setVoucherName(voucherName);
        voucher.setDiscountAmount(discountAmount);
        voucher.setVoucherImageUrl(imgUrl);
        observerService.addVoucher(voucher);
        return ResponseEntity.ok("Voucher added and emails sent.");
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkVoucher(
            @RequestParam Long voucherId,
            @RequestParam Long orderAmount) {
        boolean isValid = voucherService.checkVoucherMinOrder(voucherId, orderAmount);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/get-by-code")
    public ResponseEntity<Vouchers> getVoucherByCode(@RequestParam String code) {
        Vouchers voucher = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(voucher);
    }



    @GetMapping("/get-by-id")
    public ResponseEntity<Vouchers> getVoucherById(@RequestParam Long id) {
        Vouchers voucher = voucherService.getVoucherById(id);
        return ResponseEntity.ok(voucher);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Vouchers>> getAllVouchers() {
        List<Vouchers> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Vouchers> updateVoucher(@PathVariable Long id, @RequestBody Vouchers voucherDetails) {
        Vouchers updatedVoucher = voucherService.updateVoucher(id, voucherDetails);
        return ResponseEntity.ok(updatedVoucher);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }
}
