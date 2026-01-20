package com.dijul.demo.controller;

import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.service.PayementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name="Payment Controller", description = "Controller for all payment related calls")
public class PaymentController {

    @Autowired
    PayementService payementService;


    @PostMapping("/complete")
    private ResponseEntity<String> payOrder(@RequestBody @Valid OrderPaymentDTO orderId,
                                            @RequestParam(defaultValue ="true") boolean isSuccess) {
        System.out.println(orderId);
        return payementService.payOrder(orderId,isSuccess);

    }
}
