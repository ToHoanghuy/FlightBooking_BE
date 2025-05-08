package com.example.FlightBooking.Services.UserService;

import com.example.FlightBooking.Components.Observer.EmailNotificationSubscriber;
import com.example.FlightBooking.Components.Observer.Publisher;
import com.example.FlightBooking.Models.Decorator.Vouchers;
import com.example.FlightBooking.Repositories.Decorator.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObserverService {
    private final Publisher publisher;
    private final VoucherRepository voucherRepository;

    @Autowired
    public ObserverService(EmailNotificationSubscriber emailNotificationSubscriber, VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
        this.publisher = new Publisher();
        publisher.subscribe(emailNotificationSubscriber);
    }


    public void addVoucher(Vouchers voucher) {
        voucherRepository.save(voucher);
        publisher.setMainState(voucher.getId().toString(), "NEW_VOUCHER");
    }
    public Vouchers getVoucherById(Long voucherId) {
        return voucherRepository.findById(voucherId).orElseThrow(() -> new RuntimeException("Voucher not found with id: " + voucherId));
    }
}
