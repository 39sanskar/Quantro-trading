package com.sanskar.service;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.sanskar.domain.PaymentMethod;
import com.sanskar.model.PaymentOrder;
import com.sanskar.model.User;
import com.sanskar.response.PaymentResponse;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean ProccedPaymentOrder (PaymentOrder paymentOrder,
                                 String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink(User user,
                                              Long Amount,
                                              Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user, Long Amount,
                                            Long orderId) throws StripeException;
}
